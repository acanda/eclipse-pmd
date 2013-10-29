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

import java.nio.file.Path;

import net.sourceforge.pmd.RuleSetFactory;

import org.eclipse.core.resources.IProject;

/**
 * @author Philip Graf
 */
@Deprecated
abstract class RuleSetConfiguration {
    
    private final int id;
    private final String name;
    
    public RuleSetConfiguration(final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * @return A human readable description of the type of rule set configuration.
     */
    public abstract String getType();
    
    /**
     * @return A human readable location of the rule set configuration.
     */
    public abstract String getLocation();
    
    /**
     * @param project The project for which the configuration will be used.
     * @return The absolute path to the rule set configuration. This will be used as input for the
     *         {@link RuleSetFactory}.
     */
    public abstract Path getConfiguration(IProject project);
    
}
