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

package ch.acanda.eclipse.pmd.domain;

public class RuleSetModel {
    
    private final String name;
    private final Location location;
    
    public RuleSetModel(final String name, final Location location) {
        this.name = name;
        this.location = location;
    }
    
    public String getName() {
        return name;
    }
    
    public Location getLocation() {
        return location;
    }
    
}