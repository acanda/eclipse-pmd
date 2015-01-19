// =====================================================================
//
// Copyright (C) 2012 - 2015, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.builder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;

import ch.acanda.eclipse.pmd.domain.Location;

import com.google.common.base.Optional;

/**
 * @author Philip Graf
 */
public final class LocationResolver {

    private LocationResolver() {
        // hide constructor of utility class
    }

    public static Optional<String> resolve(final Location location, final IProject project) {
        final Optional<String> path;
        switch (location.getContext()) {
            case WORKSPACE:
                path = resolveWorkspaceLocation(location, project);
                break;

            case PROJECT:
                path = resolveProjectLocation(location, project);
                break;

            case FILESYSTEM:
                path = resolveFileSystemLocation(location);
                break;

            case REMOTE:
                path = resolveRemoteLocation(location);
                break;

            default:
                throw new IllegalStateException("Unknown location context: " + location.getContext());
        }
        return path;
    }

    private static Optional<String> resolveWorkspaceLocation(final Location location, final IProject project) {
        try {
            // format of the location's path: <project-name>/<project-relative-path>
            final Path locationPath = Paths.get(toOSPath(location.getPath()));
            final String projectName = locationPath.getName(0).toString();
            final Path projectRelativePath = locationPath.subpath(1, locationPath.getNameCount());
            final IWorkspaceRoot root = project.getWorkspace().getRoot();
            final URI locationURI = root.getProject(projectName).getLocationURI();
            if (locationURI != null) {
                return Optional.of(Paths.get(locationURI).resolve(projectRelativePath).toString());
            }
            return Optional.absent();
        } catch (final InvalidPathException e) {
            return Optional.absent();
        }
    }

    private static Optional<String> resolveProjectLocation(final Location location, final IProject project) {
        try {
            return Optional.of(Paths.get(project.getLocationURI()).resolve(toOSPath(location.getPath())).toString());
        } catch (final InvalidPathException e) {
            return Optional.absent();
        }
    }

    private static Optional<String> resolveFileSystemLocation(final Location location) {
        try {
            return Optional.of(Paths.get(location.getPath()).toString());
        } catch (final InvalidPathException e) {
            return Optional.absent();
        }
    }

    private static Optional<String> resolveRemoteLocation(final Location location) {
        try {
            return Optional.of(new URI(location.getPath()).toString());
        } catch (final URISyntaxException e) {
            return Optional.absent();
        }
    }

    private static String toOSPath(final String path) {
        return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }

}
