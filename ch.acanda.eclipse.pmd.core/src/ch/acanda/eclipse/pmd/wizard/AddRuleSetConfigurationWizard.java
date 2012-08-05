package ch.acanda.eclipse.pmd.wizard;

import org.eclipse.jface.wizard.Wizard;

import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;

public class AddRuleSetConfigurationWizard extends Wizard {
    
    private RuleSetConfiguration configuration;
    RuleSetConfigurationWizardPage ruleSetConfigurationWizardPage;
    
    public AddRuleSetConfigurationWizard() {
        setWindowTitle("Add Rule Set Configuration");
        setNeedsProgressMonitor(false);
    }
    
    @Override
    public boolean performFinish() {
        configuration = ((RuleSetConfigurationWizardPage) getContainer().getCurrentPage()).getRuleSetConfiguration();
        return true;
    }
    
    public RuleSetConfiguration getRuleSetConfiguration() {
        return configuration;
    }
    
    @Override
    public void addPages() {
        ruleSetConfigurationWizardPage = new AddFileSystemRuleSetConfigurationWizardPage();
        addPage(ruleSetConfigurationWizardPage);
    }
    
}
