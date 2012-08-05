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

import org.junit.BeforeClass;
import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.FileSystemRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;

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
        final RuleSetConfiguration config1 = new FileSystemRuleSetConfiguration(1, "PMD Configuration", path);
        final RuleSetConfiguration config2 = new FileSystemRuleSetConfiguration(2, "Another Configuration", path);
        final ImmutableList<RuleSetConfiguration> configs = ImmutableList.of(config1, config2);
        
        final String actual = RuleSetConfigurationSerializer.serialize(configs);
        
        final String expected = "FS\u241F1\u241FPMD Configuration\u241F" + path + "\u241EFS\u241F2\u241FAnother Configuration\u241F" + path;
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
        final String serializedConfigs = "FS\u241F1\u241FPMD Configuration\u241F" + path
                + "\u241EFS\u241F2\u241FAnother Configuration\u241F" + path;
        
        final ImmutableList<RuleSetConfiguration> actual = RuleSetConfigurationSerializer.deserialize(serializedConfigs);
        
        assertEquals("Number of RuleSetConfigurations", 2, actual.size());
        
        final FileSystemRuleSetConfiguration expected1 = new FileSystemRuleSetConfiguration(1, "PMD Configuration", path);
        assertFileSystemRuleSetConfigurationEquals(expected1, actual.get(0));
        
        final FileSystemRuleSetConfiguration expected2 = new FileSystemRuleSetConfiguration(2, "Another Configuration", path);
        assertFileSystemRuleSetConfigurationEquals(expected2, actual.get(1));
    }

    private void assertFileSystemRuleSetConfigurationEquals(final FileSystemRuleSetConfiguration expected, final RuleSetConfiguration actual) {
        assertEquals("Class of deserialized rule set configuration", FileSystemRuleSetConfiguration.class, actual.getClass());
        final FileSystemRuleSetConfiguration actualFS = (FileSystemRuleSetConfiguration) actual;
        assertEquals("Name", expected.getName(), actualFS.getName());
        assertEquals("Configuration", expected.getConfiguration(), actualFS.getConfiguration());
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
