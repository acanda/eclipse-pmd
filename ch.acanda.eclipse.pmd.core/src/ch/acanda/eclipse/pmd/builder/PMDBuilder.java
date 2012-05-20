// =====================================================================
//
// Copyright (C) 2012, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================
package ch.acanda.eclipse.pmd.builder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.JavaCharStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.properties.PMDProjectSettings;

public class PMDBuilder extends IncrementalProjectBuilder {
    
    public static final String ID = "ch.acanda.eclipse.pmd.builder.PMDBuilder";
    private static final String MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdMarker";
    private static final int PMD_TAB_SIZE = new Tabs().size;
    
    /**
     * Removes all PMD markers from a project and its files.
     */
    public static void removeAllMarkers(final IProject project) throws CoreException {
        project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
    }
    
    @Override
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor)
            throws CoreException {
        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            final IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        return null;
    }
    
    protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
        try {
            getProject().accept(new ResourceVisitor());
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Could not run a full PMD build", e);
        }
    }
    
    protected void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        delta.accept(new DeltaVisitor());
    }
    
    void runPMD(final IResource resource) {
        if (resource instanceof IFile) {
            try {
                final IFile file = (IFile) resource;
                if (!file.isDerived() && file.isAccessible()) {
                    final RuleSets ruleSets = new PMDProjectSettings(file.getProject()).getRuleSets();
                    if (Language.JAVA.hasExtension(file.getFileExtension()) && ruleSets.applies(file.getRawLocation().toFile())) {
                        final PMDConfiguration configuration = new PMDConfiguration();
                        final String content = new String(Files.readAllBytes(file.getRawLocation().toFile().toPath()), file.getCharset());
                        final RuleContext context = PMD.newRuleContext(file.getName(), file.getRawLocation().toFile());
                        context.setLanguageVersion(Language.JAVA.getDefaultVersion());
                        new SourceCodeProcessor(configuration).processSourceCode(new StringReader(content), ruleSets, context);
                        
                        file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
                        final Iterator<RuleViolation> violations = context.getReport().getViolationTree().iterator();
                        while (violations.hasNext()) {
                            final RuleViolation violation = violations.next();
                            addMarker(file, content, violation);
                        }
                    }
                }
            } catch (CoreException | PMDException | IOException e) {
                PMDPlugin.getDefault().error("Could not run PMD on resource " + resource.getFullPath(), e);
            }
        }
    }
    
    private void addMarker(final IFile file, final String content, final RuleViolation violation) {
        try {
            final IMarker marker = file.createMarker(MARKER_TYPE);
            marker.setAttribute(IMarker.MESSAGE, violation.getDescription());
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            marker.setAttribute(IMarker.LINE_NUMBER, violation.getBeginLine());
            final Range range = getAbsoluteRange(content, violation);
            marker.setAttribute(IMarker.CHAR_START, range.getStart());
            marker.setAttribute(IMarker.CHAR_END, range.getEnd());
        } catch (final CoreException e) {
            e.printStackTrace();
        }
    }
    
    private Range getAbsoluteRange(final String content, final RuleViolation violation) {
        final Document document = new Document(content);
        try {
            // violation line and column start at one, the marker's start and end positions at zero
            final int start = getAbsolutePosition(content, document.getLineOffset(violation.getBeginLine() - 1), violation.getBeginColumn());
            final int end = getAbsolutePosition(content, document.getLineOffset(violation.getEndLine() - 1), violation.getEndColumn());
            return new Range(start - 1, end);
        } catch (final BadLocationException e) {
            return new Range(0, 0);
        }
    }
    
    private int getAbsolutePosition(final String content, final int lineOffset, final int pmdCharOffset) {
        int pmdCharCounter = 0;
        int absoluteOffset = lineOffset;
        while (pmdCharCounter < pmdCharOffset) {
            if (absoluteOffset < content.length()) {
                final char c = content.charAt(absoluteOffset);
                if (c == '\t') {
                    pmdCharCounter += PMD_TAB_SIZE;
                } else {
                    pmdCharCounter++;
                }
            } else {
                break;
            }
            absoluteOffset++;
        }
        return absoluteOffset;
    }
    
    class DeltaVisitor implements IResourceDeltaVisitor {
        @Override
        public boolean visit(final IResourceDelta delta) throws CoreException {
            final IResource resource = delta.getResource();
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:
                case IResourceDelta.CHANGED:
                    runPMD(resource);
                    break;
                
                default:
                    break;
            }
            return true;
        }
    }
    
    class ResourceVisitor implements IResourceVisitor {
        @Override
        public boolean visit(final IResource resource) {
            runPMD(resource);
            return true;
        }
    }
    
    private static final class Range {
        private final int start;
        private final int end;
        
        public Range(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getEnd() {
            return end;
        }
    }
    
    /**
     * 
     */
    private static final class Tabs extends JavaCharStream {
        
        public int size = super.tabSize;
        
        public Tabs() {
            super((Reader) null);
        }
        
    }
}
