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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.acanda.eclipse.pmd.swtbot.bot.AddRuleSetConfigurationWizardBot;
import ch.acanda.eclipse.pmd.swtbot.bot.PMDProjectPropertyDialogBot;
import ch.acanda.eclipse.pmd.swtbot.client.JavaProjectClient;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * Tests the PMD configurations functionality of the PMD property dialog.
 * 
 * @author Philip Graf
 */
public final class PMDConfigurationsTest extends GUITestCase {
    
    private static final String PROJECT_NAME_1 = PMDConfigurationsTest.class.getSimpleName() + "1";
    private static final String PROJECT_NAME_2 = PMDConfigurationsTest.class.getSimpleName() + "2";
    
    private static final String CONFIGURATION_NAME = "PMD Rules";
    private static File rules;

    @BeforeClass
    public static void createJavaProject() throws IOException {
        JavaProjectClient.createJavaProject(PROJECT_NAME_1);
        JavaProjectClient.createJavaProject(PROJECT_NAME_2);

        rules = File.createTempFile(PMDConfigurationsTest.class.getSimpleName() + "-", ".xml");
        Files.copy(new PMDConfigurationSupplier(), rules);
    }
    
    @AfterClass
    public static void deleteJavaProject() {
        JavaProjectClient.deleteJavaProject(PROJECT_NAME_1);
        JavaProjectClient.deleteJavaProject(PROJECT_NAME_2);
        rules.delete();
    }

    @Test
    public void manageRuleSetConfigurations() {
        addFileSystemConfigurationInFirstProject();
        activateTheSameConfigurationInSecondProject();
    }

    public void addFileSystemConfigurationInFirstProject() {
        final PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_1);
        assertFalse("PMD should be disabled by default", dialog.enablePMD().isChecked());
        assertFalse("The button to add a new configuration should be disabled as long as PMD is disabled",
                dialog.addConfiguration().isEnabled());

        dialog.enablePMD().select();
        dialog.addConfiguration().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());

        wizard.name().setText(CONFIGURATION_NAME);
        assertFalse("The finish button should be disabled as long as the location is missing", wizard.finish().isEnabled());

        wizard.location().setText(rules.getAbsolutePath());
        wizard.bot().waitUntil(Conditions.tableHasRows(wizard.rules(), 2));
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD configuration", expectedNames, actualNames);
        assertTrue("The finish button should be enabled if bot a name and a location with a valid configuration is available",
                wizard.finish().isEnabled());

        wizard.finish().click();
        wizard.bot().waitUntil(Conditions.shellCloses(wizard));
        dialog.bot().waitUntil(Conditions.tableHasRows(dialog.configurations(), 1));
        assertTrue("The added configuration should be activated", dialog.configurations().getTableItem(0).isChecked());
        assertEquals("Name of the configuration", CONFIGURATION_NAME, dialog.configurations().cell(0, "Name"));
        assertEquals("Type of the configuration", "File System", dialog.configurations().cell(0, "Type"));
        assertEquals("Location of the configuration", rules.getAbsolutePath(), dialog.configurations().cell(0, "Location"));

        dialog.ok().click();
        dialog.bot().waitUntil(Conditions.shellCloses(dialog));
    }
    
    private void activateTheSameConfigurationInSecondProject() {
        PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_2);
        assertFalse("PMD should be disabled by default", dialog.enablePMD().isChecked());
        
        dialog.enablePMD().select();
        assertEquals("The previously added configuration should als be available in the second project",
                1, dialog.configurations().rowCount());
        assertFalse("The available configuration should not be activated", dialog.configurations().getTableItem(0).isChecked());
        assertEquals("Name of the configuration", CONFIGURATION_NAME, dialog.configurations().cell(0, "Name"));
        assertEquals("Type of the configuration", "File System", dialog.configurations().cell(0, "Type"));
        assertEquals("Location of the configuration", rules.getAbsolutePath(), dialog.configurations().cell(0, "Location"));

        dialog.configurations().getTableItem(0).check();
        dialog.ok().click();
        dialog.bot().waitUntil(Conditions.shellCloses(dialog));
        
        dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_2);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The configuration should be activated", dialog.configurations().getTableItem(0).isChecked());

        dialog.ok().click();
        dialog.bot().waitUntil(Conditions.shellCloses(dialog));
    }

    private static class PMDConfigurationSupplier implements InputSupplier<InputStream> {
        @Override
        public InputStream getInput() throws IOException {
            return PMDConfigurationsTest.class.getResourceAsStream("PMDConfigurationsTest.xml");
        }
    }
}
