// =====================================================================
//
// Copyright (C) 2012 - 2013, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.properties.PMDProjectSettings;

import com.google.common.collect.ImmutableList;

/**
 * Convenience class for reading and writing the workspace PMD settings, i.e. settings that do not depend on a specific
 * project. See {@link PMDProjectSettings} for settings that depend on a specific project.
 * 
 * @author Philip Graf
 */
public final class PMDWorkspaceSettings {
    
    /**
     * Name of the preference for the serialized rule set configurations.
     */
    private static final String RULE_SET_CONFIGURATIONS = "RuleSetConfigurations";
    
    /**
     * Name of the preference for the next unused rule set configuration id.
     */
    private static final String NEXT_RULE_SET_CONFIGURATION_ID = "NextRuleSetConfigurationId";
    
    private final IPreferenceStore preferenceStore;
    
    public PMDWorkspaceSettings(final IPreferenceStore preferenceStore) {
        this.preferenceStore = preferenceStore;
    }
    
    /**
     * @return The stored rule set configurations. This method will return an empty list if there aren't any stored
     *         configurations.
     */
    public ImmutableList<RuleSetConfiguration> getRuleSetsConfigurations() {
        final String serializedConfigs = preferenceStore.getString(RULE_SET_CONFIGURATIONS);
        return RuleSetConfigurationSerializer.deserialize(serializedConfigs);
    }
    
    /**
     * Stores the provided rule set configurations in the workspace preferences.
     * 
     * @param configs The rule set configurations to store. May be {@code null}.
     */
    public void setRuleSetConfigurations(final ImmutableList<RuleSetConfiguration> configs) {
        final String serializedConfigs = RuleSetConfigurationSerializer.serialize(configs);
        preferenceStore.setValue(RULE_SET_CONFIGURATIONS, serializedConfigs);
    }
    
    /**
     * @return The next unused rule set configuration id.
     */
    public int getNextRuleSetConfigurationId() {
        final int id = preferenceStore.getInt(NEXT_RULE_SET_CONFIGURATION_ID);
        preferenceStore.setValue(NEXT_RULE_SET_CONFIGURATION_ID, id + 1);
        return id;
    }
    
}
