// =====================================================================
//
// Copyright (C) 2012 - 2016, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.domain;


public class Location {
    
    private final String path;
    private final LocationContext context;
    
    public Location(final String path, final LocationContext context) {
        this.path = path;
        this.context = context;
    }
    
    public String getPath() {
        return path;
    }
    
    public LocationContext getContext() {
        return context;
    }
    
}