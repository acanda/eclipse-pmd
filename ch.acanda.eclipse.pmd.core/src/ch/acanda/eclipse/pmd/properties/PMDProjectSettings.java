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

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.builder.PMDNature;

import com.google.common.base.Strings;

/**
 * Convinience class to read and write the PMD settings of a project.
 * 
 * @author Philip Graf
 */
public final class PMDProjectSettings {
    
    private static final QualifiedName RULE_SETS = new QualifiedName(PMDPlugin.ID, "rulesets");
    private static final QualifiedName RULE_SETS_CONFIGURATION = new QualifiedName(PMDPlugin.ID, "rulesets");
    
    private final IProject project;
    
    public PMDProjectSettings(final IProject project) {
        assert project != null && project.isOpen();
        this.project = project;
    }
    
    public void setRuleSetsConfiguration(final String file) {
        try {
            final String value = file == null ? null : Path.fromOSString(file).toPortableString();
            project.setPersistentProperty(RULE_SETS_CONFIGURATION, value);
            project.setSessionProperty(RULE_SETS, null);
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Cannot store path to rule sets configuration file.", e);
        }
    }
    
    public String getRuleSetsConfiguration() {
        String config = null;
        try {
            config = project.getPersistentProperty(RULE_SETS_CONFIGURATION);
            if (config != null) {
                config = Path.fromPortableString(config).toOSString();
            }
        } catch (final CoreException e) {
            PMDPlugin.getDefault().warn("Cannot retrieve path to rule sets configuration file.", e);
        }
        return config == null ? "" : config;
    }
    
    public RuleSets getRuleSets() {
        RuleSets ruleSets = null;
        try {
            ruleSets = (RuleSets) project.getSessionProperty(RULE_SETS);
            if (ruleSets == null) {
                final String configuration = project.getPersistentProperty(RULE_SETS_CONFIGURATION);
                if (!Strings.isNullOrEmpty(configuration)) {
                    ruleSets = new RuleSetFactory().createRuleSets(configuration);
                }
            }
        } catch (final CoreException | RuleSetNotFoundException e) {
            PMDPlugin.getDefault().error("Could not load PMD rule sets.", e);
        }
        if (ruleSets == null) {
            PMDPlugin.getDefault().error("Could not load PMD rule sets.", null);
            ruleSets = new RuleSets();
        }
        return ruleSets;
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
    
    /**
     * Adds or removes the PDM nature from the project.
     */
    public void setPMDEnabled(final boolean isEnabled) {
        try {
            if (isEnabled) {
                PMDNature.addTo(project);
            } else {
                PMDNature.removeFrom(project);
            }
        } catch (final CoreException e) {
            PMDPlugin.getDefault().error("Cannot change PMD nature of project " + project.getName(), e);
        }
    }
    
}
