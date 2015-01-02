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

package ch.acanda.eclipse.pmd.v07tov08;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ProjectRuleSetConfiguration}.
 * 
 * @author Philip Graf
 */
@SuppressWarnings("deprecation")
public class ProjectRuleSetConfigurationTest {
    
    private static final int ID = 7;
    private static final String NAME = "Rule Set Name";
    private static final Path LOCATION = Paths.get("pmd.xml");
    
    private ProjectRuleSetConfiguration config;

    @Before
    public void setUp() {
        config = new ProjectRuleSetConfiguration(ID, NAME, LOCATION);
    }

    /**
     * Verifies that {@link ProjectRuleSetConfiguration#getType()} returns the correct, human readable type.
     */
    @Test
    public void getType() {
        assertEquals("Type of rule set configuration", "Project", config.getType());
    }
    
    /**
     * Verifies that {@link ProjectRuleSetConfiguration#getLocation()} returns the project relative location.
     */
    @Test
    public void getLocation() {
        assertEquals("Location of rule set configuration", LOCATION.toString(), config.getLocation());
    }
    
    /**
     * Verifies that {@link ProjectRuleSetConfiguration#getConfiguration(IProject)} returns the absolute path to the
     * rule set configuration.
     */
    @Test
    public void getConfiguration() {
        final IProject project = mock(IProject.class);
        final IPath location = mock(IPath.class);
        final File projectDirectory = new File(".");
        when(location.toFile()).thenReturn(projectDirectory);
        when(project.getLocation()).thenReturn(location);
        
        final Path actual = config.getConfiguration(project);

        final Path expected = projectDirectory.toPath().resolve(LOCATION);
        assertEquals("Absolute path to the rule set configuration", expected, actual);
    }
    
    /**
     * Verifies that {@link RuleSetConfiguration#getId()} returns the correct id.
     */
    @Test
    public void getId() {
        assertEquals("Id of the rule set configuration", ID, config.getId());
    }
    
    /**
     * Verifies that {@link RuleSetConfiguration#getName()} return the correct name.
     */
    @Test
    public void getName() {
        assertEquals("Name of the rule set configuration", NAME, config.getName());
    }
    
}
