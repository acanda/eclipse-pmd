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

import java.nio.file.Paths;

import ch.acanda.eclipse.pmd.domain.FileSystemRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.ProjectRuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.RuleSetConfiguration;
import ch.acanda.eclipse.pmd.domain.WorkspaceRuleSetConfiguration;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Serializes and deserializes a {@link RuleSetConfiguration} to and from a String.
 * 
 * @author Philip Graf
 */
final class RuleSetConfigurationSerializer {
    
    private static final char VALUE_SEPARATOR = '\u241F';
    private static final char CONFIGURATION_SEPARATOR = '\u241E';
    
    private static enum Identifiers {
        /**
         * Identifier for the {@link WorkspaceRuleSetConfiguration}.
         */
        WS,
        
        /**
         * Identifier for the {@link ProjectRuleSetConfiguration}.
         */
        PJ,
        
        /**
         * Identifier for the {@link FileSystemRuleSetConfiguration}.
         */
        FS
        
    };
    
    private RuleSetConfigurationSerializer() {
        // hide constructor of singleton
    }
    
    /**
     * Serializes a list of {@link RuleSetConfiguration}s to a String.
     * 
     * @return the serialized rule set configurations. Never returns {@code null}.
     * @throws IllegalArgumentException Thrown when the list contains an unknown implementation of
     *             {@link RuleSetConfiguration}.
     */
    public static String serialize(final ImmutableList<RuleSetConfiguration> configs) {
        if (configs == null || configs.isEmpty()) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (final RuleSetConfiguration config : configs) {
            if (config instanceof WorkspaceRuleSetConfiguration) {
                serializeWS(builder, (WorkspaceRuleSetConfiguration) config);
            } else if (config instanceof ProjectRuleSetConfiguration) {
                serializePJ(builder, (ProjectRuleSetConfiguration) config);
            } else if (config instanceof FileSystemRuleSetConfiguration) {
                serializeFS(builder, (FileSystemRuleSetConfiguration) config);
            } else {
                throw new IllegalArgumentException("Unexpected rule set configuration: " + config.getClass().getSimpleName());
            }
            builder.append(CONFIGURATION_SEPARATOR);
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
    
    /**
     * Serializes a {@link WorkspaceRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code WS})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the workspace relative path to the configuration</li>
     * </ol>
     */
    private static void serializeWS(final StringBuilder builder, final WorkspaceRuleSetConfiguration config) {
        append(builder, Identifiers.WS.name(), String.valueOf(config.getId()), config.getName(), config.getLocation());
    }
    
    /**
     * Serializes a {@link ProjectRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code PJ})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the workspace relative path to the configuration</li>
     * </ol>
     */
    private static void serializePJ(final StringBuilder builder, final ProjectRuleSetConfiguration config) {
        append(builder, Identifiers.PJ.name(), String.valueOf(config.getId()), config.getName(), config.getLocation());
    }
    
    /**
     * Serializes a {@link FileSystemRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code FS})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the file system path to the configuration</li>
     * </ol>
     */
    private static void serializeFS(final StringBuilder builder, final FileSystemRuleSetConfiguration config) {
        append(builder, Identifiers.FS.name(), String.valueOf(config.getId()), config.getName(), config.getLocation());
    }
    
    /**
     * Appends the values to the StringBuilder. The values are separated by {@link #VALUE_SEPARATOR}.
     */
    private static void append(final StringBuilder builder, final String... values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(VALUE_SEPARATOR);
            }
            builder.append(values[i]);
        }
    }
    
    /**
     * Deserializes a String into a list of {@link RuleSetConfiguration}s.
     * 
     * @param s The serialized configurations. May be {@code null} or empty.
     * @return Never returns {@code null}.
     * @throws IllegalArgumentException Thrown when the provided String is not a valid serialization.
     * @see #serialize(ImmutableList)
     */
    public static ImmutableList<RuleSetConfiguration> deserialize(final String s) {
        final Builder<RuleSetConfiguration> configs = ImmutableList.builder();
        if (!Strings.isNullOrEmpty(s)) {
            for (final String serializedConfig : s.split(String.valueOf(CONFIGURATION_SEPARATOR))) {
                final String[] values = serializedConfig.split(String.valueOf(VALUE_SEPARATOR));
                if (isWorkspaceRuleSetConfiguration(values)) {
                    configs.add(deserializeWS(values));
                } else if (isProjectRuleSetConfiguration(values)) {
                    configs.add(deserializePJ(values));
                } else if (isFileSystemRuleSetConfiguration(values)) {
                    configs.add(deserializeFS(values));
                } else {
                    throw new IllegalArgumentException("Unexpected serialized rule set configuration: " + serializedConfig);
                }
            }
        }
        return configs.build();
    }
    
    private static boolean isWorkspaceRuleSetConfiguration(final String[] values) {
        return Identifiers.WS.name().equals(values[0]) && values.length == 4;
    }
    
    private static boolean isProjectRuleSetConfiguration(final String[] values) {
        return Identifiers.PJ.name().equals(values[0]) && values.length == 4;
    }
    
    private static boolean isFileSystemRuleSetConfiguration(final String[] values) {
        return Identifiers.FS.name().equals(values[0]) && values.length == 4;
    }
    
    /**
     * Deserializes a {@link WorkspaceRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code WS})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the file system path to the configuration</li>
     * </ol>
     * 
     * @param values The values of the serialized configuration. It is guaranteed to have the correct length.
     */
    private static RuleSetConfiguration deserializeWS(final String[] values) {
        return new WorkspaceRuleSetConfiguration(Integer.parseInt(values[1]), values[2], Paths.get(values[3]));
    }
    
    /**
     * Deserializes a {@link ProjectRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code PJ})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the file system path to the configuration</li>
     * </ol>
     * 
     * @param values The values of the serialized configuration. It is guaranteed to have the correct length.
     */
    private static RuleSetConfiguration deserializePJ(final String[] values) {
        return new ProjectRuleSetConfiguration(Integer.parseInt(values[1]), values[2], Paths.get(values[3]));
    }
    
    /**
     * Deserializes a {@link FileSystemRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code FS})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the file system path to the configuration</li>
     * </ol>
     * 
     * @param values The values of the serialized configuration. It is guaranteed to have the correct length.
     */
    private static RuleSetConfiguration deserializeFS(final String[] values) {
        return new FileSystemRuleSetConfiguration(Integer.parseInt(values[1]), values[2], Paths.get(values[3]));
    }
    
}
