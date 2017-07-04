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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;

/**
 * Unit tests for {@link V07ToV08Converter}.
 * 
 * @author Philip Graf
 */

@SuppressWarnings("deprecation")
public class V07ToV08ConverterTest {
    
    @Test
    public void convertWorkspaceLocation() {
        final RuleSetConfiguration config =
                new WorkspaceRuleSetConfiguration(1, "Workspace Config", Paths.get("../src/ch.acanda.eclipse.pmd/pmd.xml"));
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(workspaceRoot.getLocationURI()).thenReturn(URI.create("file:///home/workspace/"));
        doReturn(createProjects()).when(workspaceRoot).getProjects();
        
        final Location result = V07ToV08Converter.getLocation(config, workspaceRoot);
        
        assertEquals("Location path", Paths.get("ch.acanda.eclipse.pmd", "pmd.xml").toString(), result.getPath());
        assertEquals("Location context", LocationContext.WORKSPACE, result.getContext());
    }
    
    @Test
    public void convertWorkspaceLocationFallback() {
        final RuleSetConfiguration config =
                new WorkspaceRuleSetConfiguration(1, "Workspace Config", Paths.get("../somewhere/else/pmd.xml"));
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(workspaceRoot.getLocationURI()).thenReturn(URI.create("file:///home/workspace/"));
        doReturn(createProjects()).when(workspaceRoot).getProjects();
        
        final Location result = V07ToV08Converter.getLocation(config, workspaceRoot);
        
        assertEquals("Location path", Paths.get("/home", "somewhere", "else", "pmd.xml").toString(), result.getPath());
        assertEquals("Location context", LocationContext.FILE_SYSTEM, result.getContext());
    }

    @Test
    public void convertWorkspaceLocationProjectInWorkspace() {
        final RuleSetConfiguration config =
                new WorkspaceRuleSetConfiguration(1, "Workspace Config", Paths.get("ch.acanda.eclipse.pmd/pmd.xml"));
        final IWorkspaceRoot workspaceRoot = mock(IWorkspaceRoot.class);
        when(workspaceRoot.getLocationURI()).thenReturn(URI.create("file:///home/src/"));
        doReturn(createProjects()).when(workspaceRoot).getProjects();
        
        final Location result = V07ToV08Converter.getLocation(config, workspaceRoot);
        
        assertEquals("Location path", Paths.get("ch.acanda.eclipse.pmd", "pmd.xml").toString(), result.getPath());
        assertEquals("Location context", LocationContext.WORKSPACE, result.getContext());
    }

    private IProject[] createProjects() {
        return new IProject[] {
                mockProject("ch.acanda.eclipse"),
                mockProject("ch.acanda.eclipse.pmd.core"),
                mockProject("ch.acanda.eclipse.pmd"),
        };
    }

    private IProject mockProject(final String name) {
        final IProject project = mock(IProject.class);
        when(project.getName()).thenReturn(name);
        when(project.getLocationURI()).thenReturn(URI.create("file:///home/src/" + name + "/"));
        return project;
    }
}
