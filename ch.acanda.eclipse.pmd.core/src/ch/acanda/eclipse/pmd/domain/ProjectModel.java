// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;

public class ProjectModel extends DomainModel {

    public static final String RULESETS_PROPERTY = "ruleSets";
    public static final String PMDENABLED_PROPERTY = "isPMDEnabled";

    public static final RuleSetComparator RULE_SET_COMPARATOR = new RuleSetComparator();

    private final String projectName;

    private boolean isPMDEnabled;
    private ImmutableSortedSet<RuleSetModel> ruleSets = ImmutableSortedSet.<RuleSetModel>of();

    /**
     * Creates a new project model without any rule sets and where PMD is disabled.
     *
     * @param projectName The name of the project, see {@link org.eclipse.core.resources.IProject#getName()}.
     */
    public ProjectModel(final String projectName) {
        this.projectName = checkNotNull(projectName, "The argument name must be a valid project name.");
    }

    public String getProjectName() {
        return projectName;
    }

    public void setPMDEnabled(final boolean isPMDEnabled) {
        setProperty(PMDENABLED_PROPERTY, this.isPMDEnabled, this.isPMDEnabled = isPMDEnabled);
    }

    public boolean isPMDEnabled() {
        return isPMDEnabled;
    }

    public void setRuleSets(final Iterable<RuleSetModel> ruleSets) {
        final Builder<RuleSetModel> builder = ImmutableSortedSet.orderedBy(RULE_SET_COMPARATOR);
        builder.addAll(ruleSets);
        setProperty(RULESETS_PROPERTY, this.ruleSets, this.ruleSets = builder.build());
    }

    public ImmutableSortedSet<RuleSetModel> getRuleSets() {
        return ruleSets;
    }

    private final static class RuleSetComparator implements Comparator<RuleSetModel> {

        @Override
        public int compare(final RuleSetModel ruleSet1, final RuleSetModel ruleSet2) {
            return ComparisonChain.start()
                    .compare(ruleSet1.getLocation().getContext(), ruleSet2.getLocation().getContext())
                    .compare(ruleSet1.getName(), ruleSet2.getName())
                    .compare(ruleSet1.getLocation().getPath(), ruleSet2.getLocation().getPath())
                    .result();
        }

    }

}
