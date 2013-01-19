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
 * The {@link PMDProjectPropertyDialogBot} provides easy access to the {@link PMDProjectPropertyDialog}'s widgets.
 * 
 * @author Philip Graf
 */
public class PMDProjectPropertyDialogBot extends SWTBotShell {
    
    public PMDProjectPropertyDialogBot(final Shell shell) throws WidgetNotFoundException {
        super(shell);
    }
    
    /**
     * @return The button to add a new PMD rule set configuration.
     */
    public SWTBotButton addConfiguration() {
        return bot().buttonWithId(SWTBotID.ADD.name());
    }
    
    /**
     * @return The table containing the available PMD rule set configurations.
     */
    public SWTBotTable configurations() {
        return bot().tableWithId(SWTBotID.CONFIGURATIONS.name());
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
