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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Preconditions;

/**
 * Wrapper around an {@link IMarker} with convenience methods for accessing the attributes of a PMD marker.
 * 
 * @author Philip Graf
 */
public class PMDMarker {
    
    private static final String RULE_ID = "ruleId";
    private static final String VIOLATION_CLASS_NAME = "violationClassName";
    private static final String MARKER_TEXT = "markerText";
    
    private final IMarker marker;
    
    public PMDMarker(final IMarker marker) {
        Preconditions.checkArgument(marker != null);
        this.marker = marker;
    }
    
    public void setRuleId(final String ruleId) throws CoreException {
        marker.setAttribute(RULE_ID, ruleId);
    }
    
    public String getRuleId() {
        return marker.getAttribute(RULE_ID, null);
    }
    
    public void setViolationClassName(final String violationClassName) throws CoreException {
        marker.setAttribute(VIOLATION_CLASS_NAME, violationClassName);
    }
    
    public String getViolationClassName() {
        return marker.getAttribute(VIOLATION_CLASS_NAME, "");
    }
    
    public void setMarkerText(final String markerText) throws CoreException {
        marker.setAttribute(MARKER_TEXT, markerText);
    }
    
    public String getMarkerText() {
        return marker.getAttribute(MARKER_TEXT, "");
    }
    
    public boolean isOtherWithSameRuleId(final IMarker other) {
        return other != marker && marker.getAttribute(RULE_ID, "").equals(other.getAttribute(RULE_ID, ""));
    }

}
