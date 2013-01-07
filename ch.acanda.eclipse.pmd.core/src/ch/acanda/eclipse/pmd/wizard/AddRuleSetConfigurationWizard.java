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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;

import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;

public class AddRuleSetConfigurationWizard extends Wizard {
    
    private final AddRuleSetConfigurationController controller;
    private RuleSetConfiguration configuration;
    RuleSetConfigurationWizardPage ruleSetConfigurationWizardPage;
    
    public AddRuleSetConfigurationWizard(final IProject project) {
        controller = new AddRuleSetConfigurationController(project);
        setWindowTitle("Add Rule Set Configuration");
        setNeedsProgressMonitor(false);
    }
    
    @Override
    public boolean performFinish() {
        configuration = controller.createRuleSetConfiguration();
        return true;
    }
    
    public RuleSetConfiguration getRuleSetConfiguration() {
        return configuration;
    }
    
    @Override
    public void addPages() {
        addPage(new SelectConfigurationTypeWizardPage(controller));
        addPage(new AddRuleSetConfigurationWizardPage(controller));
    }
    
    
}
