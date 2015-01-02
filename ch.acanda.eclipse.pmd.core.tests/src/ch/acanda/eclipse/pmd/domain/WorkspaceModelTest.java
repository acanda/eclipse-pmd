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

package ch.acanda.eclipse.pmd.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.DomainModel.AddElementPropertyChangeEvent;
import ch.acanda.eclipse.pmd.domain.DomainModel.RemoveElementPropertyChangeEvent;

import com.google.common.base.Optional;

/**
 * Unit tests for {@link WorkspaceModel}.
 * 
 * @author Philip Graf
 */
public class WorkspaceModelTest {
    
    /**
     * Verifies that {@link WorkspaceModel#getProjects()} returns an empty set when there aren't any projects in the
     * workspace.
     */
    @Test
    public void whenThereAreNoProjectsSetProjectsReturnsAnEmptySet() {
        assertTrue("When there areen't any projects, getProjects() should return an empty set",
                   new WorkspaceModel().getProjects().isEmpty());
    }
    
    /**
     * Verifies that an event is fired when adding a project model.
     */
    @Test
    public void addFiresAnAddElementPropertyChangeEvent() {
        final WorkspaceModel model = new WorkspaceModel();
        final boolean[] eventFired = new boolean[1];
        final ProjectModel element = new ProjectModel("Foo");
        model.addPropertyChangeListener(WorkspaceModel.PROJECTS_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                eventFired[0] = true;
                assertTrue("The event should be an AddElementPropertyChangeEvent", event instanceof AddElementPropertyChangeEvent);
                assertSame("Event's added element", element, ((AddElementPropertyChangeEvent) event).getAddedElement());
                assertNull("Event's old value should be null", event.getOldValue());
                assertSame("Event's new value should be the added element", element, event.getNewValue());
            }
        });

        model.add(element);

        assertTrue("An event should be fired when adding a project model", eventFired[0]);
    }
    
    /**
     * Verifies that an event is fired when removing a project model.
     */
    @Test
    public void removeFiresARemoveElementPropertyChangeEvent() {
        final WorkspaceModel model = new WorkspaceModel();
        final boolean[] eventFired = new boolean[1];
        final ProjectModel element = new ProjectModel("Foo");
        model.add(element);
        model.addPropertyChangeListener(WorkspaceModel.PROJECTS_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent event) {
                eventFired[0] = true;
                assertTrue("The event should be an RemoveElementPropertyChangeEvent", event instanceof RemoveElementPropertyChangeEvent);
                assertSame("Event's added element", element, ((RemoveElementPropertyChangeEvent) event).getRemovedElement());
                assertSame("Event's old value should be the removed element", element, event.getOldValue());
                assertNull("Event's new value should be null", event.getNewValue());
            }
        });

        model.remove(element.getProjectName());

        assertTrue("An event should be fired when removing a project model", eventFired[0]);
    }
    
    /**
     * Verifies that no event is fired when removing an inexistent project model.
     */
    @Test
    public void removeDoesNotFireARemoveElementPropertyChangeEventForInexistentProject() {
        final WorkspaceModel model = new WorkspaceModel();
        final boolean[] eventFired = new boolean[1];
        model.addPropertyChangeListener(WorkspaceModel.PROJECTS_PROPERTY, event -> eventFired[0] = true);

        model.remove("Bar");
        
        assertFalse("An event should not be fired when removing an inexistent project model", eventFired[0]);
    }
    
    /**
     * Verifies that {@link WorkspaceModel#getProject(String)} returns {@code Optional.absent()} when the requested
     * project model does not exist.
     */
    @Test
    public void getProjectReturnsOptionalAbsentWhenProjectModelDoesNotExist() {
        final WorkspaceModel model = new WorkspaceModel();

        final Optional<ProjectModel> actual = model.getProject("Foo");

        assertNotNull("WorkspaceModel.getProject(...) must never return null", actual);
        assertFalse("The project model should not be present", actual.isPresent());
    }
    
    /**
     * Verifies that {@link WorkspaceModel#getProject(String)} returns the requested project model.
     */
    @Test
    public void getProjectReturnsRequestedProjectModel() {
        final WorkspaceModel model = new WorkspaceModel();
        final ProjectModel expected = new ProjectModel("Foo");
        model.add(expected);

        final Optional<ProjectModel> actual = model.getProject(expected.getProjectName());

        assertNotNull("WorkspaceModel.getProject(...) must never return null", actual);
        assertTrue("The project model should be present", actual.isPresent());
        assertSame("WorkspaceModel.getProject(...) should return the requested project model", expected, actual.get());
    }
    
    /**
     * Verifies that {@link WorkspaceModel#getOrCreateProject(String)} returns a new project model when the requested
     * project model does not yet exist.
     */
    @Test
    public void getOrCreateProjectReturnsOptionalAbsentWhenProjectModelDoesNotExist() {
        final WorkspaceModel model = new WorkspaceModel();
        
        final ProjectModel actual = model.getOrCreateProject("Foo");
        
        assertNotNull("WorkspaceModel.getOrCreateProject(...) must never return null", actual);
        assertEquals("Project model name", "Foo", actual.getProjectName());
        assertSame("WorkspaceModel.getOrCreateProject(...) should add the created project model", actual, model.getProject("Foo").get());
    }
    
    /**
     * Verifies that {@link WorkspaceModel#getOrCreateProject(String)} returns the requested project model when it
     * already exists.
     */
    @Test
    public void getOrCreateProjectReturnsRequestedProjectModel() {
        final WorkspaceModel model = new WorkspaceModel();
        final ProjectModel expected = new ProjectModel("Foo");
        model.add(expected);
        
        final ProjectModel actual = model.getOrCreateProject(expected.getProjectName());
        
        assertNotNull("WorkspaceModel.getOrCreateProject(...) must never return null", actual);
        assertSame("WorkspaceModel.getOrCreateProject(...) should return the requested project model", expected, actual);
    }
    
}
