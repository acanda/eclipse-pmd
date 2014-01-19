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
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;
import ch.acanda.eclipse.pmd.ui.dialog.FileSelectionDialog;

/**
 * The {@link FileSelectionDialogBot} provides easy access to the {@link FileSelectionDialog}'s widgets.
 * 
 * @author Philip Graf
 */
public final class FileSelectionDialogBot extends DialogBot {
    
    private FileSelectionDialogBot(final Shell shell) {
        super(shell);
    }
    
    public static FileSelectionDialogBot getActive() {
        return new FileSelectionDialogBot(new SWTWorkbenchBot().shellWithId(SWTBotID.FILE_SELECTION_DIALOG.name()).widget);
    }
    
    public void select(final String... items) {
        SWTBotTreeItem treeItem = bot().tree().getTreeItem(items[0]);
        for (int i = 1; i < items.length; i++) {
            treeItem.expand();
            treeItem = treeItem.getNode(items[i]);
        }
        treeItem.select();
    }
}
