// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.v07tov08;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.resources.IProject;

import ch.acanda.eclipse.pmd.PMDPlugin;

@Deprecated
class RemoteRuleSetConfiguration extends RuleSetConfiguration {
    
    private final URI location;
    
    public RemoteRuleSetConfiguration(final int id, final String name, final String location) {
        super(id, name);
        this.location = URI.create(location);
    }
    
    @Override
    public String getType() {
        return "Remote";
    }
    
    @Override
    public String getLocation() {
        return location.toString();
    }
    
    @Override
    public Path getConfiguration(final IProject project) {
        Path path = null;
        try {
            path = Files.createTempFile("eclipse-pmd-remote-", ".xml");
            path.toFile().deleteOnExit();
            try {
                try (final InputStream stream = location.toURL().openStream()) {
                    Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (final IOException e) {
                PMDPlugin.getDefault().error("Cannot download configuration file from remote location " + location.toString(), e);
            }
        } catch (final IOException e) {
            PMDPlugin.getDefault().error("Cannot create temporary file " + location.toString(), e);
        }
        return path;
    }
    
}
