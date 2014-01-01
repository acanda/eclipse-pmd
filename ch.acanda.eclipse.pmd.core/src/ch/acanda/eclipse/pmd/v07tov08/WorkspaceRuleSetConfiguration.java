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

import java.nio.file.Path;

import org.eclipse.core.resources.IProject;

/**
 * A rule set configuration stored relative to the workspace.
 * 
 * @author Philip Graf
 */
@Deprecated
class WorkspaceRuleSetConfiguration extends RuleSetConfiguration {
    
    private final Path location;
    
    public WorkspaceRuleSetConfiguration(final int id, final String name, final Path relativeLocation) {
        super(id, name);
        location = relativeLocation.normalize();
    }
    
    @Override
    public String getType() {
        return "Workspace";
    }
    
    @Override
    public String getLocation() {
        return location.toString();
    }
    
    @Override
    public Path getConfiguration(final IProject project) {
        final Path workspace = project.getWorkspace().getRoot().getLocation().toFile().toPath();
        return workspace.resolve(location);
    }
    
}
