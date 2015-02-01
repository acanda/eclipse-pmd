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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;
import ch.acanda.eclipse.pmd.swtbot.SWTBotID;

/**
 * A composite containing a single checkbox table viewer showing the rule set configurations.
 *
 * @author Philip Graf
 */
final class RuleSetConfigurationTable extends Composite {

    private final CheckboxTableViewer tableViewer;
    private final Table table;
    private final PMDPropertyPageViewModel model;

    public RuleSetConfigurationTable(final Composite parent, final PMDPropertyPageViewModel model) {
        super(parent, SWT.NONE);
        this.model = model;

        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        setLayout(tableColumnLayout);

        tableViewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
        SWTBotID.set(table, SWTBotID.RULESETS);

        final TableViewerColumn nameViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn nameColumn = nameViewerColumn.getColumn();
        tableColumnLayout.setColumnData(nameColumn, new ColumnWeightData(1, 50, true));
        nameColumn.setText("Name");
        nameViewerColumn.setLabelProvider(new NameLabelProvider(model));

        final TableViewerColumn typeViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn typeColumn = typeViewerColumn.getColumn();
        tableColumnLayout.setColumnData(typeColumn, new ColumnPixelData(75, true, true));
        typeColumn.setText("Type");
        typeViewerColumn.setLabelProvider(new TypeLabelProvider(model));

        final TableViewerColumn locationViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn locationColumn = locationViewerColumn.getColumn();
        tableColumnLayout.setColumnData(locationColumn, new ColumnWeightData(2, 50, true));
        locationColumn.setText("Location");
        locationViewerColumn.setLabelProvider(new LocationLabelProvider(model));

        initDataBindings();
        initListeners();
    }

    private void initDataBindings() {
        final DataBindingContext bindingContext = new DataBindingContext();
        //
        final ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
        tableViewer.setContentProvider(listContentProvider);
        //
        final IObservableList ruleSetsObserveList = BeansObservables.observeList(Realm.getDefault(), model, "ruleSets");
        tableViewer.setInput(ruleSetsObserveList);
        //
        final IObservableValue tableObserveEnabledObserveWidget = SWTObservables.observeEnabled(table);
        final IObservableValue modelPMDEnabledObserveValue = BeansObservables.observeValue(model, "PMDEnabled");
        bindingContext.bindValue(tableObserveEnabledObserveWidget, modelPMDEnabledObserveValue, null, null);
        //
        final IObservableList tableViewerObserveMultiSelection = ViewersObservables.observeMultiSelection(tableViewer);
        final IObservableList selectedRuleSetsObserveList = BeansObservables.observeList(Realm.getDefault(), model, "selectedRuleSets");
        bindingContext.bindList(tableViewerObserveMultiSelection, selectedRuleSetsObserveList, null, null);
        //
        final IObservableSet tableViewerObserveCheckedElements = ViewersObservables.observeCheckedElements(tableViewer,
                                                                                                           RuleSetViewModel.class);
        final IObservableSet activeConfigurationsObserveSet = BeansObservables.observeSet(Realm.getDefault(), model, "activeRuleSets");
        bindingContext.bindSet(tableViewerObserveCheckedElements, activeConfigurationsObserveSet, null, null);
    }

    private void initListeners() {
        model.addPropertyChangeListener(PMDPropertyPageViewModel.ACTIVE_RULE_SETS, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                // this updates the column image of invalid configurations when their checked state changes
                tableViewer.refresh(true);
            }
        });
    }

}
