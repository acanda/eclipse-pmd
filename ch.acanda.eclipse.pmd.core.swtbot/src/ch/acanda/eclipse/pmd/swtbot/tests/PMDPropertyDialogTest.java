// =====================================================================
//
// Copyright (C) 2012 - 2018, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.swtbot.tests;

import static ch.acanda.eclipse.pmd.swtbot.condition.Conditions.isChecked;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.tableHasRows;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.acanda.eclipse.pmd.swtbot.bot.AddRuleSetConfigurationWizardBot;
import ch.acanda.eclipse.pmd.swtbot.bot.FileSelectionDialogBot;
import ch.acanda.eclipse.pmd.swtbot.bot.PMDPropertyDialogBot;
import ch.acanda.eclipse.pmd.swtbot.client.JavaProjectClient;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * Tests the PMD rule set functionality of the PMD property dialog.
 *
 * @author Philip Graf
 */
public final class PMDPropertyDialogTest extends GUITestCase {

    private static final String PROJECT_NAME_1 = PMDPropertyDialogTest.class.getSimpleName() + "1";
    private static final String PROJECT_NAME_2 = PMDPropertyDialogTest.class.getSimpleName() + "2";
    private static final String RULE_SET_FILE = "PMDRuleSetTest.xml";
    private static final String TEST_RULE_SET_NAME = "Test PMD Rule Set";
    private static final String FILE_SYSTEM_RULE_SET_NAME = "PMD Rules (File System)";
    private static final String WORKSPACE_RULE_SET_NAME = "PMD Rules (Workspace)";
    private static final String PROJECT_RULE_SET_NAME = "PMD Rules (Project)";
    private static final String REMOTE_RULE_SET_NAME = "PMD Rules (Remote)";
    private static final Path PMD_XML = Paths.get("pmd.xml");

    private static File rules;

    @BeforeClass
    public static void createJavaProjects() throws IOException {
        JavaProjectClient.createJavaProject(PROJECT_NAME_1);

        final String content = Resources.toString(PMDPropertyDialogTest.class.getResource(RULE_SET_FILE), Charsets.UTF_8);
        JavaProjectClient.createFileInProject(PROJECT_NAME_1, PMD_XML, content);

        JavaProjectClient.createJavaProject(PROJECT_NAME_2);

        rules = File.createTempFile(PMDPropertyDialogTest.class.getSimpleName() + "-", ".xml");
        Files.write(content, rules, Charsets.UTF_8);
    }

    @AfterClass
    public static void deleteJavaProjects() {
        JavaProjectClient.deleteJavaProject(PROJECT_NAME_1);
        JavaProjectClient.deleteJavaProject(PROJECT_NAME_2);
        rules.delete();
    }

    @Test
    public void manageRuleSets() {
        enablePMD();
        addFileSystemRuleSetInFirstProject();
        activateTheSameFileSystemRuleSetInSecondProject();
        addWorkspaceRuleSetInFirstProject();
        addProjectRuleSetInFirstProject();
        addRemoteRuleSetInFirstProject();
        deactivateWorkspaceRuleSet();
        deactivateFileSystemRuleSet();
    }

    private void enablePMD() {
        final PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertFalse("PMD should be disabled by default", dialog.enablePMD().isChecked());
        assertFalse("The button to add a new rule set should be disabled as long as PMD is disabled",
                dialog.addRuleSet().isEnabled());
        dialog.enablePMD().select();
        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    public void addFileSystemRuleSetInFirstProject() {
        final PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new rule set should be enabled when PMD is enabled", dialog.addRuleSet().isEnabled());

        dialog.addRuleSet().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled as long as the name and location are missing");

        wizard.filesystem().click();
        wizard.next().click();
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled as long as the name and location are missing");
        assertTrue("The browse button should be visible for a file system rule set", wizard.isBrowseButtonVisible());

        wizard.location().setText(rules.getAbsolutePath());
        wizard.bot().waitUntil(tableHasRows(wizard.rules(), 2));
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertEquals("The name of the ruleset should be loaded into the name text field", TEST_RULE_SET_NAME, wizard.name().getText());
        assertArrayEquals("Rules of the PMD ", expectedNames, actualNames);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if both a name and a location is available");

        wizard.name().setText("");
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled if the name is not available");

        wizard.name().setText(FILE_SYSTEM_RULE_SET_NAME);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if the name is available");

        wizard.finish().click();
        wizard.bot().waitUntil(shellCloses(wizard));
        dialog.bot().waitUntil(tableHasRows(dialog.ruleSets(), 1));
        assertTrue("The added rule set should be activated", dialog.ruleSets().getTableItem(0).isChecked());
        assertEquals("Name of the rule set", FILE_SYSTEM_RULE_SET_NAME, dialog.ruleSets().cell(0, "Name"));
        assertEquals("Type of the rule set", "File System", dialog.ruleSets().cell(0, "Type"));
        assertEquals("Location of the rule set", rules.getAbsolutePath(), dialog.ruleSets().cell(0, "Location"));

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    private void activateTheSameFileSystemRuleSetInSecondProject() {
        PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_2);
        assertFalse("PMD should be disabled by default", dialog.enablePMD().isChecked());

        dialog.enablePMD().select();
        assertEquals("The previously added rule set should als be available in the second project",
                1, dialog.ruleSets().rowCount());
        assertFalse("The available rule set should not be activated", dialog.ruleSets().getTableItem(0).isChecked());
        assertEquals("Name of the rule set", FILE_SYSTEM_RULE_SET_NAME, dialog.ruleSets().cell(0, "Name"));
        assertEquals("Type of the rule set", "File System", dialog.ruleSets().cell(0, "Type"));
        assertEquals("Location of the rule set", rules.getAbsolutePath(), dialog.ruleSets().cell(0, "Location"));

        dialog.ruleSets().getTableItem(0).check();
        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));

        dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_2);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The rule set should be activated", dialog.ruleSets().getTableItem(0).isChecked());

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    private void addWorkspaceRuleSetInFirstProject() {
        final PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new rule set should be enabled when PMD is enabled", dialog.addRuleSet().isEnabled());

        dialog.enablePMD().select();
        dialog.addRuleSet().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled as long as the name is missing");

        wizard.workspace().click();
        wizard.next().click();
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled as long as the name is missing");

        assertTrue("The browse button should be visible for a workspace rule set", wizard.isBrowseButtonVisible());

        wizard.browse().click();
        final FileSelectionDialogBot fileSelectionDialog = FileSelectionDialogBot.getActive();
        fileSelectionDialog.select(PROJECT_NAME_1, PMD_XML.toString());
        fileSelectionDialog.ok().click();
        fileSelectionDialog.waitUntilClosed();
        final String workspaceRelativePath = PROJECT_NAME_1 + '/' + PMD_XML;
        assertEquals("The location should contain the project name and the path to the rule set file",
                wizard.location().getText(), workspaceRelativePath);

        wizard.bot().waitUntil(tableHasRows(wizard.rules(), 2));
        assertEquals("The name of the ruleset should be loaded into the name text field", TEST_RULE_SET_NAME, wizard.name().getText());
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD rule set", expectedNames, actualNames);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if both a name and a location are available");

        wizard.name().setText("");
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled if the name is not available");

        wizard.name().setText(WORKSPACE_RULE_SET_NAME);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if the name is available");

        wizard.finish().click();
        wizard.bot().waitUntil(shellCloses(wizard));
        dialog.bot().waitUntil(tableHasRows(dialog.ruleSets(), 2));
        assertTrue("The added rule set should be activated", dialog.ruleSets().getTableItem(1).isChecked());
        assertEquals("Name of the rule set", WORKSPACE_RULE_SET_NAME, dialog.ruleSets().cell(1, "Name"));
        assertEquals("Type of the rule set", "Workspace", dialog.ruleSets().cell(1, "Type"));
        assertEquals("Location of the rule set", workspaceRelativePath, dialog.ruleSets().cell(1, "Location"));

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    private void addProjectRuleSetInFirstProject() {
        final PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new rule set should be enabled when PMD is enabled", dialog.addRuleSet().isEnabled());

        dialog.enablePMD().select();
        dialog.addRuleSet().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();

        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());

        wizard.project().click();
        wizard.next().click();
        assertFalse("The finish button should be disabled as long as the name is missing", wizard.finish().isEnabled());

        assertTrue("The browse button should be visible for a project rule set", wizard.isBrowseButtonVisible());

        final String projectRelativePath = PMD_XML.toString();
        wizard.location().setText(projectRelativePath);
        wizard.bot().waitUntil(tableHasRows(wizard.rules(), 2));
        assertEquals("The name of the ruleset should be loaded into the name text field", TEST_RULE_SET_NAME, wizard.name().getText());
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD rule set", expectedNames, actualNames);
        assertTrue("The finish button should be enabled if both a name and a location with a valid rule set is available",
                wizard.finish().isEnabled());

        wizard.name().setText("");
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled if the name is not available");

        wizard.name().setText(PROJECT_RULE_SET_NAME);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if the name is available");

        wizard.finish().click();
        wizard.bot().waitUntil(shellCloses(wizard));
        dialog.bot().waitUntil(tableHasRows(dialog.ruleSets(), 3));
        assertTrue("The added rule set should be activated", dialog.ruleSets().getTableItem(2).isChecked());
        assertEquals("Name of the rule set", PROJECT_RULE_SET_NAME, dialog.ruleSets().cell(2, "Name"));
        assertEquals("Type of the rule set", "Project", dialog.ruleSets().cell(2, "Type"));
        assertEquals("Location of the rule set", projectRelativePath, dialog.ruleSets().cell(2, "Location"));

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    private void addRemoteRuleSetInFirstProject() {
        final PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());
        assertTrue("The button to add a new rule set should be enabled when PMD is enabled", dialog.addRuleSet().isEnabled());

        dialog.addRuleSet().click();
        final AddRuleSetConfigurationWizardBot wizard = AddRuleSetConfigurationWizardBot.getActive();
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled as long as the name is missing");

        wizard.remote().click();
        wizard.next().click();
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled as long as the name is missing");

        assertFalse("The browse button should not be visible for a remote rule set", wizard.isBrowseButtonVisible());

        final String uri = rules.toURI().toString();
        wizard.location().setText(uri);
        wizard.bot().waitUntil(tableHasRows(wizard.rules(), 2));
        assertEquals("The name of the ruleset should be loaded into the name text field", TEST_RULE_SET_NAME, wizard.name().getText());
        final String[] expectedNames = new String[] { "ExtendsObject", "BooleanInstantiation" };
        final String[] actualNames = wizard.ruleNames();
        assertArrayEquals("Rules of the PMD rule set", expectedNames, actualNames);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if both a name and a location are available");

        wizard.name().setText("");
        wizard.waitUntilFinishIsDisabled("The finish button should be disabled if the name is not available");

        wizard.name().setText(REMOTE_RULE_SET_NAME);
        wizard.waitUntilFinishIsEnabled("The finish button should be enabled if the name is available");

        wizard.finish().click();
        wizard.bot().waitUntil(shellCloses(wizard));
        dialog.bot().waitUntil(tableHasRows(dialog.ruleSets(), 4));
        assertTrue("The added rule set should be activated", dialog.ruleSets().getTableItem(3).isChecked());
        assertEquals("Name of the rule set", REMOTE_RULE_SET_NAME, dialog.ruleSets().cell(3, "Name"));
        assertEquals("Type of the rule set", "Remote", dialog.ruleSets().cell(3, "Type"));
        assertEquals("Location of the rule set", uri, dialog.ruleSets().cell(3, "Location"));

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    private void deactivateWorkspaceRuleSet() {
        PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());

        final SWTBotTableItem workspaceTableItem = dialog.ruleSets().getTableItem(WORKSPACE_RULE_SET_NAME);
        workspaceTableItem.uncheck();
        dialog.bot().waitWhile(isChecked(workspaceTableItem));

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));

        dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertEquals("The deactivated rule set should not be in the table anymore since it is not used by any other project",
                3, dialog.ruleSets().rowCount());

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

    private void deactivateFileSystemRuleSet() {
        PMDPropertyDialogBot dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertTrue("PMD should be enabled", dialog.enablePMD().isChecked());

        final SWTBotTableItem fileSystemTableItem = dialog.ruleSets().getTableItem(FILE_SYSTEM_RULE_SET_NAME);
        fileSystemTableItem.uncheck();
        dialog.bot().waitWhile(isChecked(fileSystemTableItem));

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));

        dialog = JavaProjectClient.openPMDPropertyDialog(PROJECT_NAME_1);
        assertFalse("The deactivated rule set should still be in the table since it is used by project " + PROJECT_NAME_2,
                dialog.ruleSets().getTableItem(FILE_SYSTEM_RULE_SET_NAME).isChecked());

        dialog.ok().click();
        dialog.bot().waitUntil(shellCloses(dialog));
    }

}
