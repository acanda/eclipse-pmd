// =====================================================================
//
// Copyright (C) 2012 - 2016, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.builder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

/**
 * Unit tests for {@link PMDNature}.
 * 
 * @author Philip Graf
 */
public class PMDNatureTest {
    
    /**
     * Verifies that {@link PMDNature#addTo(IProject)} appends the PMD nature to the list of other nature ids if the
     * project does not yet have it.
     */
    @Test
    public void addToAddsPMDNatureToProject() throws CoreException {
        final IProject project = mock(IProject.class);
        final IProjectDescription description = mock(IProjectDescription.class);
        when(project.getDescription()).thenReturn(description);
        when(project.hasNature(PMDNature.ID)).thenReturn(false);
        when(description.getNatureIds()).thenReturn(new String[] { "org.example.a", "org.example.b" });
        
        PMDNature.addTo(project);
        
        verify(project, times(1)).setDescription(same(description), any(IProgressMonitor.class));
        verify(description, times(1)).setNatureIds(eq(new String[] { "org.example.a", "org.example.b", PMDNature.ID }));
    }
    
    /**
     * Verifies that {@link PMDNature#addTo(IProject)} does not change the nature ids if the project already has it.
     */
    @Test
    public void addToDoesNotAddPMDNatureToProject() throws CoreException {
        final IProject project = mock(IProject.class);
        final IProjectDescription description = mock(IProjectDescription.class);
        when(project.getDescription()).thenReturn(description);
        when(project.hasNature(PMDNature.ID)).thenReturn(true);
        when(description.getNatureIds()).thenReturn(new String[] { "org.example.a", PMDNature.ID, "org.example.b" });
        
        PMDNature.addTo(project);
        
        verify(project, never()).setDescription(any(IProjectDescription.class), any(IProgressMonitor.class));
        verify(description, never()).setNatureIds(any(String[].class));
    }
    
    /**
     * Verifies that {@link PMDNature#removeFrom(IProject)} removes the PMD nature if the project already has it and
     * that it keeps the remaining nature ids in the same order.
     */
    @Test
    public void removeFromRemovesPMDNatureFromProject() throws CoreException {
        final IProject project = mock(IProject.class);
        final IProjectDescription description = mock(IProjectDescription.class);
        when(project.getDescription()).thenReturn(description);
        when(project.hasNature(PMDNature.ID)).thenReturn(true);
        when(description.getNatureIds()).thenReturn(new String[] { "org.example.a", PMDNature.ID, "org.example.b" });
        
        PMDNature.removeFrom(project);
        
        verify(project, times(1)).setDescription(same(description), any(IProgressMonitor.class));
        verify(description, times(1)).setNatureIds(eq(new String[] { "org.example.a", "org.example.b" }));
    }
    
    /**
     * Verifies that {@link PMDNature#removeFrom(IProject)} does not change the nature ids if the project does not have
     * the PMD nature.
     */
    @Test
    public void removeFromDoesNotRemovePMDNatureFromProject() throws CoreException {
        final IProject project = mock(IProject.class);
        final IProjectDescription description = mock(IProjectDescription.class);
        when(project.getDescription()).thenReturn(description);
        when(project.hasNature(PMDNature.ID)).thenReturn(false);
        when(description.getNatureIds()).thenReturn(new String[] { "org.example.a", "org.example.b" });
        
        PMDNature.removeFrom(project);
        
        verify(project, never()).setDescription(any(IProjectDescription.class), any(IProgressMonitor.class));
        verify(description, never()).setNatureIds(any(String[].class));
    }
    
}
