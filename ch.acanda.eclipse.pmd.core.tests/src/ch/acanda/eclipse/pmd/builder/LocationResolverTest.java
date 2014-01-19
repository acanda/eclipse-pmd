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

import static org.junit.Assert.assertEquals;
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

        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location in a file system context should be the provided location", "/tmp/pmd.xml", result);
    }

    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the loacation in a remote context
     * correctly.
     */
    @Test
    public void resolveRemoteLocation() {
        final Location location = new Location("http://example.org/pmd.xml", LocationContext.REMOTE);
        final IProject project = mock(IProject.class);
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location in a remote context should be the provided location", "http://example.org/pmd.xml", result);
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
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location in a project context should be the provided location appended to the project location",
                     Paths.get("/workspace", "project", "pmd.xml").toString(), result);
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
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location in a workspace context should be the provided location appended to the workspace location",
                     Paths.get("/workspace", "project", "pmd.xml").toString(), result);
    }

}
