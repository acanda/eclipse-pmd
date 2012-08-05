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
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.PropertyPage;

import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
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
    private Button enablePMDCheckbox;
    private Button addRuleSetConfiguration;
    private Button removeRuleSetConfiguration;
    private CheckboxTableViewer tableViewer;
    private Table table;
    
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
        
        final Composite tableCompüosite = new Composite(composite, SWT.NONE);
        tableCompüosite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableCompüosite.setLayout(tableColumnLayout);
        
        tableViewer = CheckboxTableViewer.newCheckList(tableCompüosite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        SWTBotID.set(table, SWTBotID.CONFIGURATIONS);
        
        final TableViewerColumn nameViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn nameColumn = nameViewerColumn.getColumn();
        tableColumnLayout.setColumnData(nameColumn, new ColumnWeightData(1, 50, true));
        nameColumn.setText("Name");
        
        final TableViewerColumn typeViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn typeColumn = typeViewerColumn.getColumn();
        tableColumnLayout.setColumnData(typeColumn, new ColumnPixelData(75, true, true));
        typeColumn.setText("Type");
        
        final TableViewerColumn locationViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn locationColumn = locationViewerColumn.getColumn();
        tableColumnLayout.setColumnData(locationColumn, new ColumnWeightData(2, 50, true));
        locationColumn.setText("Location");
        
        addRuleSetConfiguration = new Button(composite, SWT.NONE);
        SWTBotID.set(addRuleSetConfiguration, SWTBotID.ADD);
        addRuleSetConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        addRuleSetConfiguration.setText("Add...");
        addRuleSetConfiguration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                controller.addRuleSetConfiguration(((Control) e.widget).getShell());
            }
        });
        
        removeRuleSetConfiguration = new Button(composite, SWT.NONE);
        removeRuleSetConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        removeRuleSetConfiguration.setText("Remove");
        removeRuleSetConfiguration.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                controller.removeSelectedConfigurations();
            }
        });
        
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
        //
        final IObservableValue btnEnablePmdForObserveSelectionObserveWidget = SWTObservables.observeSelection(enablePMDCheckbox);
        final IObservableValue modelPMDEnabledObserveValue = BeansObservables.observeValue(controller.getModel(), "PMDEnabled");
        bindingContext.bindValue(btnEnablePmdForObserveSelectionObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final IObservableValue addObserveEnabledObserveWidget = SWTObservables.observeEnabled(addRuleSetConfiguration);
        bindingContext.bindValue(addObserveEnabledObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
        final IObservableMap[] observeMaps = PojoObservables.observeMaps(listContentProvider.getKnownElements(),
                RuleSetConfiguration.class, new String[] { "name", "type", "location" });
        tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
        tableViewer.setContentProvider(listContentProvider);
        //
        final IObservableList controllergetModelConfigurationsObserveList = BeansObservables.observeList(Realm.getDefault(),
                controller.getModel(), "configurations");
        tableViewer.setInput(controllergetModelConfigurationsObserveList);
        //
        final IObservableValue tableObserveEnabledObserveWidget = SWTObservables.observeEnabled(table);
        bindingContext.bindValue(tableObserveEnabledObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final IObservableList tableViewerObserveMultiSelection = ViewersObservables.observeMultiSelection(tableViewer);
        final IObservableList controllergetModelSelectedConfigurationsObserveList = BeansObservables.observeList(Realm.getDefault(),
                controller.getModel(), "selectedConfigurations");
        bindingContext.bindList(tableViewerObserveMultiSelection, controllergetModelSelectedConfigurationsObserveList, null, null);
        //
        final IObservableValue removeObserveEnabledObserveWidget = SWTObservables.observeEnabled(removeRuleSetConfiguration);
        final IObservableValue removeEnabledObserveValue = BeansObservables.observeValue(controller.getModel(), "removeEnabled");
        bindingContext.bindValue(removeObserveEnabledObserveWidget, removeEnabledObserveValue, null, null);
        //
        final IObservableSet tableViewerObserveCheckedElements = ViewersObservables.observeCheckedElements(tableViewer,
                RuleSetConfiguration.class);
        final IObservableSet activeConfigurationsObserveSet = BeansObservables.observeSet(Realm.getDefault(), controller.getModel(),
                "activeConfigurations");
        bindingContext.bindSet(tableViewerObserveCheckedElements, activeConfigurationsObserveSet, null, null);
        //
        return bindingContext;
    }
}
