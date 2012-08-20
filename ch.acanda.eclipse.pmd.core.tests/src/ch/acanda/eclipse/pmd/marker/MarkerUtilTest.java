// =====================================================================
//
// Copyright (C) 2012, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.marker;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import ch.acanda.eclipse.pmd.marker.MarkerUtil;

/**
 * Unit tests for {@link MarkerUtil}.
 * 
 * @author Philip Graf
 */
public class MarkerUtilTest {
    
    private static final String MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdMarker";

    /**
     * Verifies that {@link MarkerUtil#removeAllMarkers(IFile)} removes all markers from a file.
     */
    @Test
    public void removeAllMarkersIFile() throws CoreException {
        final IFile file = mock(IFile.class);
        MarkerUtil.removeAllMarkers(file);
        verify(file).deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_ZERO);
    }
    
    /**
     * Verifies that {@linkMarkerUtil#removeAllMarkers(IProject)} removes all markers from a project and its files.
     */
    @Test
    public void removeAllMarkersIProject() throws CoreException {
        final IProject project = mock(IProject.class);
        MarkerUtil.removeAllMarkers(project);
        verify(project).deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
    }
    
    // /**
    // * Verifies that {@link MarkerUtil#addMarker(IFile, String, String, int, int, int)} adds a marker to the provided
    // file.
    // */
    // @Test
    // public void addMarker() throws CoreException {
    // final IFile file = mock(IFile.class);
    // final IMarker marker = mock(IMarker.class);
    // when(file.createMarker(MARKER_TYPE)).thenReturn(marker);
    // final IMarker actual = MarkerUtil.addMarker(file, "rule", "message", 1, 2, 3);
    // assertNotNull("The method must always return a marker", actual);
    // verify(file).createMarker(MARKER_TYPE);
    // verify(actual).setAttribute(IMarker.MESSAGE, "message");
    // verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
    // verify(actual).setAttribute(IMarker.LINE_NUMBER, 1);
    // verify(actual).setAttribute(IMarker.CHAR_START, 2);
    // verify(actual).setAttribute(IMarker.CHAR_END, 3);
    // verify(actual).setAttribute("ruleName", "rule");
    // }
    //
    // /**
    // * Verifies that {@link MarkerUtil#addMarker(IFile, String, String, int, int, int)} adds a marker to the provided
    // file with
    // * position information set to zero if the respective arguments are negative.
    // */
    // @Test
    // public void addMarkerWithUnknwonPositionInformation() throws CoreException {
    // final IFile file = mock(IFile.class);
    // final IMarker marker = mock(IMarker.class);
    // when(file.createMarker(MARKER_TYPE)).thenReturn(marker);
    // final IMarker actual = MarkerUtil.addMarker(file, "rule", "message", -1, -2, -3);
    // assertNotNull("The method must always return a marker", actual);
    // verify(file).createMarker(MARKER_TYPE);
    // verify(actual).setAttribute(IMarker.MESSAGE, "message");
    // verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
    // verify(actual).setAttribute(IMarker.LINE_NUMBER, 0);
    // verify(actual).setAttribute(IMarker.CHAR_START, 0);
    // verify(actual).setAttribute(IMarker.CHAR_END, 0);
    // }

}
