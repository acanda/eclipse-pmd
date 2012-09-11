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

package ch.acanda.eclipse.pmd.swtbot.bot;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;

/**
 * @author Philip Graf
 */
public final class AddRuleSetConfigurationWizardBot extends SWTBotShell {
    
    public AddRuleSetConfigurationWizardBot(final Shell shell) throws WidgetNotFoundException {
        super(shell);
    }
    
    public static AddRuleSetConfigurationWizardBot getActive() {
        final SWTBotShell wizard = new SWTWorkbenchBot().shell("Add Rule Set Configuration");
        return new AddRuleSetConfigurationWizardBot(wizard.widget);
    }
    
    public SWTBotText name() {
        return bot().textWithId(SWTBotID.NAME.name());
    }
    
    public SWTBotText location() {
        return bot().textWithId(SWTBotID.LOCATION.name());
    }

    public SWTBotTable rules() {
        return bot().tableWithId(SWTBotID.RULES.name());
    }
    
    public String[] ruleNames() {
        final SWTBotTable table = rules();
        final String[] names = new String[table.rowCount()];
        for (int i = 0; i < table.rowCount(); i++) {
            names[i] = table.cell(i, 0);
        }
        return names;
    }

    public SWTBotButton next() {
        return bot().button("Next >");
    }

    public SWTBotButton finish() {
        return bot().button("Finish");
    }

    public SWTBotRadio filesystem() {
        return bot().radioWithId(SWTBotID.FILE_SYSTEM.name());
    }

    public SWTBotRadio workspace() {
        return bot().radioWithId(SWTBotID.WORKSPACE.name());
    }

    public SWTBotRadio project() {
        return bot().radioWithId(SWTBotID.PROJECT.name());
    }

}
