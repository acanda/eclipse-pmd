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

package ch.acanda.eclipse.pmd.swtbot.tests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.acanda.eclipse.pmd.swtbot.bot.AddRuleSetConfigurationWizardBot;
import ch.acanda.eclipse.pmd.swtbot.bot.PMDProjectPropertyDialogBot;
import ch.acanda.eclipse.pmd.swtbot.client.JavaProjectClient;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
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
    
    private static final String FILE_SYSTEM_CONFIGURATION_NAME = "PMD Rules (File System)";
    private static final String WORKSPACE_CONFIGURATION_NAME = "PMD Rules (Workspace)";
    private static final String PROJECT_CONFIGURATION_NAME = "PMD Rules (Project)";
    private static final String REMOTE_CONFIGURATION_NAME = "PMD Rules (Remote)";
    private static File rules;
    private static final Path PMD_XML = Paths.get("pmd.xml");

    @BeforeClass
    public static void createJavaProjects() throws IOException {
        JavaProjectClient.createJavaProject(PROJECT_NAME_1);

        final String content = CharStreams.toString(CharStreams.newReaderSupplier(new PMDConfigurationSupplier(), Charsets.UTF_8));
        JavaProjectClient.createFileInProject(PROJECT_NAME_1, PMD_XML, content);
        
        JavaProjectClient.createJavaProject(PROJECT_NAME_2);
        
        rules = File.createTempFile(PMDConfigurationsTest.class.getSimpleName() + "-", ".xml");
        Files.copy(new PMDConfigurationSupplier(), rules);
    }
    
    @AfterClass
    public static void deleteJavaProjects() {
        JavaProjectClient.deleteJavaProject(PROJECT_NAME_1);
        JavaProjectClient.deleteJavaProject(PROJECT_NAME_2);
        rules.delete();
    }

    @Test
    public void manageRuleSetConfigurations() {
        enablePMD();
        addFileSystemConfigurationInFirstProject();
        activateTheSameConfigurationInSecondProject();
        addWorkspaceConfigurationInFirstProject();
        addProjectConfigurationInFirstProject();
        addRemoteConfigurationInFirstProject();
    }

    private void enablePMD() {
        final PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_1);
        assertFalse("PMD should be disabled by default", dialog.enablePMD().isChecked());
        assertFalse("The button to add a new configuration should be disabled as long as PMD is disabled",
                dialog.addConfiguration().isEnabled());
        dialog.enablePMD().select();
        dialog.ok().click();
        dialog.bot().waitUntil(Conditions.shellCloses(dialog));
    }
    
    public void addFileSystemConfigurationInFirstProject() {
        final PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new configuration should be enabled when PMD is enabled", dialog.addConfiguration().isEnabled());

        dialog.addConfiguration().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.filesystem().click();
        wizard.next().click();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());

        wizard.name().setText(FILE_SYSTEM_CONFIGURATION_NAME);
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
        assertEquals("Name of the configuration", FILE_SYSTEM_CONFIGURATION_NAME, dialog.configurations().cell(0, "Name"));
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
        assertEquals("Name of the configuration", FILE_SYSTEM_CONFIGURATION_NAME, dialog.configurations().cell(0, "Name"));
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
    
    private void addWorkspaceConfigurationInFirstProject() {
        final PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new configuration should be enabled when PMD is enabled", dialog.addConfiguration().isEnabled());
        
        dialog.enablePMD().select();
        dialog.addConfiguration().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.workspace().click();
        wizard.next().click();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.name().setText(WORKSPACE_CONFIGURATION_NAME);
        assertFalse("The finish button should be disabled as long as the location is missing", wizard.finish().isEnabled());
        
        final String workspaceRelativePath = Paths.get(PROJECT_NAME_1).resolve(PMD_XML).toString();
        wizard.location().setText(workspaceRelativePath);
        wizard.bot().waitUntil(Conditions.tableHasRows(wizard.rules(), 2));
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD configuration", expectedNames, actualNames);
        assertTrue("The finish button should be enabled if bot a name and a location with a valid configuration is available",
                wizard.finish().isEnabled());
        
        wizard.finish().click();
        wizard.bot().waitUntil(Conditions.shellCloses(wizard));
        dialog.bot().waitUntil(Conditions.tableHasRows(dialog.configurations(), 2));
        assertTrue("The added configuration should be activated", dialog.configurations().getTableItem(1).isChecked());
        assertEquals("Name of the configuration", WORKSPACE_CONFIGURATION_NAME, dialog.configurations().cell(1, "Name"));
        assertEquals("Type of the configuration", "Workspace", dialog.configurations().cell(1, "Type"));
        assertEquals("Location of the configuration", workspaceRelativePath, dialog.configurations().cell(1, "Location"));
        
        dialog.ok().click();
        dialog.bot().waitUntil(Conditions.shellCloses(dialog));
    }

    private void addProjectConfigurationInFirstProject() {
        final PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new configuration should be enabled when PMD is enabled", dialog.addConfiguration().isEnabled());
        
        dialog.enablePMD().select();
        dialog.addConfiguration().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.project().click();
        wizard.next().click();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.name().setText(PROJECT_CONFIGURATION_NAME);
        assertFalse("The finish button should be disabled as long as the location is missing", wizard.finish().isEnabled());
        
        final String projectRelativePath = PMD_XML.toString();
        wizard.location().setText(projectRelativePath);
        wizard.bot().waitUntil(Conditions.tableHasRows(wizard.rules(), 2));
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD configuration", expectedNames, actualNames);
        assertTrue("The finish button should be enabled if bot a name and a location with a valid configuration is available",
                wizard.finish().isEnabled());
        
        wizard.finish().click();
        wizard.bot().waitUntil(Conditions.shellCloses(wizard));
        dialog.bot().waitUntil(Conditions.tableHasRows(dialog.configurations(), 3));
        assertTrue("The added configuration should be activated", dialog.configurations().getTableItem(2).isChecked());
        assertEquals("Name of the configuration", PROJECT_CONFIGURATION_NAME, dialog.configurations().cell(2, "Name"));
        assertEquals("Type of the configuration", "Project", dialog.configurations().cell(2, "Type"));
        assertEquals("Location of the configuration", projectRelativePath, dialog.configurations().cell(2, "Location"));
        
        dialog.ok().click();
        dialog.bot().waitUntil(Conditions.shellCloses(dialog));
    }

    private void addRemoteConfigurationInFirstProject() {
        final PMDProjectPropertyDialogBot dialog = JavaProjectClient.openPMDProjectPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new configuration should be enabled when PMD is enabled", dialog.addConfiguration().isEnabled());
        
        dialog.addConfiguration().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.remote().click();
        wizard.next().click();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());
        
        wizard.name().setText(REMOTE_CONFIGURATION_NAME);
        assertFalse("The finish button should be disabled as long as the location is missing", wizard.finish().isEnabled());
        
        final String uri = rules.toURI().toString();
        wizard.location().setText(uri);
        wizard.bot().waitUntil(Conditions.tableHasRows(wizard.rules(), 2));
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD configuration", expectedNames, actualNames);
        assertTrue("The finish button should be enabled if bot a name and a location with a valid configuration is available",
                wizard.finish().isEnabled());
        
        wizard.finish().click();
        wizard.bot().waitUntil(Conditions.shellCloses(wizard));
        dialog.bot().waitUntil(Conditions.tableHasRows(dialog.configurations(), 4));
        assertTrue("The added configuration should be activated", dialog.configurations().getTableItem(3).isChecked());
        assertEquals("Name of the configuration", REMOTE_CONFIGURATION_NAME, dialog.configurations().cell(3, "Name"));
        assertEquals("Type of the configuration", "Remote", dialog.configurations().cell(3, "Type"));
        assertEquals("Location of the configuration", uri, dialog.configurations().cell(3, "Location"));
        
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
