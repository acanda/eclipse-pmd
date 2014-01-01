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

import java.nio.file.Paths;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Serializes and deserializes a {@link RuleSetConfiguration} to and from a String.
 * 
 * @author Philip Graf
 */
@Deprecated
@SuppressWarnings("PMD.TooManyMethods")
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
        FS,
        
        /**
         * Identifier for the {@link RemoteRuleSetConfiguration}.
         */
        RM
        
    };
    
    private RuleSetConfigurationSerializer() {
        // hide constructor of singleton
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
                } else if (isRemoteRuleSetConfiguration(values)) {
                    configs.add(deserializeRM(values));
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
    
    private static boolean isRemoteRuleSetConfiguration(final String[] values) {
        return Identifiers.RM.name().equals(values[0]) && values.length == 4;
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
    
    /**
     * Deserializes a {@link RemoteRuleSetConfiguration}. The serialized record consists of four values:
     * <ol>
     * <li>the rule set configuration type ({@code RM})</li>
     * <li>the id</li>
     * <li>the name</li>
     * <li>the URI to the configuration</li>
     * </ol>
     * 
     * @param values The values of the serialized configuration. It is guaranteed to have the correct length.
     */
    private static RuleSetConfiguration deserializeRM(final String[] values) {
        return new RemoteRuleSetConfiguration(Integer.parseInt(values[1]), values[2], values[3]);
    }
    
}
