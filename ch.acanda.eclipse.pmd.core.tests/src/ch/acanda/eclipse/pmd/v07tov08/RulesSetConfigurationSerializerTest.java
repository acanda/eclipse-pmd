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

package ch.acanda.eclipse.pmd.v07tov08;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests for {@link RuleSetConfigurationSerializer}.
 * 
 * @author Philip Graf
 */
@SuppressWarnings("deprecation")
public class RulesSetConfigurationSerializerTest {
    
    private static Path path;

    @BeforeClass
    public static void beforeClass() throws IOException {
        path = Files.createTempFile(null, null).toAbsolutePath();
    }

    /**
     * Verifies that {@link RuleSetConfigurationSerializer#deserialize(String)} corretly deserializes a single
     * {@link FileSystemRuleSetConfiguration}.
     */
    @Test
    public void deserializeSingleFileSystemRuleSetConfiguration() {
        final String serializedConfig = "FS\u241F1\u241FPMD Configuration\u241F" + path;
        
        final ImmutableList<RuleSetConfiguration> actual = RuleSetConfigurationSerializer.deserialize(serializedConfig);
        
        assertEquals("Number of RuleSetConfigurations", 1, actual.size());
        
        final FileSystemRuleSetConfiguration expected = new FileSystemRuleSetConfiguration(1, "PMD Configuration", path);
        assertFileSystemRuleSetConfigurationEquals(expected, actual.get(0));
    }
    

    /**
     * Verifies that {@link RuleSetConfigurationSerializer#deserialize(String)} corretly deserializes multiple
     * {@link RuleSetConfiguration}s.
     */
    @Test
    public void deserializeMultipleRuleSetConfigurations() {
        final Path workspaceRelativePath = Paths.get("Project/pmd.xml");
        final Path projectRelativePath = Paths.get("pmd.xml");

        final String serializedConfigs = "FS\u241F1\u241FFile System Configuration\u241F" + path
                + "\u241EWS\u241F2\u241FWorkspace Configuration\u241F" + workspaceRelativePath
                + "\u241EPJ\u241F3\u241FProject Configuration\u241F" + projectRelativePath;
        
        final ImmutableList<RuleSetConfiguration> actual = RuleSetConfigurationSerializer.deserialize(serializedConfigs);
        
        assertEquals("Number of RuleSetConfigurations", 3, actual.size());
        
        final FileSystemRuleSetConfiguration expected1 = new FileSystemRuleSetConfiguration(1, "File System Configuration", path);
        assertFileSystemRuleSetConfigurationEquals(expected1, actual.get(0));
        
        final WorkspaceRuleSetConfiguration expected2 = new WorkspaceRuleSetConfiguration(2, "Workspace Configuration",
                workspaceRelativePath);
        assertWorkspaceRuleSetConfigurationEquals(expected2, actual.get(1));
        
        final ProjectRuleSetConfiguration expected3 = new ProjectRuleSetConfiguration(3, "Project Configuration", projectRelativePath);
        assertProjectRuleSetConfigurationEquals(expected3, actual.get(2));
    }

    private void assertFileSystemRuleSetConfigurationEquals(final FileSystemRuleSetConfiguration expected, final RuleSetConfiguration actual) {
        assertEquals("Class of deserialized rule set configuration", FileSystemRuleSetConfiguration.class, actual.getClass());
        final FileSystemRuleSetConfiguration actualFS = (FileSystemRuleSetConfiguration) actual;
        assertEquals("Name", expected.getName(), actualFS.getName());
        assertEquals("Configuration", expected.getLocation(), actualFS.getLocation());
    }
    
    private void assertWorkspaceRuleSetConfigurationEquals(final WorkspaceRuleSetConfiguration expected, final RuleSetConfiguration actual) {
        assertEquals("Class of deserialized rule set configuration", WorkspaceRuleSetConfiguration.class, actual.getClass());
        final WorkspaceRuleSetConfiguration actualWS = (WorkspaceRuleSetConfiguration) actual;
        assertEquals("Name", expected.getName(), actualWS.getName());
        assertEquals("Location", expected.getLocation(), actualWS.getLocation());
    }

    private void assertProjectRuleSetConfigurationEquals(final ProjectRuleSetConfiguration expected, final RuleSetConfiguration actual) {
        assertEquals("Class of deserialized rule set configuration", ProjectRuleSetConfiguration.class, actual.getClass());
        final ProjectRuleSetConfiguration actualPJ = (ProjectRuleSetConfiguration) actual;
        assertEquals("Name", expected.getName(), actualPJ.getName());
        assertEquals("Location", expected.getLocation(), actualPJ.getLocation());
    }

    /**
     * Verifies that {@link RuleSetConfigurationSerializer#deserialize(String)} throws an
     * {@link IllegalArgumentException} when it deserializes an invalid serialization.
     */
    @Test(expected = IllegalArgumentException.class)
    public void deserializeInvalidFileSystemRuleSetConfiguration() {
        final String serializedConfig = "FS\u241FPMD Configuration";
        
        RuleSetConfigurationSerializer.deserialize(serializedConfig);
    }

}
