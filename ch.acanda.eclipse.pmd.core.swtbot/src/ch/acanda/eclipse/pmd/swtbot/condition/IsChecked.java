// =====================================================================
//
// Copyright (C) 2012 - 2013, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.swtbot.condition;

import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

/**
 * This condition tests if a table item is checked.
 * 
 * @author Philip Graf
 */
public class IsChecked extends DefaultCondition {
    
    private final SWTBotTableItem tableItem;

    public IsChecked(final SWTBotTableItem tableItem) {
        this.tableItem = tableItem;
    }

    @Override
    @SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
    public boolean test() throws Exception {
        return tableItem.isChecked();
    }
    
    @Override
    public String getFailureMessage() {
        return "TableItem " + tableItem.getText() + " is not checked";
    }
    
}
