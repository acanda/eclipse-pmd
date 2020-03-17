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

package ch.acanda.eclipse.pmd.wizard;

import java.nio.file.Paths;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.google.common.base.Optional;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.ui.dialog.FileSelectionDialog;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RulesetsFactoryUtils;

/**
 * Controller for the wizard to add a new rule set configuration.
 *
 * @author Philip Graf
 */
final class AddRuleSetConfigurationController {

    private final AddRuleSetConfigurationModel model;
    private final IProject project;

    public AddRuleSetConfigurationController(final IProject project) {
        model = new AddRuleSetConfigurationModel(project);
        this.project = project;
    }

    public AddRuleSetConfigurationModel getModel() {
        return model;
    }

    public void browse(final Shell shell) {
        if (model.isFileSystemTypeSelected()) {
            final FileDialog fileDialog = new FileDialog(shell);
            final String file = fileDialog.open();
            if (file != null) {
                model.setLocation(file);
            }

        } else if (model.isProjectTypeSelected()) {
            final Optional<IResource> resource = browseContainer(shell, project);
            if (resource.isPresent()) {
                model.setLocation(getRelativePath(project, resource.get()));
            }

        } else if (model.isWorkspaceTypeSelected()) {
            final Optional<IResource> optionalResource = browseContainer(shell, project.getWorkspace().getRoot());
            if (optionalResource.isPresent()) {
                final IResource resource = optionalResource.get();
                model.setLocation(resource.getProject().getName() + "/" + resource.getProjectRelativePath().toString());
            }

        } else {
            throw new IllegalStateException();
        }
    }

    private Optional<IResource> browseContainer(final Shell shell, final IContainer container) {
        final FileSelectionDialog dialog = new FileSelectionDialog(shell);
        dialog.setMessage("Choose a PMD rule set configuration:");
        dialog.setValidator(new ISelectionStatusValidator() {
            @Override
            public IStatus validate(final Object[] selection) {
                IStatus result = new Status(IStatus.OK, PMDPlugin.ID, "");
                if (selection.length == 1 && !(selection[0] instanceof IContainer)) {
                    final IResource resource = (IResource) selection[0];
                    final String configuration = resource.getLocation().toOSString();
                    try {
                        RulesetsFactoryUtils.defaultFactory().createRuleSet(configuration);
                    } catch (final RuleSetNotFoundException | IllegalArgumentException e) {
                        // the rule set location is invalid
                        result = new Status(IStatus.WARNING, PMDPlugin.ID, resource.getName()
                                + " is not a valid PMD rule set configuration");
                    }
                } else {
                    result = new Status(IStatus.WARNING, PMDPlugin.ID, "");
                }
                return result;
            }
        });
        dialog.setInput(container);
        if (dialog.open() == Window.OK) {
            return Optional.of((IResource) dialog.getFirstResult());
        }
        return Optional.absent();
    }

    private String getRelativePath(final IContainer container, final IResource resource) {
        return Paths.get(container.getLocationURI()).relativize(Paths.get(resource.getLocationURI())).toString().replace('\\', '/');
    }

    public RuleSetModel createRuleSetModel() {
        if (model.isValid()) {
            return new RuleSetModel(model.getName(), new Location(model.getLocation(), getLocationContext()));
        }
        throw new IllegalStateException("Cannot create RuleSetModel as the view model is not valid");
    }

    private LocationContext getLocationContext() {
        final LocationContext locationContext;

        if (model.isWorkspaceTypeSelected()) {
            locationContext = LocationContext.WORKSPACE;

        } else if (model.isProjectTypeSelected()) {
            locationContext = LocationContext.PROJECT;

        } else if (model.isFileSystemTypeSelected()) {
            locationContext = LocationContext.FILE_SYSTEM;

        } else if (model.isRemoteTypeSelected()) {
            locationContext = LocationContext.REMOTE;

        } else {
            throw new IllegalStateException("Unknown configuration type");
        }

        return locationContext;
    }

}
