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

import java.io.File;
import java.nio.file.Path;
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
                // format of the location's path: <project-name>/<project-relative-path>
                final Path locationPath = Paths.get(toOSPath(location.getPath()));
                final IProject locationProject = project.getWorkspace().getRoot().getProject(locationPath.getName(0).toString());
                path = Paths.get(locationProject.getLocationURI())
                            .resolve(locationPath.subpath(1, locationPath.getNameCount()))
                            .toString();

                break;

            case PROJECT:
                path = Paths.get(project.getLocationURI()).resolve(toOSPath(location.getPath())).toString();
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

    private static String toOSPath(final String path) {
        return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }

}
