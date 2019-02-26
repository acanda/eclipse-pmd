// =====================================================================
//
// Copyright (C) 2012 - 2019, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.ui.util;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * Default implementation of {@link SelectionListener} with {@link #widgetSelected(SelectionEvent)} calling
 * {@link #widgetDefaultSelected(SelectionEvent)} and vice versa so only one of them needs to be implemented.
 * 
 * @author Philip Graf
 */
public class SelectionAdapter implements SelectionListener {
    
    @Override
    public void widgetSelected(final SelectionEvent e) {
        widgetDefaultSelected(e);
    }
    
    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        widgetSelected(e);
    }
    
}
