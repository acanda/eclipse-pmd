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

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.resources.IProject;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.preferences.PMDWorkspaceSettings;
import ch.acanda.eclipse.pmd.ui.model.ValidationResult;
import ch.acanda.eclipse.pmd.ui.model.ViewModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * @author Philip Graf
 */
final class PMDProjectPropertyPageModel extends ViewModel {
    
    private ImmutableList<RuleSetConfiguration> configurations = ImmutableList.of();
    private ImmutableList<RuleSetConfiguration> selectedConfigurations = ImmutableList.of();
    private ImmutableSet<RuleSetConfiguration> activeConfigurations = ImmutableSet.of();
    private PMDProjectSettings projectSettings;
    private PMDWorkspaceSettings workspaceSettings;
    private boolean isPMDEnabled;
    
    public void init(final IProject project) {
        projectSettings = new PMDProjectSettings(project);
        workspaceSettings = new PMDWorkspaceSettings(PMDPlugin.getDefault().getPreferenceStore());
        reset();
    }
    
    public boolean isPMDEnabled() {
        return isPMDEnabled;
    }
    
    public void setPMDEnabled(final boolean isPMDEnabled) {
        final boolean isRemoveEnabledBefore = isRemoveEnabled();
        setProperty("PMDEnabled", this.isPMDEnabled, this.isPMDEnabled = isPMDEnabled);
        final boolean isRemoveEnabledAfter = isRemoveEnabled();
        setProperty("removeEnabled", isRemoveEnabledBefore, isRemoveEnabledAfter);
    }
    
    public ImmutableList<RuleSetConfiguration> getConfigurations() {
        return configurations;
    }
    
    public void setConfigurations(final ImmutableList<RuleSetConfiguration> configurations) {
        setProperty("configurations", this.configurations, this.configurations = configurations);
    }
    
    @Override
    protected boolean updateDirty() {
        boolean isClean = projectSettings.isPMDEnabled() == isPMDEnabled();
        isClean = isClean && Objects.equals(workspaceSettings.getRuleSetsConfigurations(), getConfigurations());
        return !isClean;
    }
    
    @Override
    protected void reset() {
        setConfigurations(workspaceSettings.getRuleSetsConfigurations());
        setActiveConfigurations(projectSettings.getActiveRuleSetConfigurations(getConfigurations()));
        setSelectedConfigurations(ImmutableList.<RuleSetConfiguration> of());
        setPMDEnabled(projectSettings.isPMDEnabled());
    }
    
    public void addRuleSetConfiguration(final RuleSetConfiguration ruleSetConfiguration) {
        final Builder<RuleSetConfiguration> newConfigurations = ImmutableList.builder();
        newConfigurations.addAll(configurations);
        newConfigurations.add(ruleSetConfiguration);
        setConfigurations(newConfigurations.build());
    }
    
    @Override
    protected ImmutableSet<String> createValidatedPropertiesSet() {
        return ImmutableSet.of();
    }
    
    @Override
    protected void validate(final String propertyName, final ValidationResult result) {
        // nothing to validate
    }
    
    public void removeSelectedConfigurations() {
        final Builder<RuleSetConfiguration> newConfigurations = ImmutableList.builder();
        for(final RuleSetConfiguration config: configurations) {
            if(!selectedConfigurations.contains(config)) {
                newConfigurations.add(config);
            }
        }
        setConfigurations(newConfigurations.build());
    }
    
    public void setSelectedConfigurations(final List<RuleSetConfiguration> configs) {
        final boolean isRemoveEnabledBefore = isRemoveEnabled();
        setProperty("selectedConfigurations", selectedConfigurations, selectedConfigurations = ImmutableList.copyOf(configs));
        final boolean isRemoveEnabledAfter = isRemoveEnabled();
        setProperty("removeEnabled", isRemoveEnabledBefore, isRemoveEnabledAfter);
    }
    
    public List<RuleSetConfiguration> getSelectedConfigurations() {
        return selectedConfigurations;
    }
    
    public void setActiveConfigurations(final Set<RuleSetConfiguration> configs) {
        setProperty("activeConfigurations", activeConfigurations, activeConfigurations = ImmutableSet.copyOf(configs));
    }
    
    public Set<RuleSetConfiguration> getActiveConfigurations() {
        return activeConfigurations;
    }
    
    public boolean isRemoveEnabled() {
        return isPMDEnabled() && !selectedConfigurations.isEmpty();
    }
    
    public void setRemoveEnabled(final boolean b) {
        
    }
}
