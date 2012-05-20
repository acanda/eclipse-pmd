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

package ch.acanda.eclipse.pmd.swtbot;

import org.eclipse.swt.widgets.Control;

/**
 * @author Philip Graf
 */
public enum SWTBotID {
    
    PROPERTY_PAGE_ENABLE_PMD;
    
    public static void set(final Control control, final SWTBotID id) {
        control.setData("org.eclipse.swtbot.widget.key", id.name());
    }
    
}
