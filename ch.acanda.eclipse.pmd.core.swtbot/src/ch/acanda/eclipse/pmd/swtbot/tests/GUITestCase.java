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

package ch.acanda.eclipse.pmd.swtbot.tests;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Philip Graf
 */
public class GUITestCase {
    
    private final SWTWorkbenchBot bot;
    
    @BeforeClass
    public static void initSWTBotPreferences() {
        SWTBotPreferences.TIMEOUT = 10000;
    }
    
    @BeforeClass
    @AfterClass
    public static void resetWorkbench() {
        final SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
        workbenchBot.resetWorkbench();
        closeWelcomeView(workbenchBot);
        // switch to Java perspective
        workbenchBot.perspectiveById("org.eclipse.jdt.ui.JavaPerspective").activate();
    }

    private static void closeWelcomeView(final SWTWorkbenchBot workbenchBot) {
        for (final SWTBotView view : workbenchBot.views()) {
            if ("org.eclipse.ui.internal.introview".equals(view.getReference().getId())) {
                view.close();
            }
        }
    }

    protected GUITestCase() {
        bot = new SWTWorkbenchBot();
    }
    
    protected SWTWorkbenchBot bot() {
        return bot;
    }

}
