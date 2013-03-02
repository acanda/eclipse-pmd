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

package ch.acanda.eclipse.pmd.wizard;

import static ch.acanda.eclipse.pmd.ui.util.ValidationUtil.errorIfBlank;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import org.eclipse.core.resources.IProject;

import ch.acanda.eclipse.pmd.ui.model.ValidationProblem;
import ch.acanda.eclipse.pmd.ui.model.ValidationProblem.Severity;
import ch.acanda.eclipse.pmd.ui.model.ValidationResult;
import ch.acanda.eclipse.pmd.ui.model.ViewModel;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * View model for the wizard page to add a new file system rule set configuration.
 * 
 * @author Philip Graf
 */
class AddRuleSetConfigurationModel extends ViewModel {
    
    public static final String FILE_SYSTEM_TYPE_SELECTED = "fileSystemTypeSelected";
    public static final String WORKSPACE_TYPE_SELECTED = "workspaceTypeSelected";
    public static final String PROJECT_TYPE_SELECTED = "projectTypeSelected";
    public static final String LOCATION = "location";
    
    private final IProject project;
    
    private String name;
    private String location;
    private boolean isFileSystemTypeSelected;
    private boolean isWorkspaceTypeSelected;
    private boolean isProjectTypeSelected;
    
    /**
     * This property is derived from {@link #location}. If {@link #location} is valid this list contains the rules of
     * the selected rule set, otherwise it is empty. It is never {@code null}.
     */
    private ImmutableList<Rule> rules = ImmutableList.of();
    
    public AddRuleSetConfigurationModel(final IProject project) {
        this.project = project;
    }
    
    @Override
    protected boolean updateDirty() {
        return !(Strings.isNullOrEmpty(name) && Strings.isNullOrEmpty(location));
    }
    
    @Override
    protected void reset() {
        setName(null);
        setLocation(null);
        setWorkspaceTypeSelected(true);
        setProjectTypeSelected(false);
        setFileSystemTypeSelected(false);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        setProperty("name", this.name, this.name = name);
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(final String location) {
        setProperty(LOCATION, this.location, this.location = location);
    }
    
    public ImmutableList<Rule> getRules() {
        return rules;
    }
    
    private void setRules(final ImmutableList<Rule> rules) {
        assert rules != null;
        setProperty("rules", this.rules, this.rules = rules);
    }
    
    public boolean isFileSystemTypeSelected() {
        return isFileSystemTypeSelected;
    }
    
    public void setFileSystemTypeSelected(final boolean isFileSystemTypeSelected) {
        setProperty(FILE_SYSTEM_TYPE_SELECTED, this.isFileSystemTypeSelected, this.isFileSystemTypeSelected = isFileSystemTypeSelected);
    }
    
    public boolean isWorkspaceTypeSelected() {
        return isWorkspaceTypeSelected;
    }
    
    public void setWorkspaceTypeSelected(final boolean isWorkspaceTypeSelected) {
        setProperty(WORKSPACE_TYPE_SELECTED, this.isWorkspaceTypeSelected, this.isWorkspaceTypeSelected = isWorkspaceTypeSelected);
    }
    
    public boolean isProjectTypeSelected() {
        return isProjectTypeSelected;
    }
    
    public void setProjectTypeSelected(final boolean isProjectTypeSelected) {
        setProperty(PROJECT_TYPE_SELECTED, this.isProjectTypeSelected, this.isProjectTypeSelected = isProjectTypeSelected);
    }
    
    @Override
    protected ImmutableSet<String> createValidatedPropertiesSet() {
        return ImmutableSet.of(LOCATION, "name");
    }
    
    @Override
    protected void validate(final String propertyName, final ValidationResult validationResult) {
        validateName(validationResult);
        validateLocation(propertyName, validationResult);
    }
    
    /**
     * Validates the location of the rule set configuration and sets or resets the property {@link #rules} depending on
     * whether {@link #location} contains a valid rule set configuration location or not.
     */
    private void validateLocation(final String propertyName, final ValidationResult result) {
        final Builder<Rule> rules = ImmutableList.builder();
        if (!errorIfBlank(LOCATION, location, "Please enter the location of the rule set configuration", result)) {
            RuleSet ruleSet = null;
            try {
                final Path absoluteLocation = getAbsoluteLocation();
                if (Files.exists(absoluteLocation)) {
                    ruleSet = new RuleSetFactory().createRuleSet(absoluteLocation.toString());
                    rules.addAll(ruleSet.getRules());
                }
            } catch (final RuleSetNotFoundException | IllegalArgumentException e) {
                // the rule set location is invalid - the validation problem will be added below
            }
            if (ruleSet == null || ruleSet.getRules().isEmpty()) {
                result.add(new ValidationProblem(LOCATION, Severity.ERROR,
                        "The rule set configuration at the given location is invalid"));
            }
        }
        if (LOCATION.equals(propertyName)) {
            setRules(rules.build());
        }
    }
    
    private Path getAbsoluteLocation() {
        final Path absoluteLocation;
        if (isWorkspaceTypeSelected) {
            absoluteLocation = Paths.get(project.getWorkspace().getRoot().getLocationURI()).resolve(Paths.get(location));
        } else if (isProjectTypeSelected) {
            absoluteLocation = Paths.get(project.getLocationURI()).resolve(Paths.get(location));
        } else if (isFileSystemTypeSelected) {
            absoluteLocation = Paths.get(location);
        } else {
            throw new IllegalStateException("Unknown ");
        }
        return absoluteLocation;
    }
    
    private void validateName(final ValidationResult result) {
        errorIfBlank("name", name, "Please enter a name for this rule set configuration", result);
    }
    
}
