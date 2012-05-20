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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.acanda.eclipse.pmd.swtbot.SWTBotID;
import ch.acanda.eclipse.pmd.swtbot.client.JavaProjectClient;

/**
 * @author Philip Graf
 */
public final class EnablePMDTest extends GUITestCase {
    
    private static final String PROJECT_NAME = EnablePMDTest.class.getSimpleName();

    @BeforeClass
    public static void createJavaProject() {
        JavaProjectClient.createJavaProject(PROJECT_NAME);
    }

    @AfterClass
    public static void deleteJavaProject() {
        JavaProjectClient.deleteJavaProject(PROJECT_NAME);
    }

    @Test
    public void enablePMD() {
        SWTBotShell dialog = openPMDProjectPropertyDialog();
        SWTBotCheckBox enablePMDCheckBox = dialog.bot().checkBoxWithId(SWTBotID.PROPERTY_PAGE_ENABLE_PMD.name());
        assertFalse("PMD should be disabled by default", enablePMDCheckBox.isChecked());
        
        enablePMDCheckBox.select();
        dialog.bot().button("OK").click();
        bot().waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(dialog));
        
        dialog = openPMDProjectPropertyDialog();
        enablePMDCheckBox = dialog.bot().checkBoxWithId(SWTBotID.PROPERTY_PAGE_ENABLE_PMD.name());
        assertTrue("PMD should be enabled", enablePMDCheckBox.isChecked());
        
        dialog.bot().button("OK").click();
        bot().waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses(dialog));
    }
    
    private SWTBotShell openPMDProjectPropertyDialog() {
        bot().viewById("org.eclipse.jdt.ui.PackageExplorer").bot().tree().getTreeItem(PROJECT_NAME).select();
        bot().menu("File").menu("Properties").click();
        final SWTBotShell dialog = bot().shell("Properties for " + PROJECT_NAME);
        final SWTBot dialogBot = dialog.bot();
        dialogBot.tree().getTreeItem("PMD").select();
        return dialog;
    }
}
