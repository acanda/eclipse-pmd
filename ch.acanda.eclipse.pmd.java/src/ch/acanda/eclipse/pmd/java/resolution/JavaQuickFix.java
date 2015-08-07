// =====================================================================
//
// Copyright (C) 2012 - 2015, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import com.google.common.base.Optional;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Base class for a Java quick fix.
 *
 * @author Philip Graf
 *
 * @param <T> The type of AST node that will be passed to {@link #apply(ASTNode)}.
 */
public abstract class JavaQuickFix<T extends ASTNode> extends WorkbenchMarkerResolution {

    private static final IMarker[] NO_OTHER_MARKERS = new IMarker[0];
    protected final PMDMarker marker;

    protected JavaQuickFix(final PMDMarker marker) {
        this.marker = marker;
    }

    @Override
    public final Image getImage() {
        return PMDPluginImages.get(getImageDescriptor());
    }

    /**
     * Returns the image descriptor for the image to be displayed in the list of resolutions.
     *
     * @return The image descriptor for the image to be shown. Must not return <code>null</code>.
     */
    abstract protected ImageDescriptor getImageDescriptor();

    /**
     * Returns other markers with the same rule id as the marker of this quick fix. This allows to fix multiple PMD
     * problems of the same type all at once.
     */
    @Override
    @SuppressWarnings("PMD.UseVarargs")
    public IMarker[] findOtherMarkers(final IMarker[] markers) {
        final IMarker[] result;
        if (markers.length > 1) {
            final List<IMarker> otherMarkers = new ArrayList<>(markers.length);
            for (final IMarker other : markers) {
                if (marker.isOtherWithSameRuleId(other)) {
                    otherMarkers.add(other);
                }
            }
            if (otherMarkers.isEmpty()) {
                result = NO_OTHER_MARKERS;
            } else {
                result = otherMarkers.toArray(new IMarker[otherMarkers.size()]);
            }
        } else {
            result = NO_OTHER_MARKERS;
        }
        return result;
    }

    @Override
    public void run(final IMarker[] markers, final IProgressMonitor monitor) {
        final Map<IFile, List<IMarker>> map = createMarkerMap(markers);
        monitor.beginTask(getLabel(), (map.keySet().size() * 2 + markers.length) * 100);
        try {
            for (final Entry<IFile, List<IMarker>> entry : map.entrySet()) {
                fixMarkersInFile(entry.getKey(), entry.getValue(), monitor);
            }
        } finally {
            monitor.done();
        }
    }

    /**
     * @return A map grouping the markers by their file.
     */
    private Map<IFile, List<IMarker>> createMarkerMap(final IMarker[] markers) {
        final Map<IFile, List<IMarker>> markerMap = new HashMap<>();
        for (final IMarker marker : markers) {
            final IResource resource = marker.getResource();
            if (resource instanceof IFile && resource.isAccessible()) {
                final IFile key = (IFile) resource;
                List<IMarker> value = markerMap.get(key);
                if (value == null) {
                    value = new ArrayList<IMarker>();
                    markerMap.put(key, value);
                }
                value.add(marker);
            }
        }
        return markerMap;
    }

    @Override
    public void run(final IMarker marker) {
        final IResource resource = marker.getResource();
        if (resource instanceof IFile && resource.isAccessible()) {
            fixMarkersInFile((IFile) resource, Arrays.asList(marker), new NullProgressMonitor());
        }
    }

    /**
     * Fixes all provided markers in a file.
     *
     * @param markers The markers to fix. There is at least one marker in this collection and all markers can be fixed
     *            by this quick fix.
     */
    protected void fixMarkersInFile(final IFile file, final List<IMarker> markers, final IProgressMonitor monitor) {
        monitor.subTask(file.getFullPath().toOSString());

        final Optional<ICompilationUnit> optionalCompilationUnit = getCompilationUnit(file);

        if (!optionalCompilationUnit.isPresent()) {
            return;
        }

        final ICompilationUnit compilationUnit = optionalCompilationUnit.get();
        ITextFileBufferManager bufferManager = null;
        final IPath path = compilationUnit.getPath();

        try {
            bufferManager = FileBuffers.getTextFileBufferManager();
            bufferManager.connect(path, LocationKind.IFILE, null);

            final ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.IFILE);

            final IDocument document = textFileBuffer.getDocument();
            final IAnnotationModel annotationModel = textFileBuffer.getAnnotationModel();

            final ASTParser astParser = ASTParser.newParser(AST.JLS4);
            astParser.setKind(ASTParser.K_COMPILATION_UNIT);
            astParser.setResolveBindings(needsTypeResolution());
            astParser.setSource(compilationUnit);

            final SubProgressMonitor parserMonitor = new SubProgressMonitor(monitor, 100);
            final CompilationUnit ast = (CompilationUnit) astParser.createAST(parserMonitor);
            parserMonitor.done();

            startFixingMarkers(ast);

            final Map<?, ?> options = compilationUnit.getJavaProject().getOptions(true);
            for (final IMarker marker : markers) {
                try {
                    final MarkerAnnotation annotation = getMarkerAnnotation(annotationModel, marker);
                    // if the annotation is null it means that is was deleted by a previous quick fix
                    if (annotation != null) {
                        final Optional<T> node = getNodeFinder(annotationModel.getPosition(annotation)).findNode(ast);
                        if (node.isPresent()) {
                            final boolean isSuccessful = fixMarker(node.get(), document, options);
                            if (isSuccessful) {
                                marker.delete();
                            }
                        }
                    }
                } finally {
                    monitor.worked(100);
                }
            }

            finishFixingMarkers(ast, document, options);

            // commit changes to underlying file if it is not opened in an editor
            if (!isEditorOpen(file)) {
                final SubProgressMonitor commitMonitor = new SubProgressMonitor(monitor, 100);
                textFileBuffer.commit(commitMonitor, false);
                commitMonitor.done();
            } else {
                monitor.worked(100);
            }

        } catch (CoreException | MalformedTreeException | BadLocationException e) {
            // TODO: log error
            // PMDPlugin.getDefault().error("Error processing quickfix", e);

        } finally {
            if (bufferManager != null) {
                try {
                    bufferManager.disconnect(path, LocationKind.IFILE, null);
                } catch (final CoreException e) {
                    // TODO: log error
                    // PMDPlugin.getDefault().error("Error processing quickfix", e);
                }
            }
        }
    }

    /**
     * Returns {@code true} if the quick fix needs resolved types. Type resolution comes at a considerable cost in both
     * time and space, however, and should not be requested frivolously. The additional space is not reclaimed until the
     * AST, all its nodes, and all its bindings become garbage. So it is very important to not retain any of these
     * objects longer than absolutely necessary.
     *
     * @see ASTParser#setResolveBindings(boolean)
     */
    protected boolean needsTypeResolution() {
        return false;
    }

    /**
     * Prepares the quick fix for fixing the markers. This method is guaranteed to be invoked before
     * {@link #fixMarker(ASTNode, IDocument, Map)} and {@link #finishFixingMarkers(CompilationUnit, IDocument, Map).
     */
    protected abstract void startFixingMarkers(final CompilationUnit ast);

    /**
     * Fixes a single marker. The marker is already resolve to its corresponding node in the AST. This method is
     * guaranteed to be invoked before {@link #finishFixingMarkers(CompilationUnit, IDocument, Map).
     *
     * @param node The marker's corresponding node in the AST.
     * @param document The document representing the Java file.
     * @param options The project's Java options.
     * @return {@code true} iff the quick fix was applied successfully, i.e. the PMD problem was resolved. If
     *         {@code false} is returned then the AST must not have been modified in any way.
     * @throws CoreException Thrown when the AST has already been modified but the fix could not have been successfully
     *             applied. Throwing this exception will abort all quick fixes for this file. Any already successfully
     *             applied quick fixes will not be committed.
     */
    protected abstract boolean fixMarker(final T node, final IDocument document, final Map<?, ?> options) throws CoreException;

    /**
     * Finishes fixing the markers. After this method the document should have its final form before being committed.
     *
     * @param ast The document's AST.
     * @param document The document representing the Java file.
     * @param options The project's Java options.
     * @throws BadLocationException Thrown when the fixing cannot be finished properly. The already applied quick fixes
     *             will not be committed.
     */
    protected abstract void finishFixingMarkers(final CompilationUnit ast, final IDocument document, final Map<?, ?> options)
            throws BadLocationException;

    /**
     * @param position The position of the marker.
     * @return The node finder that will be used to search for the node which will be passed to the quick fix.
     */
    protected abstract NodeFinder<CompilationUnit, T> getNodeFinder(final Position position);

    /**
     * Returns the type of the AST node that will be used to find the node that will be used as an argument when
     * invoking {@link #apply(ASTNode)}. This method takes the type from the type parameter of this class.
     *
     * @return The type of the node that will be used when invoking {@link #apply(ASTNode)}.
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getNodeType() {
        // This works only if 'this' is a direct subclass of ASTQuickFix.
        final ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    // /**
    // * Applies the quick fix to the provided node. The marker's range lies within the node's range and the node's type
    // * is the same as the one returned by {@link #getNodeType()}.
    // *
    // * @return {@code true} iff the quick fix was applied successfully, i.e. the PMD problem was resolved.
    // */
    // protected abstract boolean apply(final T node);

    private Optional<ICompilationUnit> getCompilationUnit(final IFile file) {
        final IJavaElement element = JavaCore.create(file);
        return element instanceof ICompilationUnit ? Optional.of((ICompilationUnit) element) : Optional.<ICompilationUnit>absent();
    }

    private MarkerAnnotation getMarkerAnnotation(final IAnnotationModel annotationModel, final IMarker marker) {
        @SuppressWarnings("unchecked")
        final Iterator<Annotation> annotations = annotationModel.getAnnotationIterator();
        while (annotations.hasNext()) {
            final Annotation annotation = annotations.next();
            if (annotation instanceof MarkerAnnotation) {
                final IMarker annotationMarker = ((MarkerAnnotation) annotation).getMarker();
                if (annotationMarker.equals(marker)) {
                    return (MarkerAnnotation) annotation;
                }
            }
        }
        return null;
    }

    /**
     * @return {@code true} iff an editor for the provided file is open. The editor must not necessarily be visible or
     *         even belong to the active window or perspective.
     */
    private boolean isEditorOpen(final IFile file) {
        for (final IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (final IWorkbenchPage page : window.getPages()) {
                for (final IEditorReference reference : page.getEditorReferences()) {
                    try {
                        final IEditorInput input = reference.getEditorInput();
                        if (input instanceof IFileEditorInput && file.equals(((IFileEditorInput) input).getFile())) {
                            return true;
                        }
                    } catch (final PartInitException e) {
                        // cannot get editor input -> ignore
                    }
                }
            }
        }
        return false;
    }

}
