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

package ch.acanda.eclipse.pmd.properties;

import static ch.acanda.eclipse.pmd.properties.PMDPropertyPageModelTransformer.toDomainModels;
import static ch.acanda.eclipse.pmd.properties.PMDPropertyPageModelTransformer.toViewModel;
import static ch.acanda.eclipse.pmd.properties.PMDPropertyPageModelTransformer.toViewModels;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;

import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.builder.PMDNature;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.domain.WorkspaceModel;
import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;
import ch.acanda.eclipse.pmd.repository.ProjectModelRepository;
import ch.acanda.eclipse.pmd.wizard.AddRuleSetConfigurationWizard;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Controller for the PMD project property page.
 *
 * @author Philip Graf
 */
final class PMDPropertyPageController {

    private final PMDPropertyPageViewModel model;
    private ProjectModel projectModel;
    private IProject project;

    public PMDPropertyPageController() {
        model = new PMDPropertyPageViewModel();
    }

    public PMDPropertyPageViewModel getModel() {
        return model;
    }

    public void init(final IProject project) {
        this.project = project;
        final WorkspaceModel workspaceModel = PMDPlugin.getDefault().getWorkspaceModel();

        projectModel = workspaceModel.getOrCreateProject(project.getName());
        model.setInitialState(projectModel.isPMDEnabled(), projectModel.getRuleSets());
        final ImmutableSortedSet.Builder<RuleSetModel> ruleSetBuilder = ImmutableSortedSet.orderedBy(ProjectModel.RULE_SET_COMPARATOR);
        for (final ProjectModel projectModel : workspaceModel.getProjects()) {
            ruleSetBuilder.addAll(projectModel.getRuleSets());
        }
        model.setRuleSets(ImmutableList.copyOf(toViewModels(ruleSetBuilder.build())));
        reset();
    }

    public void reset() {
        model.setActiveRuleSets(ImmutableSet.copyOf(toViewModels(projectModel.getRuleSets())));
        model.setSelectedRuleSets(ImmutableList.<RuleSetViewModel>of());
        model.setPMDEnabled(projectModel.isPMDEnabled());
    }

    public void save() {
        projectModel.setPMDEnabled(model.isPMDEnabled());
        projectModel.setRuleSets(toDomainModels(model.getActiveRuleSets()));

        final ProjectModelRepository projectModelRepository = new ProjectModelRepository();
        projectModelRepository.save(projectModel);

        try {
            if (model.isPMDEnabled()) {
                PMDNature.addTo(project);
            } else {
                PMDNature.removeFrom(project);
            }
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Cannot change PMD nature of project " + project.getName(), e);
        }
    }

    public boolean isValid() {
        return true;
    }

    public void addRuleSetConfiguration(final Shell shell) {
        final AddRuleSetConfigurationWizard wizard = new AddRuleSetConfigurationWizard(project);
        final WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.setPageSize(300, SWT.DEFAULT);
        final int result = dialog.open();
        if (result == Window.OK && wizard.getRuleSetModel() != null) {
            final RuleSetViewModel viewModel = toViewModel(wizard.getRuleSetModel());
            model.addRuleSet(viewModel);
            final HashSet<RuleSetViewModel> activeConfigs = new HashSet<>(model.getActiveRuleSets());
            activeConfigs.add(viewModel);
            model.setActiveRuleSets(activeConfigs);
        }
    }

    public void removeSelectedConfigurations() {
        final Predicate<RuleSetViewModel> notInSelection = not(in(model.getSelectedRuleSets()));
        model.setRuleSets(ImmutableList.copyOf(filter(model.getRuleSets(), notInSelection)));
        model.setActiveRuleSets(filter(model.getActiveRuleSets(), notInSelection));
        model.setSelectedRuleSets(ImmutableList.<RuleSetViewModel>of());
    }

}
