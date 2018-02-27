// =====================================================================
//
// Copyright (C) 2012 - 2018, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IProject;
import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

/**
 * Unit tests for {@link PMDPropertyPageController}.
 * 
 * @author Philip Graf
 */
public class PMDPropertyPageControllerTest {
    
    /**
     * Verifies that {@link PMDPropertyPageController#removeSelectedConfigurations()} updates the view model correctly.
     */
    @Test
    public void removeSelectedConfigurations() {
        final PMDPropertyPageController controller = new PMDPropertyPageController();
        final PMDPropertyPageViewModel model = controller.getModel();
        final IProject project = mock(IProject.class);
        model.setInitialState(true, ImmutableSortedSet.<RuleSetModel>of(), project);
        final ImmutableList<RuleSetViewModel> ruleSets = createRuleSets();
        model.setRuleSets(ruleSets);
        model.setActiveRuleSets(ruleSets.subList(0, 2));
        model.setSelectedRuleSets(ruleSets.subList(1, 3));
        
        controller.removeSelectedConfigurations();

        assertEquals("ruleSets", "[A, D]", toNameString(model.getRuleSets()));
        assertEquals("activeRuleSets", "[A]", toNameString(model.getActiveRuleSets()));
        assertEquals("selectedRuleSets", "[]", toNameString(model.getSelectedRuleSets()));
    }
    
    /**
     * Verifies that {@link PMDPropertyPageController#removeSelectedConfigurations()} updates the view model correctly
     * when none of the rule sets are selected.
     */
    @Test
    public void removeSelectedConfigurationsWithoutSelection() {
        final PMDPropertyPageController controller = new PMDPropertyPageController();
        final PMDPropertyPageViewModel model = controller.getModel();
        final IProject project = mock(IProject.class);
        model.setInitialState(true, ImmutableSortedSet.<RuleSetModel>of(), project);
        final ImmutableList<RuleSetViewModel> ruleSets = createRuleSets();
        model.setRuleSets(ruleSets);
        model.setActiveRuleSets(ruleSets.subList(0, 2));
        model.setSelectedRuleSets(ImmutableList.<RuleSetViewModel>of());
        
        controller.removeSelectedConfigurations();
        
        assertEquals("ruleSets", "[A, B, C, D]", toNameString(model.getRuleSets()));
        assertEquals("activeRuleSets", "[A, B]", toNameString(model.getActiveRuleSets()));
        assertEquals("selectedRuleSets", "[]", toNameString(model.getSelectedRuleSets()));
    }

    /**
     * Returns a string representation of the provided rule sets iterable containing its element's names.
     */
    private String toNameString(final Iterable<RuleSetViewModel> ruleSets) {
        return Iterables.toString(Iterables.transform(ruleSets, new Function<RuleSetViewModel, String>() {
            @Override
            public String apply(final RuleSetViewModel ruleSet) {
                return ruleSet.getName();
            }
        }));
    }

    private ImmutableList<RuleSetViewModel> createRuleSets() {
        return ImmutableList.of(new RuleSetViewModel("A", "A-Type", "A-Location", true, "A-LocationToolTip"),
                new RuleSetViewModel("B", "B-Type", "B-Location", false, "B-LocationToolTip"),
                new RuleSetViewModel("C", "C-Type", "C-Location", true, "C-LocationToolTip"),
                new RuleSetViewModel("D", "D-Type", "D-Location", false, "D-LocationToolTip"));
    }
    
}
