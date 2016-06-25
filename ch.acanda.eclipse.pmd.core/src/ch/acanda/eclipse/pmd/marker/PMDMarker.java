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

package ch.acanda.eclipse.pmd.marker;

import org.eclipse.core.resources.IMarker;

public interface PMDMarker {

    String getRuleId();

    String getRuleName();

    String getViolationClassName();

    String getVariableName();

    String getMarkerText();

    boolean isOtherWithSameRuleId(IMarker other);

}
