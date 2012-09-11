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

package ch.acanda.eclipse.pmd.preferences;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.FileSystemRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.ProjectRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.WorkspaceRuleSetConfiguration;

import com.google.common.collect.ImmutableList;

/**
 * Tests for {@link RuleSetConfigurationSerializer}.
 * 
 * @author Philip Graf
 */
public class RulesSetConfigurationSerializerTest {
    
    private static Path path;

    @BeforeClass
    public static void beforeClass() throws IOException {
        path = Files.createTempFile(null, null).toAbsolutePath();
    }

    /**
     * Verifies that {@link RuleSetConfigurationSerializer#serialize(ImmutableList)} correctly serializes a single
     * {@link FileSystemRuleSetConfiguration}.
     */
    @Test
    public void serializeSingleFileSystemRuleSetConfiguration() {
        final RuleSetConfiguration config = new FileSystemRuleSetConfiguration(1, "PMD Configuration", path);
        final ImmutableList<RuleSetConfiguration> configs = ImmutableList.of(config);

        final String actual = RuleSetConfigurationSerializer.serialize(configs);
        
        final String expected = "FS\u241F1\u241FPMD Configuration\u241F" + path;
        assertEquals("Serialized FileSystemRuleSetConfiguration", expected, actual);
    }
    
    /**
     * Verifies that {@link RuleSetConfigurationSerializer#serialize(ImmutableList)} correctly serializes multiple
     * {@link RuleSetConfiguration}s.
     */
    @Test
    public void serializeMultipleRuleSetConfiguration() {
        final RuleSetConfiguration config1 = new FileSystemRuleSetConfiguration(1, "File System Configuration", path);
        final Path workspaceRelativePath = Paths.get("Project/pmd.xml");
        final RuleSetConfiguration config2 = new WorkspaceRuleSetConfiguration(2, "Workspace Configuration", workspaceRelativePath);
        final Path projectRelativePath = Paths.get("pmd.xml");
        final RuleSetConfiguration config3 = new ProjectRuleSetConfiguration(2, "Project Configuration", projectRelativePath);
        final ImmutableList<RuleSetConfiguration> configs = ImmutableList.of(config1, config2, config3);
        
        final String actual = RuleSetConfigurationSerializer.serialize(configs);
        
        final String expected = "FS\u241F1\u241FFile System Configuration\u241F" + path
                + "\u241EWS\u241F2\u241FWorkspace Configuration\u241F" + workspaceRelativePath
                + "\u241EPJ\u241F2\u241FProject Configuration\u241F" + projectRelativePath;
        assertEquals("Serialized RuleSetConfigurations", expected, actual);
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
     * Verifies that {@link RuleSetConfigurationSerializer#serialize(ImmutableList)} throws an
     * {@link IllegalArgumentException} when it cannot serialize a {@link RuleSetConfiguration}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void serializeUnknownRuleSetConfiguration() {
        final RuleSetConfiguration config = mock(RuleSetConfiguration.class);
        final ImmutableList<RuleSetConfiguration> configs = ImmutableList.of(config);

        RuleSetConfigurationSerializer.serialize(configs);
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
