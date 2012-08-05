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

package ch.acanda.eclipse.pmd.wizard;

import java.nio.file.Paths;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.domain.FileSystemRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.preferences.PMDWorkspaceSettings;

/**
 * Controller for the wizard page to add a new file system rule set configuration.
 * 
 * @author Philip Graf
 */
final class AddFileSystemRuleSetConfigurationWizardPageController {
    
    private final AddFileSystemRuleSetConfigurationWizardPageModel model = new AddFileSystemRuleSetConfigurationWizardPageModel();
    
    public AddFileSystemRuleSetConfigurationWizardPageModel getModel() {
        return model;
    }
    
    public void browseFileSystem(final Shell shell) {
        final FileDialog fileDialog = new FileDialog(shell);
        final String file = fileDialog.open();
        if (file != null) {
            model.setLocation(file);
        }
    }
    
    public RuleSetConfiguration createRuleSetConfiguration() {
        if (model.isValid()) {
            final PMDWorkspaceSettings workspaceSettings = new PMDWorkspaceSettings(PMDPlugin.getDefault().getPreferenceStore());
            final int id = workspaceSettings.getNextRuleSetConfigurationId();
            return new FileSystemRuleSetConfiguration(id, model.getName(), Paths.get(model.getLocation()));
        }
        throw new IllegalStateException("Cannot create RuleSetConfiguration as the model is not valid");
    }
    
}
