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

package ch.acanda.eclipse.pmd.swtbot.tests;

import static ch.acanda.eclipse.pmd.swtbot.condition.Conditions.isPerspectiveActive;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author Philip Graf
 */
public class GUITestCase {
    
    private final SWTWorkbenchBot bot;
    
    protected GUITestCase() {
        bot = new SWTWorkbenchBot();
    }

    @BeforeClass
    public static void initSWTBotPreferencesAndOpenJavaPerspective() {
        SWTBotPreferences.TIMEOUT = 10000;
        final SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
        closeWelcomeView(workbenchBot);
        openJavaPerspective(workbenchBot);
    }

    @AfterClass
    public static void resetWorkbench() {
        final SWTWorkbenchBot workbenchBot = new SWTWorkbenchBot();
        workbenchBot.resetWorkbench();
    }
    
    @After
    public void closeAllDialogs() {
        bot.closeAllShells();
    }

    private static void closeWelcomeView(final SWTWorkbenchBot workbenchBot) {
        for (final SWTBotView view : workbenchBot.views()) {
            if ("org.eclipse.ui.internal.introview".equals(view.getReference().getId())) {
                view.close();
            }
        }
    }

    private static void openJavaPerspective(final SWTWorkbenchBot workbenchBot) {
        final SWTBotPerspective javaPerspective = workbenchBot.perspectiveById("org.eclipse.jdt.ui.JavaPerspective");
        if (!javaPerspective.isActive()) {
            workbenchBot.menu("Window").menu("Open Perspective").menu("Java").click();
            workbenchBot.waitUntil(isPerspectiveActive(javaPerspective));
        }
    }
    
    protected SWTWorkbenchBot bot() {
        return bot;
    }

}
