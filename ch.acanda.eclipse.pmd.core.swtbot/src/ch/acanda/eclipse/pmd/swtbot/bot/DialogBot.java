// =====================================================================
//
// Copyright (C) 2012 - 2014, Philip Graf
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
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * Provides easy access to standard widgets of a dialog, e.g. the OK or Cancel buttons.
 * 
 * @author Philip Graf
 */
public class DialogBot extends SWTBotShell {

    public DialogBot(final Shell shell) throws WidgetNotFoundException {
        super(shell);
    }

    /**
     * @return The dialog's {@code OK} button.
     */
    public SWTBotButton ok() {
        return bot().button("OK");
    }

    /**
     * @return The dialog's {@code Cancel} button.
     */
    public SWTBotButton cancel() {
        return bot().button("Cancel");
    }
    
    /**
     * Waits until this dialog closes. Note: this method does not close the dialog.
     */
    public void waitUntilClosed() {
        bot().waitUntil(Conditions.shellCloses(this));
    }
    
}
