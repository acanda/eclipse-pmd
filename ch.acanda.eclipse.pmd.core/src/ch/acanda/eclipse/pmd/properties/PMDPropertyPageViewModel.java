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

package ch.acanda.eclipse.pmd.properties;

import static ch.acanda.eclipse.pmd.properties.PMDPropertyPageModelTransformer.toViewModels;
import static com.google.common.collect.Iterables.elementsEqual;

import java.util.List;
import java.util.Set;

import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.ui.model.ValidationResult;
import ch.acanda.eclipse.pmd.ui.model.ViewModel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

/**
 * View model for the {@link PMDPropertyPage}.
 *
 * @author Philip Graf
 */
final class PMDPropertyPageViewModel extends ViewModel {

    /**
     * All available rule sets of the entire workspace.
     */
    private ImmutableList<RuleSetViewModel> ruleSets = ImmutableList.of();

    /**
     * The rule sets currently selected.
     */
    private ImmutableList<RuleSetViewModel> selectedRuleSets = ImmutableList.of();

    /**
     * The rule sets currently activated, i.e. with a checked checkbox.
     */
    private ImmutableSet<RuleSetViewModel> activeRuleSets = ImmutableSet.of();

    private boolean isPMDEnabled;
    private boolean initialIsPMDEnabled;
    private Iterable<RuleSetViewModel> initialActiveRuleSets;

    public boolean isPMDEnabled() {
        return isPMDEnabled;
    }

    public void setPMDEnabled(final boolean isPMDEnabled) {
        setProperty("PMDEnabled", this.isPMDEnabled, this.isPMDEnabled = isPMDEnabled);
    }

    public ImmutableList<RuleSetViewModel> getRuleSets() {
        return ruleSets;
    }

    public void setRuleSets(final ImmutableList<RuleSetViewModel> ruleSets) {
        setProperty("ruleSets", this.ruleSets, this.ruleSets = ruleSets);
    }

    /**
     * Sets the initial state, i.e. the state of the view model before any changes were made. This state is used by
     * {@link #updateDirty()} so it must be set before any properties of the view model are changed.
     */
    public void setInitialState(final boolean isPmdEnabled, final ImmutableSortedSet<RuleSetModel> ruleSets) {
        initialIsPMDEnabled = isPMDEnabled;
        initialActiveRuleSets = toViewModels(ruleSets);
    }

    @Override
    protected boolean updateDirty() {
        boolean isClean = initialIsPMDEnabled == isPMDEnabled();
        isClean = isClean && elementsEqual(initialActiveRuleSets, getActiveRuleSets());
        return !isClean;
    }

    public void addRuleSet(final RuleSetViewModel viewModel) {
        final Builder<RuleSetViewModel> newConfigurations = ImmutableList.builder();
        newConfigurations.addAll(ruleSets);
        newConfigurations.add(viewModel);
        setRuleSets(newConfigurations.build());
    }

    @Override
    protected ImmutableSet<String> createValidatedPropertiesSet() {
        return ImmutableSet.of();
    }

    @Override
    protected void validate(final String propertyName, final ValidationResult result) {
        // nothing to validate
    }

    public void setSelectedRuleSets(final List<RuleSetViewModel> ruleSets) {
        setProperty("selectedRuleSets", selectedRuleSets, selectedRuleSets = ImmutableList.copyOf(ruleSets));
    }

    public List<RuleSetViewModel> getSelectedRuleSets() {
        return selectedRuleSets;
    }

    public void setActiveRuleSets(final Iterable<RuleSetViewModel> ruleSets) {
        setProperty("activeRuleSets", activeRuleSets, activeRuleSets = ImmutableSet.copyOf(ruleSets));
    }

    public void setActiveRuleSets(final Set<RuleSetViewModel> ruleSets) {
        setProperty("activeRuleSets", activeRuleSets, activeRuleSets = ImmutableSet.copyOf(ruleSets));
    }

    public Set<RuleSetViewModel> getActiveRuleSets() {
        return activeRuleSets;
    }

    static final class RuleSetViewModel {

        private final String name;
        private final String type;
        private final String location;

        public RuleSetViewModel(final String name, final String type, final String location) {
            this.name = name;
            this.type = type;
            this.location = location;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getLocation() {
            return location;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (location == null ? 0 : location.hashCode());
            result = prime * result + (name == null ? 0 : name.hashCode());
            result = prime * result + (type == null ? 0 : type.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RuleSetViewModel other = (RuleSetViewModel) obj;
            if (location == null) {
                if (other.location != null) {
                    return false;
                }
            } else if (!location.equals(other.location)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            if (type == null) {
                if (other.type != null) {
                    return false;
                }
            } else if (!type.equals(other.type)) {
                return false;
            }
            return true;
        }

    }

}
