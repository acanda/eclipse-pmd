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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;

import com.google.common.base.Optional;

/**
 * Unit tests for {@code LocationResolver}.
 * 
 * @author Philip Graf
 */
public class LocationResolverTest {
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the loacation in a file system
     * context correctly.
     */
    @Test
    public void resolveFileSystemLocation() {
        final Location location = new Location("/tmp/pmd.xml", LocationContext.FILESYSTEM);
        final IProject project = mock(IProject.class);

        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertTrue("A valid file system location should resolve", result.isPresent());
        assertEquals("The resolved location in a file system context should be the provided location",
                Paths.get("/tmp", "pmd.xml").toString(), result.get());
    }

    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} does not throw an exception in a file system
     * context if the path is invalid.
     */
    @Test
    public void resolveFileSystemLocationWithInvalidPath() {
        final Location location = new Location("/tmp/?", LocationContext.FILESYSTEM);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the loacation in a remote context
     * correctly.
     */
    @Test
    public void resolveRemoteLocation() {
        final Location location = new Location("http://example.org/pmd.xml", LocationContext.REMOTE);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertTrue("A valid remote location should resolve", result.isPresent());
        assertEquals("The resolved location in a remote context should be the provided location", "http://example.org/pmd.xml",
                result.get());
    }

    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} does not throw an exception in a remote
     * context if the URI is invalid.
     */
    @Test
    public void resolveRemoteLocationWithInvalidURI() {
        final Location location = new Location("http:#", LocationContext.REMOTE);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the location in a project context
     * correctly.
     */
    @Test
    public void resolveProjectLocation() throws URISyntaxException {
        final Location location = new Location("pmd.xml", LocationContext.PROJECT);
        final IProject project = mock(IProject.class);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertTrue("A valid project location should resolve", result.isPresent());
        assertEquals("The resolved location in a project context should be the provided location appended to the project location",
                Paths.get("/workspace", "project", "pmd.xml").toString(), result.get());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} does not throw an exception in a project
     * context if the path is invalid.
     */
    @Test
    public void resolveProjectLocationWithInvalidPath() throws URISyntaxException {
        final Location location = new Location("pmd.xml?", LocationContext.PROJECT);
        final IProject project = mock(IProject.class);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the location in a workspace context
     * correctly.
     */
    @Test
    public void resolveWorkspaceLocation() throws URISyntaxException {
        final Location location = new Location("project/pmd.xml", LocationContext.WORKSPACE);
        final IProject project = mock(IProject.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(project.getWorkspace()).thenReturn(workspace);
        when(workspace.getRoot()).thenReturn(workspaceRoot);
        when(workspaceRoot.getProject("project")).thenReturn(project);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertTrue("A valid workspace location should resolve", result.isPresent());
        assertEquals("The resolved location in a workspace context should be the provided location appended to the workspace location",
                Paths.get("/workspace", "project", "pmd.xml").toString(), result.get());
    }

    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} does not throw an exception in a workspace
     * context if the project does not exist.
     */
    @Test
    public void resolveWorkspaceLocationWithMissingProject() {
        final Location location = new Location("MissingProject/pmd.xml", LocationContext.WORKSPACE);
        final IProject project = mock(IProject.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(project.getWorkspace()).thenReturn(workspace);
        when(workspace.getRoot()).thenReturn(workspaceRoot);
        when(workspaceRoot.getProject("MissingProject")).thenReturn(project);
        when(project.getLocationURI()).thenReturn(null);
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }

    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} does not throw an exception in a workspace
     * context if the path contains invalid characters.
     */
    @Test
    public void resolveWorkspaceLocationWithInvalidPath() throws URISyntaxException {
        final Location location = new Location("project/pmd.xml?", LocationContext.WORKSPACE);
        final IProject project = mock(IProject.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(project.getWorkspace()).thenReturn(workspace);
        when(workspace.getRoot()).thenReturn(workspaceRoot);
        when(workspaceRoot.getProject("project")).thenReturn(project);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolve(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }

}
