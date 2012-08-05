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

package ch.acanda.eclipse.pmd.domain;

import java.nio.file.Path;

import net.sourceforge.pmd.RuleSetFactory;

/**
 * @author Philip Graf
 */
public abstract class RuleSetConfiguration {
    
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
     * @return The path to the rule set configuration. This will be used as input for the {@link RuleSetFactory}.
     */
    public abstract Path getConfiguration();
    
}
