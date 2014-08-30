// =====================================================================
//
// Copyright (C) 2012 - 2014, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.domain.ProjectModel;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * Repository for project models. This implementation stores the project models in configuration files named
 * {@code .eclipse-pmd} in the respective project's root folder.
 * 
 * @author Philip Graf
 */
public class ProjectModelRepository {
    
    private static final String PMD_CONFIG_FILENAME = ".eclipse-pmd";
    
    public void save(final ProjectModel model) {
        checkNotNull(model, "The argument 'model' must not be null.");
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        try {
            final IProject project = workspace.getRoot().getProject(model.getProjectName());
            final IFile configFile = project.getFile(PMD_CONFIG_FILENAME);
            final String config = new ProjectModelSerializer().serialize(model);
            final ByteArrayInputStream source = new ByteArrayInputStream(config.getBytes(ProjectModelSerializer.ENCODING));
            if (configFile.exists()) {
                configFile.setContents(source, true, true, null);
            } else {
                configFile.create(source, true, null);
            }
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Cannot save " + PMD_CONFIG_FILENAME + " in project " + model.getProjectName(), e);
        }
    }
    
    public Optional<ProjectModel> load(final String projectName) {
        checkState(!Strings.isNullOrEmpty(projectName), "The argument 'projectName' must be a valid project name.");
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProject project = workspace.getRoot().getProject(projectName);
        final IFile configFile = project.getFile(PMD_CONFIG_FILENAME);
        Optional<ProjectModel> result = Optional.absent();
        if (configFile.exists()) {
            try {
                result = Optional.of(new ProjectModelSerializer().deserialize(configFile.getContents(true), projectName));
            } catch (IOException | CoreException e) {
                PMDPlugin.getDefault().error("Cannot load " + PMD_CONFIG_FILENAME + " in project " + projectName, e);
            }
        }
        return result;
    }
    
}
