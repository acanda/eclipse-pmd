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

package ch.acanda.eclipse.pmd.swtbot.condition;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

/**
 * This condition tests if a perspective is checked.
 * 
 * @author Philip Graf
 */
public final class IsPerspectiveActive extends DefaultCondition {
    
    private final SWTBotPerspective perspective;

    public IsPerspectiveActive(final SWTBotPerspective perspective) {
        this.perspective = perspective;
    }

    @Override
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    public boolean test() throws Exception {
        return perspective.isActive();
    }
    
    @Override
    public String getFailureMessage() {
        return "Perspective " + perspective.getLabel() + " is not active";
    }
    
}
