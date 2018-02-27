// =====================================================================
//
// Copyright (C) 2012 - 2018, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Preconditions;

/**
 * Wrapper around an {@link IMarker} with convenience methods for accessing the attributes of a PMD marker.
 *
 * @author Philip Graf
 */
public class WrappingPMDMarker implements PMDMarker {

    private static final String DEFAULT_VALUE = "";
    private static final String RULE_ID = "ruleId";
    private static final String RULE_NAME = "ruleName";
    private static final String VIOLATION_CLASS_NAME = "violationClassName";
    private static final String VARIABLE_NAME = "variableName";
    private static final String MARKER_TEXT = "markerText";
    private static final String LANGUAGE = "language";

    private final IMarker marker;

    public WrappingPMDMarker(final IMarker marker) {
        Preconditions.checkArgument(marker != null);
        this.marker = marker;
    }

    public void setRuleId(final String ruleId) throws CoreException {
        marker.setAttribute(RULE_ID, ruleId);
    }

    @Override
    public String getRuleId() {
        return marker.getAttribute(RULE_ID, null);
    }

    public void setRuleName(final String name) throws CoreException {
        marker.setAttribute(RULE_NAME, name);
    }

    @Override
    public String getRuleName() {
        return marker.getAttribute(RULE_NAME, DEFAULT_VALUE);
    }

    public void setViolationClassName(final String violationClassName) throws CoreException {
        marker.setAttribute(VIOLATION_CLASS_NAME, violationClassName);
    }

    @Override
    public String getViolationClassName() {
        return marker.getAttribute(VIOLATION_CLASS_NAME, DEFAULT_VALUE);
    }

    public void setVariableName(final String variableName) throws CoreException {
        marker.setAttribute(VARIABLE_NAME, variableName);
    }

    @Override
    public String getVariableName() {
        return marker.getAttribute(VARIABLE_NAME, DEFAULT_VALUE);
    }

    public void setMarkerText(final String markerText) throws CoreException {
        marker.setAttribute(MARKER_TEXT, markerText);
    }

    @Override
    public String getMarkerText() {
        return marker.getAttribute(MARKER_TEXT, DEFAULT_VALUE);
    }

    @Override
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public boolean isOtherWithSameRuleId(final IMarker other) {
        return other != marker && marker.getAttribute(RULE_ID, DEFAULT_VALUE).equals(other.getAttribute(RULE_ID, DEFAULT_VALUE));
    }

    /**
     * The language attribute is used in the plugin.xml to filter markers by language. The filter accesses the wrapped
     * {@code IMarker} directly so there is no need for a getter here.
     *
     * @param language The name of the language. This is the terse name of the language as returned by
     *            {@code net.sourceforge.pmd.lang.Language.getTerseName()}.
     */
    public void setLanguage(final String language) throws CoreException {
        marker.setAttribute(LANGUAGE, language);
    }

}
