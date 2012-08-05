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

package ch.acanda.eclipse.pmd.properties;

import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.preferences.PMDWorkspaceSettings;
import ch.acanda.eclipse.pmd.wizard.AddRuleSetConfigurationWizard;

/**
 * Controller for the PMD project property page.
 * 
 * @author Philip Graf
 */
final class PMDProjectPropertyPageController {
    
    private final PMDProjectPropertyPageModel model;
    private IProject project;
    
    public PMDProjectPropertyPageController() {
        model = new PMDProjectPropertyPageModel();
    }
    
    public PMDProjectPropertyPageModel getModel() {
        return model;
    }
    
    public void init(final IProject project) {
        this.project = project;
        model.init(project);
    }
    
    public void reset() {
        model.reset();
    }
    
    public void save() {
        final PMDWorkspaceSettings workspaceSettings = new PMDWorkspaceSettings(PMDPlugin.getDefault().getPreferenceStore());
        workspaceSettings.setRuleSetConfigurations(model.getConfigurations());
        final PMDProjectSettings projectSettings = new PMDProjectSettings(project);
        projectSettings.setActiveRuleSetConfigurations(model.getActiveConfigurations());
        projectSettings.setPMDEnabled(model.isPMDEnabled());
    }
    
    public boolean isValid() {
        return true;
    }
    
    public void addRuleSetConfiguration(final Shell shell) {
        final AddRuleSetConfigurationWizard wizard = new AddRuleSetConfigurationWizard();
        final WizardDialog dialog = new WizardDialog(shell, wizard);
        final int result = dialog.open();
        if (result == Window.OK && wizard.getRuleSetConfiguration() != null) {
            model.addRuleSetConfiguration(wizard.getRuleSetConfiguration());
            final HashSet<RuleSetConfiguration> activeConfigs = new HashSet<>(model.getActiveConfigurations());
            activeConfigs.add(wizard.getRuleSetConfiguration());
            model.setActiveConfigurations(activeConfigs);
        }
    }
    
    public void removeSelectedConfigurations() {
        model.removeSelectedConfigurations();
    }
    
}
