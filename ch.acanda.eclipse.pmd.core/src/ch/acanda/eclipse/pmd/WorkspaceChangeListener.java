// =====================================================================
//
// Copyright (C) 2012 - 2018, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;

import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.WorkspaceModel;
import ch.acanda.eclipse.pmd.repository.ProjectModelRepository;

import com.google.common.base.Optional;

/**
 * This listener watches the workspace for changes and updates the eclipse-pmd workspace model accordingly.
 *
 * @author Philip Graf
 */

final class WorkspaceChangeListener implements IResourceChangeListener {

    private final ProjectModelRepository projectModelRepository;
    private final WorkspaceModel workspaceModel;

    public WorkspaceChangeListener(final WorkspaceModel workspaceModel, final ProjectModelRepository projectModelRepository) {
        this.workspaceModel = workspaceModel;
        this.projectModelRepository = projectModelRepository;
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        try {
            final IResourceDelta delta = event.getDelta();
            if (delta != null) {
                delta.accept(new WorkspaceDeltaVisitor());
            }
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Couldn't react to change in workspace resource '" + event.getResource().getName() + "'.", e);
        }
    }

    private final class WorkspaceDeltaVisitor implements IResourceDeltaVisitor {

        @Override
        public boolean visit(final IResourceDelta delta) {
            if (delta.getResource().getType() == IResource.PROJECT) {
                visitProject(delta, (IProject) delta.getResource());
            }
            return delta.getResource() instanceof IWorkspaceRoot;
        }

        private void visitProject(final IResourceDelta delta, final IProject project) {
            if (delta.getKind() == IResourceDelta.ADDED) {
                // a new project has been created
                addProject(project.getName());

            } else if (delta.getKind() == IResourceDelta.REMOVED) {
                // a project has been deleted
                removeProject(project.getName());

            } else if (delta.getKind() == IResourceDelta.CHANGED && isFlagged(delta, IResourceDelta.OPEN)) {
                // a project has been opened or closed
                if (project.isOpen()) {
                    addProject(project.getName());
                } else {
                    removeProject(project.getName());
                }

            }
        }

        private boolean isFlagged(final IResourceDelta delta, final int flag) {
            return (delta.getFlags() & flag) == flag;
        }

        private void addProject(final String projectName) {
            final Optional<ProjectModel> existingProjectModel = projectModelRepository.load(projectName);
            workspaceModel.add(existingProjectModel.or(new ProjectModel(projectName)));
        }

        /**
         * Removes a project model from the workspace model to release the resources eclipse-pmd doesn't need anymore.
         */
        private void removeProject(final String projectName) {
            workspaceModel.remove(projectName);
        }

    }
}
