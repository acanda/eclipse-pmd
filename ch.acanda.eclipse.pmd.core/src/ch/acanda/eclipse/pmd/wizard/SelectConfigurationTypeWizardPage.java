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

package ch.acanda.eclipse.pmd.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;

public class SelectConfigurationTypeWizardPage extends WizardPage {

    private final AddRuleSetConfigurationController controller;
    private Button workspaceRadioButton;
    private Button projectRadioButton;
    private Button fileSystemRadioButton;
    private Button remoteRadioButton;

    public SelectConfigurationTypeWizardPage(final AddRuleSetConfigurationController controller) {
        super("selectConfigurationType");
        this.controller = controller;
        setTitle("Add Rule Set Configuration");
        setDescription("Select the type of rule set configuration you want to add.");
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout(2, false));
        setControl(container);

        workspaceRadioButton = new Button(container, SWT.RADIO);
        SWTBotID.set(workspaceRadioButton, SWTBotID.WORKSPACE);

        final Label workspaceLabel = new Label(container, SWT.NONE);
        workspaceLabel.setFont(SWTResourceManager.getBoldFont(workspaceLabel.getFont()));
        workspaceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        workspaceLabel.setText("Workspace");
        new Label(container, SWT.NONE);

        final Label workspaceDescription = new Label(container, SWT.WRAP);
        workspaceDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        workspaceDescription.setText("The path to the rule set configuration file is stored relative to the workspace."
                + " Use this type if you have a rule set configuration file that you want to use for several projects in the workspace.");

        projectRadioButton = new Button(container, SWT.RADIO);
        final GridData projectRadioButtonGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        projectRadioButtonGridData.verticalIndent = 10;
        projectRadioButton.setLayoutData(projectRadioButtonGridData);
        SWTBotID.set(projectRadioButton, SWTBotID.PROJECT);

        final Label projectLabel = new Label(container, SWT.NONE);
        projectLabel.setFont(SWTResourceManager.getBoldFont(projectLabel.getFont()));
        final GridData lblProjectResourceGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        lblProjectResourceGridData.verticalIndent = 10;
        projectLabel.setLayoutData(lblProjectResourceGridData);
        projectLabel.setText("Project");
        new Label(container, SWT.NONE);

        final Label projectDescription = new Label(container, SWT.WRAP);
        projectDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        projectDescription.setText("The path to the rule set configuration is stored relative to the project."
                + " Use this type if your projects have their own rule set configuration files.");

        fileSystemRadioButton = new Button(container, SWT.RADIO);
        final GridData fileSystemRadioButtonGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        fileSystemRadioButtonGridData.verticalIndent = 10;
        fileSystemRadioButton.setLayoutData(fileSystemRadioButtonGridData);
        SWTBotID.set(fileSystemRadioButton, SWTBotID.FILE_SYSTEM);

        final Label fileSystemLabel = new Label(container, SWT.NONE);
        fileSystemLabel.setFont(SWTResourceManager.getBoldFont(fileSystemLabel.getFont()));
        final GridData lblWorkspaceResourceGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        lblWorkspaceResourceGridData.verticalIndent = 10;
        fileSystemLabel.setLayoutData(lblWorkspaceResourceGridData);
        fileSystemLabel.setText("File System");
        new Label(container, SWT.NONE);

        final Label fileSystemDescription = new Label(container, SWT.WRAP);
        fileSystemDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        fileSystemDescription.setText("The path to the rule set configuration file is stored absolute."
                + " Use this type if you have a rule set configuration file outside of the workspace that you use for several workspaces.");

        remoteRadioButton = new Button(container, SWT.RADIO);
        final GridData remoteRadioButtonGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        remoteRadioButtonGridData.verticalIndent = 10;
        remoteRadioButton.setLayoutData(remoteRadioButtonGridData);
        SWTBotID.set(remoteRadioButton, SWTBotID.REMOTE);

        final Label remoteLabel = new Label(container, SWT.NONE);
        remoteLabel.setFont(SWTResourceManager.getBoldFont(remoteLabel.getFont()));
        final GridData remoteLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
        remoteLabelGridData.verticalIndent = 10;
        remoteLabel.setLayoutData(remoteLabelGridData);
        remoteLabel.setText("Remote");
        new Label(container, SWT.NONE);

        final Label remoteDescription = new Label(container, SWT.WRAP);
        remoteDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        remoteDescription.setText("An URI to the rule set configuration is stored."
                + " Use this type if you have a rule set configuration file that is only available via an URI.");
        initDataBindings();
    }

    private DataBindingContext initDataBindings() {
        final DataBindingContext bindingContext = new DataBindingContext();
        //
        final IObservableValue workspaceTypeView = WidgetProperties.selection().observe(workspaceRadioButton);
        final IObservableValue workspaceTypeModel = BeanProperties.value("workspaceTypeSelected").observe(controller.getModel());
        bindingContext.bindValue(workspaceTypeView, workspaceTypeModel, null, null);
        //
        final IObservableValue projectTypeView = WidgetProperties.selection().observe(projectRadioButton);
        final IObservableValue projectTypeModel = BeanProperties.value("projectTypeSelected").observe(controller.getModel());
        bindingContext.bindValue(projectTypeView, projectTypeModel, null, null);
        //
        final IObservableValue fileSystemTypeView = WidgetProperties.selection().observe(fileSystemRadioButton);
        final IObservableValue fileSystemTypeModel = BeanProperties.value("fileSystemTypeSelected").observe(controller.getModel());
        bindingContext.bindValue(fileSystemTypeView, fileSystemTypeModel, null, null);
        //
        final IObservableValue observeSelectionRemoteRadioButtonObserveWidget = WidgetProperties.selection().observe(remoteRadioButton);
        final IObservableValue remoteTypeSelectedControllergetModelObserveValue = BeanProperties.value("remoteTypeSelected").observe(
                controller.getModel());
        bindingContext.bindValue(observeSelectionRemoteRadioButtonObserveWidget, remoteTypeSelectedControllergetModelObserveValue, null,
                null);
        //
        return bindingContext;
    }
}
