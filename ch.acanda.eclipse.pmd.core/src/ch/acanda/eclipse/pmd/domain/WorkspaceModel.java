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

package ch.acanda.eclipse.pmd.domain;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class WorkspaceModel extends DomainModel {

    public static final String PROJECTS_PROPERTY = "projectModels";

    private final Map<String, ProjectModel> projects = new HashMap<>();

    public ImmutableSet<ProjectModel> getProjects() {
        return ImmutableSet.copyOf(projects.values());
    }

    /**
     * Associates the specified project model with the specified name in this workspace model. If there is already a
     * project model associated with the same name, it is replaced by the new project model.
     *
     * @param projectModel The project model.
     */
    public void add(final ProjectModel projectModel) {
        projects.put(projectModel.getProjectName(), projectModel);
        addPropertyElement(PROJECTS_PROPERTY, projectModel);
    }

    /**
     * Returns the project model for a project with the specified name.
     *
     * @param name The name of the project ({@code IProject.getName()}).
     * @return The project model if it was previously added otherwise {@code Optional#absent()}.
     */
    public Optional<ProjectModel> getProject(final String name) {
        return Optional.fromNullable(projects.get(name));
    }

    /**
     * Returns the project model for a project with the specified name. If the project model does not yet exist it will
     * be created and added to the workspace model.
     *
     * @param name The name of the project ({@code IProject.getName()}).
     * @return The project model.
     */
    public ProjectModel getOrCreateProject(final String name) {
        if (!projects.containsKey(name)) {
            add(new ProjectModel(name));
        }
        return projects.get(name);
    }

    /**
     * Removes a project model from the workspace model.
     *
     * @param name The name of the project to remove.
     */
    public void remove(final String name) {
        final ProjectModel removedModel = projects.remove(name);
        removePropertyElement(PROJECTS_PROPERTY, removedModel);
    }

}
