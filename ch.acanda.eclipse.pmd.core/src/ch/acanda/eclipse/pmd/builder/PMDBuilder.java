// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.cache.RuleSetsCache;
import ch.acanda.eclipse.pmd.cache.RuleSetsCacheLoader;
import net.sourceforge.pmd.RuleSets;

/**
 * Builder for PMD enabled projects.
 *
 * @author Philip Graf
 */
public class PMDBuilder extends IncrementalProjectBuilder {

    public static final String ID = "ch.acanda.eclipse.pmd.builder.PMDBuilder";

    private static final RuleSetsCache CACHE = new RuleSetsCache(new RuleSetsCacheLoader(), PMDPlugin.getDefault().getWorkspaceModel());

    @Override
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor)
            throws CoreException {
        final IProgressMonitor subMonitor = SubMonitor.convert(monitor);
        if (kind == FULL_BUILD) {
            fullBuild(subMonitor);
        } else {
            final IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(subMonitor);
            } else {
                incrementalBuild(delta, subMonitor);
            }
        }
        return null;
    }

    protected void fullBuild(final IProgressMonitor monitor) {
        try {
            getProject().accept(new ResourceVisitor(monitor));
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Could not run a full PMD build", e);
        }
    }

    protected void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
        delta.accept(new DeltaVisitor(monitor));
    }

    void analyze(final IResource resource, final boolean includeMembers, final IProgressMonitor monitor) throws CoreException {
        if (resource instanceof IFile) {
            monitor.setTaskName("PMD analyzing file: " + ((IFile) resource).getName());
            final RuleSets ruleSets = CACHE.getRuleSets(resource.getProject().getName());
            new Analyzer().analyze((IFile) resource, ruleSets, new ViolationProcessor());

        } else if (resource instanceof IFolder && includeMembers) {
            final IFolder folder = (IFolder) resource;
            for (final IResource member : folder.members()) {
                analyze(member, includeMembers, monitor);
            }
        }
    }

    class DeltaVisitor implements IResourceDeltaVisitor {

        private final IProgressMonitor monitor;

        public DeltaVisitor(final IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public boolean visit(final IResourceDelta delta) throws CoreException {
            final IResource resource = delta.getResource();
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:
                case IResourceDelta.CHANGED:
                    analyze(resource, (delta.getFlags() & IResourceDelta.DERIVED_CHANGED) != 0, monitor);
                    break;

                default:
                    break;
            }
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            return true;
        }
    }

    class ResourceVisitor implements IResourceVisitor {

        private final IProgressMonitor monitor;

        public ResourceVisitor(final IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        @Override
        public boolean visit(final IResource resource) throws CoreException {
            analyze(resource, false, monitor);
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            return true;
        }
    }

}
