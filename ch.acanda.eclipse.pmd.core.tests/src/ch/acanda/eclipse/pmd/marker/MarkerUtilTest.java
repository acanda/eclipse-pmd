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

package ch.acanda.eclipse.pmd.marker;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

/**
 * Unit tests for {@link MarkerUtil}.
 * 
 * @author Philip Graf
 */
public class MarkerUtilTest {
    
    private static final String MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdMarker";
    private static final String LONG_MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdLongMarker";

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
    
    /**
     * Verifies that {@link MarkerUtil#addMarker(IFile, String, RuleViolation)} adds a marker to the provided file.
     */
    @Test
    public void addMarker() throws CoreException {
        final IFile file = mock(IFile.class);
        final IMarker marker = mock(IMarker.class);
        when(file.createMarker(MARKER_TYPE)).thenReturn(marker);
        final RuleViolation violation = mock(RuleViolation.class);
        when(violation.getDescription()).thenReturn("message");
        when(violation.getBeginLine()).thenReturn(1);
        when(violation.getBeginColumn()).thenReturn(17);
        when(violation.getEndLine()).thenReturn(1);
        when(violation.getEndColumn()).thenReturn(22);
        when(violation.getClassName()).thenReturn("ClassName");
        final Rule rule = mock(Rule.class);
        when(rule.getLanguage()).thenReturn(new JavaLanguageModule());
        when(rule.getRuleSetName()).thenReturn("basic");
        when(rule.getName()).thenReturn("ExtendsObject");
        when(violation.getRule()).thenReturn(rule);
        
        final IMarker actual = MarkerUtil.addMarker(file, "class A extends Object {}", violation);

        assertNotNull("The method must always return a marker", actual);
        verify(file).createMarker(MARKER_TYPE);
        verify(actual).setAttribute(IMarker.MESSAGE, "message");
        verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        verify(actual).setAttribute(IMarker.LINE_NUMBER, 1);
        verify(actual).setAttribute(IMarker.CHAR_START, 16);
        verify(actual).setAttribute(IMarker.CHAR_END, 22);
        verify(actual).setAttribute("ruleId", "java.basic.ExtendsObject");
        verify(actual).setAttribute("violationClassName", "ClassName");
        verify(actual).setAttribute("markerText", "Object");
    }
    
    /**
     * Verifies that {@link MarkerUtil#addMarker(IFile, String, RuleViolation)} adds a marker to the provided file with
     * the correct position if the content contains tabs and spaces.
     */
    @Test
    public void addMarkerWithTab() throws CoreException {
        final IFile file = mock(IFile.class);
        final IMarker marker = mock(IMarker.class);
        when(file.createMarker(MARKER_TYPE)).thenReturn(marker);
        final RuleViolation violation = mock(RuleViolation.class);
        when(violation.getDescription()).thenReturn("message");
        when(violation.getBeginLine()).thenReturn(1);
        when(violation.getBeginColumn()).thenReturn(25);
        when(violation.getEndLine()).thenReturn(1);
        when(violation.getEndColumn()).thenReturn(30);
        when(violation.getClassName()).thenReturn("ClassName");
        final Rule rule = mock(Rule.class);
        when(rule.getLanguage()).thenReturn(new JavaLanguageModule());
        when(rule.getRuleSetName()).thenReturn("basic");
        when(rule.getName()).thenReturn("ExtendsObject");
        when(violation.getRule()).thenReturn(rule);
        
        final IMarker actual = MarkerUtil.addMarker(file, "class ABC extends\tObject {}", violation);
        
        assertNotNull("The method must always return a marker", actual);
        verify(file).createMarker(MARKER_TYPE);
        verify(actual).setAttribute(IMarker.MESSAGE, "message");
        verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        verify(actual).setAttribute(IMarker.LINE_NUMBER, 1);
        verify(actual).setAttribute(IMarker.CHAR_START, 18);
        verify(actual).setAttribute(IMarker.CHAR_END, 24);
        verify(actual).setAttribute("ruleId", "java.basic.ExtendsObject");
        verify(actual).setAttribute("violationClassName", "ClassName");
        verify(actual).setAttribute("markerText", "Object");
    }
    
    /**
     * Verifies that {@link MarkerUtil#addMarker(IFile, String, RuleViolation)} adds a marker to the provided file with
     * position information set to zero if the respective arguments are negative.
     */
    @Test
    public void addMarkerWithUnknwonPositionInformation() throws CoreException {
        final IFile file = mock(IFile.class);
        final IMarker marker = mock(IMarker.class);
        when(file.createMarker(MARKER_TYPE)).thenReturn(marker);
        final RuleViolation violation = mock(RuleViolation.class);
        when(violation.getDescription()).thenReturn("message");
        when(violation.getBeginLine()).thenReturn(-1);
        when(violation.getBeginColumn()).thenReturn(-18);
        when(violation.getEndLine()).thenReturn(-1);
        when(violation.getEndColumn()).thenReturn(-24);
        when(violation.getClassName()).thenReturn("ClassName");
        final Rule rule = mock(Rule.class);
        when(rule.getLanguage()).thenReturn(new JavaLanguageModule());
        when(rule.getRuleSetName()).thenReturn("basic");
        when(rule.getName()).thenReturn("ExtendsObject");
        when(violation.getRule()).thenReturn(rule);
        
        final IMarker actual = MarkerUtil.addMarker(file, "class A extends Object {}", violation);
        
        assertNotNull("The method must always return a marker", actual);
        verify(file).createMarker(MARKER_TYPE);
        verify(actual).setAttribute(IMarker.MESSAGE, "message");
        verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        verify(actual).setAttribute(IMarker.LINE_NUMBER, 0);
        verify(actual).setAttribute(IMarker.CHAR_START, 0);
        verify(actual).setAttribute(IMarker.CHAR_END, 0);
        verify(actual).setAttribute("ruleId", "java.basic.ExtendsObject");
        verify(actual).setAttribute("violationClassName", "ClassName");
        verify(actual).setAttribute("markerText", "");
    }
    
    /**
     * Verifies that {@link MarkerUtil#addMarker(IFile, String, RuleViolation)} adds a marker to the provided file with
     * position information containing the line, the character start and end position but not the marker text if the
     * marker would span more than one line.
     */
    @Test
    public void addMarkerSpanningMoreThanOneLine() throws CoreException {
        final IFile file = mock(IFile.class);
        final IMarker marker = mock(IMarker.class);
        when(file.createMarker(LONG_MARKER_TYPE)).thenReturn(marker);
        final RuleViolation violation = mock(RuleViolation.class);
        when(violation.getDescription()).thenReturn("message");
        when(violation.getBeginLine()).thenReturn(1);
        when(violation.getBeginColumn()).thenReturn(24);
        when(violation.getEndLine()).thenReturn(2);
        when(violation.getEndColumn()).thenReturn(1);
        when(violation.getClassName()).thenReturn("ClassName");
        final Rule rule = mock(Rule.class);
        when(rule.getLanguage()).thenReturn(new JavaLanguageModule());
        when(rule.getRuleSetName()).thenReturn("basic");
        when(rule.getName()).thenReturn("ExtendsObject");
        when(violation.getRule()).thenReturn(rule);
        
        final IMarker actual = MarkerUtil.addMarker(file, "class A extends Object {\n}", violation);
        
        assertNotNull("The method must always return a marker", actual);
        verify(file).createMarker(LONG_MARKER_TYPE);
        verify(actual).setAttribute(IMarker.MESSAGE, "message");
        verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        verify(actual).setAttribute(IMarker.LINE_NUMBER, 1);
        verify(actual).setAttribute(IMarker.CHAR_START, 23);
        verify(actual).setAttribute(IMarker.CHAR_END, 26);
        verify(actual).setAttribute("ruleId", "java.basic.ExtendsObject");
        verify(actual).setAttribute("violationClassName", "ClassName");
        verify(actual, never()).setAttribute(eq("markerText"), anyString());
    }
    
    /**
     * Verifies that {@link MarkerUtil#addMarker(IFile, String, RuleViolation)} adds a marker even when the character
     * start and end positions are mixed up, i.e. {@code start > end}. For some rules, PMD creates violations where the
     * start position is greater than the end position.
     */
    @Test
    public void addMarkerEndBeforeStart() throws CoreException {
        final IFile file = mock(IFile.class);
        final IMarker marker = mock(IMarker.class);
        when(file.createMarker(MARKER_TYPE)).thenReturn(marker);
        final RuleViolation violation = mock(RuleViolation.class);
        when(violation.getDescription()).thenReturn("message");
        when(violation.getBeginLine()).thenReturn(1);
        when(violation.getBeginColumn()).thenReturn(22);
        when(violation.getEndLine()).thenReturn(1);
        when(violation.getEndColumn()).thenReturn(17);
        when(violation.getClassName()).thenReturn("ClassName");
        final Rule rule = mock(Rule.class);
        when(rule.getLanguage()).thenReturn(new JavaLanguageModule());
        when(rule.getRuleSetName()).thenReturn("basic");
        when(rule.getName()).thenReturn("ExtendsObject");
        when(violation.getRule()).thenReturn(rule);
        
        final IMarker actual = MarkerUtil.addMarker(file, "class A extends Object { }", violation);
        
        assertNotNull("The method must always return a marker", actual);
        verify(file).createMarker(MARKER_TYPE);
        verify(actual).setAttribute(IMarker.MESSAGE, "message");
        verify(actual).setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        verify(actual).setAttribute(IMarker.LINE_NUMBER, 1);
        verify(actual).setAttribute(IMarker.CHAR_START, 16);
        verify(actual).setAttribute(IMarker.CHAR_END, 22);
        verify(actual).setAttribute("ruleId", "java.basic.ExtendsObject");
        verify(actual).setAttribute("violationClassName", "ClassName");
        verify(actual).setAttribute("markerText", "Object");
    }
}
