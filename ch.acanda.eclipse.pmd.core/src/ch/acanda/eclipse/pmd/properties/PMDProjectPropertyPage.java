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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;
import ch.acanda.eclipse.pmd.ui.util.SelectionAdapter;

/**
 * PMD properties page for a project's property dialog.
 * 
 * @author Philip Graf
 */
public class PMDProjectPropertyPage extends PropertyPage {
    public PMDProjectPropertyPage() {}
    
    private final PMDProjectPropertyPageController controller = new PMDProjectPropertyPageController();
    private Text ruleSetsConfiguration;
    private Button enablePMDCheckbox;
    private Button browseButton;
    
    @Override
    protected Control createContents(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NULL);
        final GridLayout gl_composite = new GridLayout(3, false);
        gl_composite.marginWidth = 0;
        composite.setLayout(gl_composite);
        
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        enablePMDCheckbox = new Button(composite, SWT.CHECK);
        enablePMDCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        enablePMDCheckbox.setText("Enable PMD for this project");
        SWTBotID.set(enablePMDCheckbox, SWTBotID.PROPERTY_PAGE_ENABLE_PMD);
        
        final Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
        
        final Label rulesetLabel = new Label(composite, SWT.NONE);
        rulesetLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        rulesetLabel.setText("PMD rulesets");
        
        ruleSetsConfiguration = new Text(composite, SWT.BORDER);
        ruleSetsConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ruleSetsConfiguration.setEditable(false);
        
        browseButton = new Button(composite, SWT.NONE);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                controller.browseForRuleSetsConfiguration(getShell());
            }
        });
        browseButton.setText("Browse...");
        
        initDataBindings();
        
        controller.init((IProject) getElement().getAdapter(IProject.class));
        
        return composite;
    }
    
    @Override
    protected void performApply() {
        controller.save();
    }
    
    @Override
    protected void performDefaults() {
        controller.reset();
    }
    
    @Override
    public boolean performOk() {
        if (controller.isValid()) {
            controller.save();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean okToLeave() {
        return controller.isValid();
    }
    
    @Override
    public boolean isValid() {
        return controller.isValid();
    }
    
    private DataBindingContext initDataBindings() {
        final DataBindingContext bindingContext = new DataBindingContext();
        final PMDProjectPropertyPageModel model = controller.getModel();
        //
        final IObservableValue btnEnablePmdForObserveSelectionObserveWidget = SWTObservables.observeSelection(enablePMDCheckbox);
        final IObservableValue modelPMDEnabledObserveValue = BeansObservables.observeValue(model, "PMDEnabled");
        bindingContext.bindValue(btnEnablePmdForObserveSelectionObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final IObservableValue rulesetObserveTextObserveWidget = SWTObservables.observeText(ruleSetsConfiguration, SWT.Modify);
        final IObservableValue modelRuleSetsConfigurationObserveValue = BeansObservables.observeValue(model, "ruleSetsConfiguration");
        bindingContext.bindValue(rulesetObserveTextObserveWidget, modelRuleSetsConfigurationObserveValue, null, null);
        //
        final IObservableValue rulesetObserveEnabledObserveWidget = SWTObservables.observeEnabled(ruleSetsConfiguration);
        bindingContext.bindValue(rulesetObserveEnabledObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final IObservableValue browseButtonObserveEnabledObserveWidget = SWTObservables.observeEnabled(browseButton);
        bindingContext.bindValue(browseButtonObserveEnabledObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        return bindingContext;
    }
}
