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

package ch.acanda.eclipse.pmd.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Utility for creating, adding and removing PMD markers.
 * 
 * @author Philip Graf
 */
public final class MarkerUtil {
    
    private static final String MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdMarker";
    
    /**
     * Removes all PMD markers from a file.
     */
    public static void removeAllMarkers(final IFile file) throws CoreException {
        file.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_ZERO);
    }
    
    /**
     * Removes all PMD markers from a project and all the files it contains.
     */
    public static void removeAllMarkers(final IProject project) throws CoreException {
        project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
    }
    
    /**
     * Adds a PMD Marker to a file.
     * 
     * @param file The marker will be added to this file.
     * @param message A human readable short description of the marker.
     * @param line The annotation of the marker will be shown a this line. The first line is zero. A negative number
     *            will be set to zero.
     * @param start The start of the violating code (number of characters from the start of the file). A negative number
     *            will be set to zero.
     * @param end The end of the violating code (number of characters from the start of the file). A negative number
     *            will be set to zero.
     * @return The created marker.
     * @throws CoreException Thrown when the file does not exist or its project is closed.
     */
    public static IMarker addMarker(final IFile file, final String message, final int line, final int start, final int end)
            throws CoreException {
        final IMarker marker = file.createMarker(MARKER_TYPE);
        marker.setAttribute(IMarker.MESSAGE, message);
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        marker.setAttribute(IMarker.LINE_NUMBER, Math.max(line, 0));
        marker.setAttribute(IMarker.CHAR_START, Math.max(start, 0));
        marker.setAttribute(IMarker.CHAR_END, Math.max(end, 0));
        return marker;
    }
    
}
