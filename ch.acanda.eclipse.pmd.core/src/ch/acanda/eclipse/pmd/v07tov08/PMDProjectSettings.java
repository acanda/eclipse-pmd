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

package ch.acanda.eclipse.pmd.v07tov08;

import static com.google.common.base.Strings.nullToEmpty;

import java.util.Set;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetReferenceId;
import net.sourceforge.pmd.RuleSets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.builder.PMDNature;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Convenience class for reading and writing the PMD settings of a project. See {@link PMDWorkspaceSettings} for
 * settings that do not depend on a specific project.
 *
 * @author Philip Graf
 */
@Deprecated
final class PMDProjectSettings {

    private static final QualifiedName ACTIVE_RULE_SETS = new QualifiedName(PMDPlugin.ID, "rulesets");
    private static final QualifiedName ACTIVE_RULE_SET_IDS = new QualifiedName(PMDPlugin.ID, "activerulesets");

    private final Function<RuleSetConfiguration, RuleSetReferenceId> toReferenceId =
            new Function<RuleSetConfiguration, RuleSetReferenceId>() {
                @Override
                public RuleSetReferenceId apply(final RuleSetConfiguration config) {
                    return new RuleSetReferenceId(config.getConfiguration(project).toString());
                }
            };

    private final IProject project;

    public PMDProjectSettings(final IProject project) {
        assert project != null && project.isOpen();
        this.project = project;
    }

    public RuleSets getActiveRuleSets() {
        RuleSets ruleSets = null;
        try {
            ruleSets = getActiveRuleSetsFromCache();
            if (ruleSets == null) {
                final PMDWorkspaceSettings workspaceSettings = new PMDWorkspaceSettings(PMDPlugin.getDefault().getPreferenceStore());
                final ImmutableList<RuleSetConfiguration> configs = workspaceSettings.getRuleSetsConfigurations();
                final ImmutableList<RuleSetConfiguration> activeConfigs = ImmutableList.copyOf(getActiveRuleSetConfigurations(configs));
                ruleSets = new RuleSetFactory().createRuleSets(Lists.transform(activeConfigs, toReferenceId));
                putActiveRuleSetsIntoCache(ruleSets);
            }
        } catch (final RuleSetNotFoundException e) {
            PMDPlugin.getDefault().error("Could not load PMD rule sets.", e);
        }
        if (ruleSets == null) {
            ruleSets = new RuleSets();
            putActiveRuleSetsIntoCache(ruleSets);
        }
        return ruleSets;
    }

    private RuleSets getActiveRuleSetsFromCache() {
        try {
            return (RuleSets) project.getSessionProperty(ACTIVE_RULE_SETS);
        } catch (final CoreException e) {
            PMDPlugin.getDefault().warn("Cannot read active rule sets from session properties of project " + project.getName(), e);
        }
        return null;
    }

    private void putActiveRuleSetsIntoCache(final RuleSets ruleSets) {
        try {
            project.setSessionProperty(ACTIVE_RULE_SETS, ruleSets);
        } catch (final CoreException e) {
            PMDPlugin.getDefault().warn("Could not store active rule sets in session properties of project " + project.getName(), e);
        }
    }

    /**
     * @return {@code true} if the project has the PMD nature, {@code false} otherwise.
     */
    public boolean isPMDEnabled() {
        try {
            return project.hasNature(PMDNature.ID);
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Cannot access PMD nature of project " + project.getName(), e);
        }
        return false;
    }

    public Set<RuleSetConfiguration> getActiveRuleSetConfigurations(final ImmutableList<RuleSetConfiguration> configs) {
        final ImmutableSet.Builder<RuleSetConfiguration> activeConfigs = ImmutableSet.builder();
        try {
            final ImmutableSet.Builder<Integer> idsBuilder = ImmutableSet.builder();
            final String activeRuleSetIds = nullToEmpty(project.getPersistentProperty(ACTIVE_RULE_SET_IDS));
            for (final String id : Splitter.on(',').omitEmptyStrings().split(activeRuleSetIds)) {
                idsBuilder.add(Integer.parseInt(id));
            }
            final ImmutableSet<Integer> ids = idsBuilder.build();
            for (final RuleSetConfiguration config : configs) {
                if (ids.contains(config.getId())) {
                    activeConfigs.add(config);
                }
            }
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Cannot retrieve active rule set configuration ids of project " + project.getName(), e);
        }
        return activeConfigs.build();
    }

    public void deleteSettings() {
        try {
            project.setPersistentProperty(ACTIVE_RULE_SET_IDS, null);
            project.setSessionProperty(ACTIVE_RULE_SETS, null);
        } catch (final CoreException e) {
            PMDPlugin.getDefault().warn("Cannot delete PMD project settings for project " + project.getName(), e);
        }
    }

}
