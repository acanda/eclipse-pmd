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

package ch.acanda.eclipse.pmd.wizard;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.acanda.eclipse.pmd.ui.model.ValidationResult;

/**
 * Tests the PMD rule set functionality of the PMD property dialog.
 * 
 * @author Philip Graf
 */
public class AddRuleSetConfigurationModelTest {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    /**
     * Verifies that a rule set configuration in a project outside of the workspace does not produce an error in the
     * "Add Rule Set Configuration" wizard.
     */
    @Test
    public void validateWorkspaceConfigurationWithProjectOutsideWorkspace() throws IOException {
        final Path ruleSetFile = createRuleSetFile();
        
        final IProject project = mock(IProject.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        final IWorkspaceRoot root = mock(IWorkspaceRoot.class);
        when(project.getWorkspace()).thenReturn(workspace);
        when(workspace.getRoot()).thenReturn(root);
        when(root.getProject(anyString())).thenReturn(project);
        when(project.getLocationURI()).thenReturn(ruleSetFile.getParent().toUri());
        
        final AddRuleSetConfigurationModel model = new AddRuleSetConfigurationModel(project);
        model.setWorkspaceTypeSelected(true);
        model.setName("X");
        model.setLocation("ProjectX/" + ruleSetFile.getName(ruleSetFile.getNameCount() - 1));
        final ValidationResult validationResult = new ValidationResult();
        
        model.validate(AddRuleSetConfigurationModel.LOCATION, validationResult);
        
        if (validationResult.hasErrors()) {
            final String msg = "The validation should not result in any errors "
                    + "if the project is located outside the workspace. First error: ";
            fail(msg + validationResult.getFirstErrorMessage());
        }
    }
    
    public Path createRuleSetFile() throws IOException {
        final Path ruleSetFile = folder.newFile("AddRuleSetConfigurationModelTest.xml").toPath();
        try (final InputStream in = AddRuleSetConfigurationModelTest.class.getResourceAsStream("AddRuleSetConfigurationModelTest.xml")) {
            Files.copy(in, ruleSetFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return ruleSetFile;
    }
    
}
