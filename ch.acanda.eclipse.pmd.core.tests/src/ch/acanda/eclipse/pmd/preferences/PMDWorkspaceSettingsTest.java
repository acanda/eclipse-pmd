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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

/**
 * Unit tests for {@link PMDWorkspaceSettings}.
 * 
 * @author Philip Graf
 */
public class PMDWorkspaceSettingsTest {
    
    private static final String NEXT_RULE_SET_CONFIGURATION_ID = "NextRuleSetConfigurationId";

    /**
     * Verify that {@link PMDWorkspaceSettings#getNextRuleSetConfigurationId()} reads the id from the preference store
     * and updates the value to contain the next id.
     */
    @Test
    public void testGetNextRuleSetConfigurationId() {
        final IPreferenceStore store = mock(IPreferenceStore.class);
        when(store.getInt(NEXT_RULE_SET_CONFIGURATION_ID)).thenReturn(1234);
        final PMDWorkspaceSettings settings = new PMDWorkspaceSettings(store);
        final int id = settings.getNextRuleSetConfigurationId();
        assertEquals("The id should be read from the preference store", 1234, id);
        verify(store).setValue(NEXT_RULE_SET_CONFIGURATION_ID, 1235);
    }
    
}
