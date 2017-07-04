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

package ch.acanda.eclipse.pmd.builder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;

import ch.acanda.eclipse.pmd.domain.Location;

import com.google.common.base.Optional;

/**
 * Utility class to resolve a {@link Location}.
 *
 * @author Philip Graf
 */
public final class LocationResolver {

    private LocationResolver() {
        // hide constructor of utility class
    }

    /**
     * Resolves the location and checks if it exist.
     *
     * @return The absolute location if it exist or {@code Optional#absent()} if it doesn't.
     */
    public static Optional<String> resolveIfExists(final Location location, final IProject project) {
        final Optional<String> path;
        switch (location.getContext()) {
            case WORKSPACE:
                path = resolveWorkspaceLocationIfExists(location, project);
                break;

            case PROJECT:
                path = resolveProjectLocationIfExists(location, project);
                break;

            case FILE_SYSTEM:
                path = resolveFileSystemLocationIfExists(location);
                break;

            case REMOTE:
                path = resolveRemoteLocationIfExists(location);
                break;

            default:
                throw new IllegalStateException("Unknown location context: " + location.getContext());
        }
        return path;
    }

    /**
     * Resolves a location. Unlike {@link #resolveIfExists(Location, IProject)} this also resolves if the location does
     * not exist.
     *
     * @return The absolute location or {@code null} if it cannot be resolved.
     */
    public static String resolve(final Location location, final IProject project) {
        final String resolvedLocation;
        switch (location.getContext()) {
            case WORKSPACE:
                final Path workspacePath = resolveWorkspaceLocation(location, project);
                resolvedLocation = workspacePath == null ? null : workspacePath.toString();
                break;

            case PROJECT:
                final Path path = Paths.get(project.getLocationURI()).resolve(toOSPath(location.getPath()));
                resolvedLocation = path.normalize().toString();
                break;

            case FILE_SYSTEM:
            case REMOTE:
                resolvedLocation = location.getPath();
                break;

            default:
                throw new IllegalStateException("Unknown location context: " + location.getContext());
        }
        return resolvedLocation;
    }

    private static Optional<String> resolveWorkspaceLocationIfExists(final Location location, final IProject project) {
        try {
            final Path path = resolveWorkspaceLocation(location, project);
            if (path != null && Files.exists(path)) {
                return Optional.of(path.toString());
            }
            return Optional.absent();
        } catch (final InvalidPathException e) {
            return Optional.absent();
        }
    }

    private static Path resolveWorkspaceLocation(final Location location, final IProject project) {
        // format of the location's path: <project-name>/<project-relative-path>
        if (location.getPath().trim().length() > 0) {
            final Path locationPath = Paths.get(toOSPath(location.getPath()));
            if (locationPath.getNameCount() >= 2) {
                final String projectName = locationPath.getName(0).toString();
                final IWorkspaceRoot root = project.getWorkspace().getRoot();
                final URI locationURI = root.getProject(projectName).getLocationURI();
                if (locationURI != null) {
                    final Path projectRelativePath = locationPath.subpath(1, locationPath.getNameCount());
                    return Paths.get(locationURI).resolve(projectRelativePath);
                }
            }
        }
        return null;
    }

    private static Optional<String> resolveProjectLocationIfExists(final Location location, final IProject project) {
        try {
            final Path path = Paths.get(project.getLocationURI()).resolve(toOSPath(location.getPath()));
            return Files.exists(path) ? Optional.of(path.toString()) : Optional.<String>absent();
        } catch (final InvalidPathException e) {
            return Optional.absent();
        }
    }

    private static Optional<String> resolveFileSystemLocationIfExists(final Location location) {
        try {
            final Path path = Paths.get(location.getPath());
            return Files.exists(path) ? Optional.of(path.toString()) : Optional.<String>absent();
        } catch (final InvalidPathException e) {
            return Optional.absent();
        }
    }

    private static Optional<String> resolveRemoteLocationIfExists(final Location location) {
        try {
            final URI uri = new URI(location.getPath());
            uri.toURL().openStream().close();
            return Optional.of(uri.toString());
        } catch (final URISyntaxException | IOException e) {
            return Optional.absent();
        }
    }

    private static String toOSPath(final String path) {
        return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }

}
