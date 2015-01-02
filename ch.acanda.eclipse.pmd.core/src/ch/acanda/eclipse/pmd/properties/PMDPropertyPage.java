// =====================================================================
//
// Copyright (C) 2012 - 2015, Philip Graf
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
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;
import ch.acanda.eclipse.pmd.ui.util.SelectionAdapter;

/**
 * PMD properties page for a project's property dialog.
 *
 * @author Philip Graf
 */
public class PMDPropertyPage extends PropertyPage {

    private final PMDPropertyPageController controller = new PMDPropertyPageController();
    private Button enablePMDCheckbox;
    private Button addRuleSet;

    @Override
    public void setElement(final IAdaptable element) {
        super.setElement(element);
        controller.init((IProject) element.getAdapter(IProject.class));
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NULL);
        final GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        composite.setLayout(layout);

        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        enablePMDCheckbox = new Button(composite, SWT.CHECK);
        enablePMDCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        enablePMDCheckbox.setText("Enable PMD for this project");
        SWTBotID.set(enablePMDCheckbox, SWTBotID.ENABLE_PMD);

        final Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        final Label lblSelectRulesSets = new Label(composite, SWT.NONE);
        lblSelectRulesSets.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        lblSelectRulesSets.setText("Select rules sets");

        final RuleSetConfigurationTable ruleSetConfigurationTable = new RuleSetConfigurationTable(composite, controller.getModel());
        ruleSetConfigurationTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));

        addRuleSet = new Button(composite, SWT.NONE);
        SWTBotID.set(addRuleSet, SWTBotID.ADD);
        setButtonLayoutData(addRuleSet);
        addRuleSet.setText("Add...");
        addRuleSet.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                controller.addRuleSetConfiguration(((Control) e.widget).getShell());
            }
        });

        initDataBindings();

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
        //
        final IObservableValue btnEnablePmdForObserveSelectionObserveWidget = SWTObservables.observeSelection(enablePMDCheckbox);
        final IObservableValue modelPMDEnabledObserveValue = BeansObservables.observeValue(controller.getModel(), "PMDEnabled");
        bindingContext.bindValue(btnEnablePmdForObserveSelectionObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final IObservableValue addObserveEnabledObserveWidget = SWTObservables.observeEnabled(addRuleSet);
        bindingContext.bindValue(addObserveEnabledObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        return bindingContext;
    }
}
