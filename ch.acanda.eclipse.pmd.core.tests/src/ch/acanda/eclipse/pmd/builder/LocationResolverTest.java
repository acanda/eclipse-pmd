// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.junit.Test;

import com.google.common.base.Optional;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;

/**
 * Unit tests for {@code LocationResolver}.
 * 
 * @author Philip Graf
 */
public class LocationResolverTest {
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} resolves the loacation in a file
     * system context correctly.
     */
    @Test
    public void resolveIfExistsFileSystemLocation() throws IOException {
        final Path ruleSetFile = Files.createTempFile(LocationResolverTest.class.getSimpleName(), ".xml");
        try {
            final Location location = new Location(ruleSetFile.toString(), LocationContext.FILE_SYSTEM);
            final IProject project = mock(IProject.class);
            
            final Optional<String> result = LocationResolver.resolveIfExists(location, project);
            
            assertTrue("A valid file system location should resolve", result.isPresent());
            assertEquals("The resolved location in a file system context should be the provided location",
                    ruleSetFile.toString(), result.get());
        } finally {
            Files.deleteIfExists(ruleSetFile);
        }
    }

    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not resolve the loacation in a
     * file system context when the file is mossing.
     */
    @Test
    public void resolveIfExistsFileSystemLocationWithMissingFile() {
        final Location location = new Location("/tmp/pmd.xml", LocationContext.FILE_SYSTEM);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a file
     * system context if the path is invalid.
     */
    @Test
    public void resolveIfExistsFileSystemLocationWithInvalidPath() {
        final Location location = new Location("\u0000:", LocationContext.FILE_SYSTEM);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} resolves the location in a remote
     * context correctly.
     */
    @Test
    public void resolveIfExistsRemoteLocation() throws IOException {
        final Path ruleSetFile = Files.createTempFile(LocationResolverTest.class.getSimpleName(), ".xml");
        try {
            final Location location = new Location(ruleSetFile.toUri().toString(), LocationContext.REMOTE);
            final IProject project = mock(IProject.class);
            
            final Optional<String> result = LocationResolver.resolveIfExists(location, project);
            
            assertTrue("A valid remote location should resolve", result.isPresent());
            assertEquals("The resolved location in a remote context should be the provided location",
                    ruleSetFile.toUri().toString(), result.get());
        } finally {
            Files.deleteIfExists(ruleSetFile);
        }
    }

    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not resolve the location in a
     * remote context when the file does not exist.
     */
    @Test
    public void resolveIfExistsRemoteLocationWithMissingFile() {
        final Location location = new Location("http://example.org/pmd.xml", LocationContext.REMOTE);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * remote context if the URI is invalid.
     */
    @Test
    public void resolveIfExistsRemoteLocationWithInvalidURI() {
        final Location location = new Location("http:#", LocationContext.REMOTE);
        final IProject project = mock(IProject.class);
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} resolves the location in a project
     * context correctly.
     */
    @Test
    public void resolveIfExistsProjectLocation() throws URISyntaxException, IOException {
        final Path ruleSetFile = Files.createTempFile(LocationResolverTest.class.getSimpleName(), ".xml");
        try {
            final Location location = new Location(ruleSetFile.getFileName().toString(), LocationContext.PROJECT);
            final IProject project = mock(IProject.class);
            when(project.getLocationURI()).thenReturn(ruleSetFile.getParent().toUri());
            
            final Optional<String> result = LocationResolver.resolveIfExists(location, project);
            
            assertTrue("A valid project location should resolve", result.isPresent());
            assertEquals("The resolved location in a project context should be the provided location appended to the project location",
                    ruleSetFile.toString(), result.get());
        } finally {
            Files.deleteIfExists(ruleSetFile);
        }
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not resolve the location in a
     * project context when the rule set file does not exist.
     */
    @Test
    public void resolveIfExistsProjectLocationWithMissingFile() throws URISyntaxException {
        final Location location = new Location("pmd.xml", LocationContext.PROJECT);
        final IProject project = mock(IProject.class);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * project context if the path is invalid.
     */
    @Test
    public void resolveIfExistsProjectLocationWithInvalidPath() throws URISyntaxException {
        final Location location = new Location("\u0000:", LocationContext.PROJECT);
        final IProject project = mock(IProject.class);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} resolves the location in a workspace
     * context correctly.
     */
    @Test
    public void resolveIfExistsWorkspaceLocation() throws URISyntaxException, IOException {
        final Path ruleSetFile = Files.createTempFile(LocationResolverTest.class.getSimpleName(), ".xml");
        try {
            final Location location = new Location("project/" + ruleSetFile.getFileName().toString(), LocationContext.WORKSPACE);
            final IProject project = mock(IProject.class);
            final IWorkspace workspace = mock(IWorkspace.class);
            final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
            when(project.getWorkspace()).thenReturn(workspace);
            when(workspace.getRoot()).thenReturn(workspaceRoot);
            when(workspaceRoot.getProject("project")).thenReturn(project);
            when(project.getLocationURI()).thenReturn(ruleSetFile.getParent().toUri());
            
            final Optional<String> result = LocationResolver.resolveIfExists(location, project);
            
            assertTrue("A valid workspace location should resolve", result.isPresent());
            assertEquals("The resolved location in a workspace context should be the provided location appended to the workspace location",
                    ruleSetFile.toString(), result.get());
        } finally {
            Files.deleteIfExists(ruleSetFile);
        }
    }

    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * workspace context if the rule set file does not exist.
     */
    @Test
    public void resolveIfExistsWorkspaceLocationWithMissingFile() throws URISyntaxException {
        final Location location = new Location("project/pmd.xml", LocationContext.WORKSPACE);
        final IProject project = createProject("project", new URI("file:///workspace/project"));
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }

    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * workspace context if the project does not exist.
     */
    @Test
    public void resolveIfExistsWorkspaceLocationWithMissingProject() {
        final Location location = new Location("MissingProject/pmd.xml", LocationContext.WORKSPACE);
        final IProject project = createProject("MissingProject", null);
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }

    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * workspace context if the path contains invalid characters.
     */
    @Test
    public void resolveIfExistsWorkspaceLocationWithInvalidPath() throws URISyntaxException {
        final Location location = new Location("project/\u0000:", LocationContext.WORKSPACE);
        final IProject project = createProject("project", new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }

    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * workspace context if the path consists only of the project name.
     */
    @Test
    public void resolveIfExistsWorkspaceLocationWithProjectNameOnly() throws URISyntaxException {
        final Location location = new Location("project", LocationContext.WORKSPACE);
        final IProject project = createProject("project", new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolveIfExists(Location, IProject)} does not throw an exception in a
     * workspace context if the path is empty.
     */
    @Test
    public void resolveIfExistsWorkspaceLocationWithoutPath() throws URISyntaxException {
        final Location location = new Location("", LocationContext.WORKSPACE);
        final IProject project = createProject("project", new URI("file:///workspace/project/"));
        
        final Optional<String> result = LocationResolver.resolveIfExists(location, project);
        
        assertFalse("The location should not resolve", result.isPresent());
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the location in a project context
     * correctly.
     */
    @Test
    public void resolveWorkspaceLocation() throws URISyntaxException {
        final Location location = new Location("project/path/pmd.xml", LocationContext.WORKSPACE);
        final IProject project = createProject("project", new URI("file:///workspace/project/"));
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location should be the project's path with the location's path appended",
                Paths.get("/workspace", "project", "path", "pmd.xml"), Paths.get(result));
    }
    
    private static IProject createProject(final String name, final URI uri) {
        final IProject project = mock(IProject.class);
        final IWorkspace workspace = mock(IWorkspace.class);
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(project.getWorkspace()).thenReturn(workspace);
        when(workspace.getRoot()).thenReturn(workspaceRoot);
        when(workspaceRoot.getProject(name)).thenReturn(project);
        when(project.getLocationURI()).thenReturn(uri);
        return project;
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the location in a project context
     * correctly.
     */
    @Test
    public void resolveProjectLocation() throws URISyntaxException {
        final Location location = new Location("path/pmd.xml", LocationContext.PROJECT);
        final IProject project = mock(IProject.class);
        when(project.getLocationURI()).thenReturn(new URI("file:///workspace/project/"));
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location should be the project's path with the location's path appended",
                Paths.get("/workspace", "project", "path", "pmd.xml"), Paths.get(result));
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the location in a file system context
     * correctly.
     */
    @Test
    public void resolveFileSystemLocation() {
        final Location location = new Location("/some/path/pmd.xml", LocationContext.FILE_SYSTEM);
        final IProject project = mock(IProject.class);
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location should just be the path", "/some/path/pmd.xml", result);
    }
    
    /**
     * Verifies that {@link LocationResolver#resolve(Location, IProject)} resolves the location in a remote context
     * correctly.
     */
    @Test
    public void resolveRemoteLocation() {
        final Location location = new Location("http://example.org/pmd.xml", LocationContext.FILE_SYSTEM);
        final IProject project = mock(IProject.class);
        
        final String result = LocationResolver.resolve(location, project);
        
        assertEquals("The resolved location should be the URL", "http://example.org/pmd.xml", result);
    }
}
