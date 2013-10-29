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

package ch.acanda.eclipse.pmd.swtbot.bot;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;

/**
 * The {@link PMDPropertyDialogBot} provides easy access to the {@link PMDPropertyDialog}'s widgets.
 * 
 * @author Philip Graf
 */
public class PMDPropertyDialogBot extends SWTBotShell {
    
    public PMDPropertyDialogBot(final Shell shell) throws WidgetNotFoundException {
        super(shell);
    }
    
    /**
     * @return The button to add a new PMD rule set.
     */
    public SWTBotButton addRuleSet() {
        return bot().buttonWithId(SWTBotID.ADD.name());
    }
    
    /**
     * @return The table containing the available PMD rule sets.
     */
    public SWTBotTable ruleSets() {
        return bot().tableWithId(SWTBotID.RULESETS.name());
    }
    
    /**
     * @return The dialog's OK button.
     */
    public SWTBotButton ok() {
        return bot().button("OK");
    }
    
    public SWTBotCheckBox enablePMD() {
        return bot().checkBoxWithId(SWTBotID.ENABLE_PMD.name());
    }

}
