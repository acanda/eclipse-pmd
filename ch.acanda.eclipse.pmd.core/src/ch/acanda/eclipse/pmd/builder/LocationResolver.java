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

package ch.acanda.eclipse.pmd.builder;

import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;

import ch.acanda.eclipse.pmd.domain.Location;

/**
 * @author Philip Graf
 */
public final class LocationResolver {

    private LocationResolver() {
        // hide constructor of utility class
    }

    public static String resolve(final Location location, final IProject project) {
        final String path;
        switch (location.getContext()) {
            case WORKSPACE:
                path = Paths.get(project.getWorkspace().getRoot().getLocationURI()).resolve(location.getPath()).toString();
                break;

            case PROJECT:
                path = Paths.get(project.getLocationURI()).resolve(location.getPath()).toString();
                break;

            case FILESYSTEM:
            case REMOTE:
                path = location.getPath();
                break;

            default:
                throw new IllegalStateException("Unknown location context: " + location.getContext());
        }
        return path;
    }

}
