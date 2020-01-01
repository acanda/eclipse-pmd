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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

/**
 * Unit test for {@link WrappingPMDMarker}.
 * 
 * @author Philip Graf
 */
public class WrappingPMDMarkerTest {
    
    private static final String RULE_ID = "ruleId";

    /**
     * Verifies that {@link WrappingPMDMarker#isOtherWithSameRuleId(IMarker)} returns true if the argument is not the same
     * instance but has the same rule id.
     */
    @Test
    public void isOtherWithSameRuleId() {
        final IMarker marker = mock(IMarker.class);
        final String ruleId = "Rule A";
        when(marker.getAttribute(eq(RULE_ID), isA(String.class))).thenReturn(ruleId);
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final IMarker other = mock(IMarker.class);
        when(other.getAttribute(eq(RULE_ID), isA(String.class))).thenReturn(ruleId);
        final boolean actual = pmdMarker.isOtherWithSameRuleId(other);
        assertTrue("The marker must not be the same instance as the other marker", actual);
    }

    /**
     * Verifies that {@link WrappingPMDMarker#isOtherWithSameRuleId(IMarker)} returns false if the argument is the same
     * instance.
     */
    @Test
    public void isOtherWithSameRuleIdSameInstance() {
        final IMarker marker = mock(IMarker.class);
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final IMarker other = marker;
        final boolean actual = pmdMarker.isOtherWithSameRuleId(other);
        assertFalse("The marker must not be the same instance as the other marker", actual);
    }

    /**
     * Verifies that {@link WrappingPMDMarker#isOtherWithSameRuleId(IMarker)} returns false if the argument has not the same
     * rule id.
     */
    @Test
    public void isOtherWithSameRuleIdDifferentRuleId() {
        final IMarker marker = mock(IMarker.class);
        when(marker.getAttribute(eq(RULE_ID), isA(String.class))).thenReturn("Rule B");
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final IMarker other = mock(IMarker.class);
        when(other.getAttribute(eq(RULE_ID), isA(String.class))).thenReturn("Rule C");
        final boolean actual = pmdMarker.isOtherWithSameRuleId(other);
        assertFalse("The marker must not be the same instance as the other marker", actual);
    }

    /**
     * Verifies that {@link WrappingPMDMarker#setRuleId(String)} sets the rule id on the wrapped marker.
     */
    @Test
    public void setRuleId() throws CoreException {
        final IMarker marker = mock(IMarker.class);
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final String expected = "Rule D";
        pmdMarker.setRuleId(expected);
        verify(marker).setAttribute(RULE_ID, expected);
    }

    /**
     * Verifies that {@link WrappingPMDMarker#getRuleId()} gets the rule id from the wrapped marker.
     */
    @Test
    public void getRuleId() throws CoreException {
        final IMarker marker = mock(IMarker.class);
        final String expected = "Rule E";
        when(marker.getAttribute(eq(RULE_ID), anyString())).thenReturn(expected);
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final String actual = pmdMarker.getRuleId();
        assertEquals("The rule id should be read from the wrapped marker", expected, actual);
    }

    /**
     * Verifies that {@link WrappingPMDMarker#setViolationClassName(String)} sets the violation class name on the wrapped
     * marker.
     */
    @Test
    public void setViolationClassName() throws CoreException {
        final IMarker marker = mock(IMarker.class);
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final String expected = "ViolationClassName";
        pmdMarker.setViolationClassName(expected);
        verify(marker).setAttribute("violationClassName", expected);
    }
    
    /**
     * Verifies that {@link WrappingPMDMarker#getViolationClassName()} gets the violation class name from the wrapped marker.
     */
    @Test
    public void getViolationClassName() throws CoreException {
        final IMarker marker = mock(IMarker.class);
        final String expected = "ViolationClassName";
        when(marker.getAttribute(eq("violationClassName"), anyString())).thenReturn(expected);
        final PMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final String actual = pmdMarker.getViolationClassName();
        assertEquals("The rule id should be read from the wrapped marker", expected, actual);
    }
    
    /**
     * Verifies that {@link WrappingPMDMarker#setLanguage(String)} sets the language on the wrapped marker.
     */
    @Test
    public void setLanguage() throws CoreException {
        final IMarker marker = mock(IMarker.class);
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
        final String expected = "java";
        pmdMarker.setLanguage(expected);
        verify(marker).setAttribute("language", expected);
    }

}
