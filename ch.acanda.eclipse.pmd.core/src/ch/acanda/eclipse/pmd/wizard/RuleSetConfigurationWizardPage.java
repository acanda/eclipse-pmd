package ch.acanda.eclipse.pmd.wizard;

import org.eclipse.jface.wizard.IWizardPage;

import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;

public interface RuleSetConfigurationWizardPage extends IWizardPage {
    
    RuleSetConfiguration getRuleSetConfiguration();
    
}
