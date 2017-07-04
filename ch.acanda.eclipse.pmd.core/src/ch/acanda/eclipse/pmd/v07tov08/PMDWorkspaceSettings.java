// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.v07tov08;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.collect.ImmutableList;

/**
 * Convenience class for reading and writing the workspace PMD settings, i.e. settings that do not depend on a specific
 * project. See {@link PMDProjectSettings} for settings that depend on a specific project.
 * 
 * @author Philip Graf
 */
@Deprecated
final class PMDWorkspaceSettings {
    
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
    
    public void deleteSettings() {
        preferenceStore.setToDefault(RULE_SET_CONFIGURATIONS);
        preferenceStore.setToDefault(NEXT_RULE_SET_CONFIGURATION_ID);
    }
    
}
