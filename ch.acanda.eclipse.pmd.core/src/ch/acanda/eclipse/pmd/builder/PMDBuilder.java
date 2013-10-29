// =====================================================================
//
// Copyright (C) 2012 - 2013, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================
package ch.acanda.eclipse.pmd.builder;

import java.util.Map;

import net.sourceforge.pmd.RuleSets;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.cache.RuleSetsCache;
import ch.acanda.eclipse.pmd.cache.RuleSetsCacheLoader;

/**
 * Builder for PMD enabled projects.
 *
 * @author Philip Graf
 */
public class PMDBuilder extends IncrementalProjectBuilder {

    public static final String ID = "ch.acanda.eclipse.pmd.builder.PMDBuilder";

    private final RuleSetsCache cache = new RuleSetsCache(new RuleSetsCacheLoader(), PMDPlugin.getDefault().getWorkspaceModel());

    @Override
    @SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
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

    void analyze(final IResource resource) {
        if (resource instanceof IFile) {
            final RuleSets ruleSets = cache.getRuleSets(resource.getProject().getName());
            new Analyzer().analyze((IFile) resource, ruleSets, new ViolationProcessor());
        }
    }

    class DeltaVisitor implements IResourceDeltaVisitor {
        @Override
        public boolean visit(final IResourceDelta delta) throws CoreException {
            final IResource resource = delta.getResource();
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:
                case IResourceDelta.CHANGED:
                    analyze(resource);
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
            analyze(resource);
            return true;
        }
    }

}
