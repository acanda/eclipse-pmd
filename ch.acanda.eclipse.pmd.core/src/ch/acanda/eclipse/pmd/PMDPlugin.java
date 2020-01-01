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

package ch.acanda.eclipse.pmd;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.common.base.Optional;

import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.WorkspaceModel;
import ch.acanda.eclipse.pmd.repository.ProjectModelRepository;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;
import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * @author Philip Graf
 */
public final class PMDPlugin extends AbstractUIPlugin {

    public static final String ID = "ch.acanda.eclipse.pmd.core";

    private static PMDPlugin plugin;

    private WorkspaceModel workspaceModel;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        initWorkspaceModel();
        initPMD();
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void stop(final BundleContext context) throws Exception {
        PMDPluginImages.dispose();
        plugin = null;
        super.stop(context);
    }

    public static PMDPlugin getDefault() {
        return plugin;
    }

    private void initPMD() {
        // The PMD languages are made available as services using the java.util.ServiceLoader facility. The following
        // line ensures the services are loaded using a class loader with access to the different service
        // implementations (i.e. languages).
        LanguageRegistry.getLanguages();
    }

    private void initWorkspaceModel() {
        workspaceModel = new WorkspaceModel();
        final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        final ProjectModelRepository projectModelRepository = new ProjectModelRepository();
        for (final IProject project : projects) {
            final Optional<ProjectModel> model = projectModelRepository.load(project.getName());
            if (model.isPresent()) {
                workspaceModel.add(model.get());
            } else {
                workspaceModel.add(new ProjectModel(project.getName()));
            }
        }
        final IResourceChangeListener workspaceChangeListener = new WorkspaceChangeListener(workspaceModel, projectModelRepository);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(workspaceChangeListener, IResourceChangeEvent.POST_CHANGE);
    }

    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    /**
     * Logs an error message to the platform, i.e. it will be visible in the Error Log view and distributed to the log
     * listeners.
     *
     * @return An error status containing the error message.
     */
    public IStatus error(final String message) {
        return log(IStatus.ERROR, message, null);
    }

    /**
     * Logs an error message and a {@code Throwable} to the platform, i.e. it will be visible in the Error Log view and
     * distributed to the log listeners.
     *
     * @return An error status containing the error message and throwable.
     */
    public IStatus error(final String message, final Throwable throwable) {
        return log(IStatus.ERROR, message, throwable);
    }

    /**
     * Logs a warning message and a {@code Throwable} to the platform, i.e. it will be visible in the Error Log view and
     * distributed to the log listeners.
     *
     * @return A warning status containing the warning message and throwable.
     */
    public IStatus warn(final String message, final Throwable throwable) {
        return log(IStatus.WARNING, message, throwable);
    }

    /**
     * Logs an info message to the platform, i.e. it will be visible in the Error Log view and distributed to the log
     * listeners.
     *
     * @return An info status containing the message.
     */
    public IStatus info(final String message) {
        return log(IStatus.INFO, message, null);
    }

    /**
     * Logs an info message and a {@code Throwable} to the platform, i.e. it will be visible in the Error Log view and
     * distributed to the log listeners.
     *
     * @return An info status containing the message and throwable.
     */
    public IStatus info(final String message, final Throwable throwable) {
        return log(IStatus.INFO, message, throwable);
    }

    private IStatus log(final int severity, final String message, final Throwable throwable) {
        final IStatus status = new Status(severity, ID, message, throwable);
        getLog().log(status);
        return status;
    }

    /**
     * Creates and returns a new image descriptor for an image file located within the PMD plug-in.
     *
     * @param path The relative path of the image file, relative to the root of the plug-in; the path must be legal.
     * @return The image descriptor, or <code>null</code> if no image could be found.
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        return imageDescriptorFromPlugin(ID, path);
    }

}
