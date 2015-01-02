// =====================================================================
//
// Copyright (C) 2012 - 2015, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.repository;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.xml.sax.SAXException;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;

/**
 * Unit tests for {@link ProjectModelSerializer}.
 * 
 * @author Philip Graf
 */
public final class ProjectModelSerializerTest {
    
    /**
     * Verifies that {@link ProjectModelSerializer#serialize(ProjectModel)} serializes a {@link ProjectModel} correctly.
     */
    @Test
    public void serialize() throws SAXException, IOException {
        final ProjectModel projectModel = new ProjectModel("TestProjectName");
        projectModel.setPMDEnabled(true);
        projectModel.setRuleSets(createRuleSets());
        
        final String actual = new ProjectModelSerializer().serialize(projectModel);
        
        final String expected = createXmlConfiguration();
        assertEquals("Serialized project model", expected, actual);
        assertValid(actual);
    }
    
    /**
     * Verifies that {@link ProjectModelSerializer#serialize(ProjectModel)} serializes a {@link ProjectModel} without
     * rule sets correctly, i.e. without a {@code <rulesets>} tag.
     */
    @Test
    public void serializeWithoutRuleSets() throws SAXException, IOException {
        final ProjectModel projectModel = new ProjectModel("TestProjectName");
        projectModel.setPMDEnabled(false);
        
        final String actual = new ProjectModelSerializer().serialize(projectModel);
        
        final String expected = createXmlConfigurationWithoutRuleSets();
        assertEquals("Serialized project model", expected, actual);
        assertValid(actual);
    }

    private String createXmlConfiguration() {
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<eclipse-pmd xmlns=\"http://acanda.ch/eclipse-pmd/0.8\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://acanda.ch/eclipse-pmd/0.8 http://acanda.ch/eclipse-pmd/eclipse-pmd-0.8.xsd\">\n"
                + "  <analysis enabled=\"true\" />\n"
                + "  <rulesets>\n"
                + "    <ruleset name=\"Project Rule Set\" ref=\"pmd.xml\" refcontext=\"project\" />\n"
                + "    <ruleset name=\"Workspace Rule Set\" ref=\"Projext X/pmd.xml\" refcontext=\"workspace\" />\n"
                + "    <ruleset name=\"Filesystem Rule Set\" ref=\"x:\\pmx.xml\" refcontext=\"filesystem\" />\n"
                + "    <ruleset name=\"Remote Rule Set\" ref=\"http://example.org/pmd.xml\" refcontext=\"remote\" />\n"
                + "  </rulesets>\n"
                + "</eclipse-pmd>";
        return expected;
    }
    
    private String createXmlConfigurationWithoutRuleSets() {
        final String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<eclipse-pmd xmlns=\"http://acanda.ch/eclipse-pmd/0.8\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://acanda.ch/eclipse-pmd/0.8 http://acanda.ch/eclipse-pmd/eclipse-pmd-0.8.xsd\">\n"
                + "  <analysis enabled=\"false\" />\n"
                + "</eclipse-pmd>";
        return expected;
    }

    private Iterable<RuleSetModel> createRuleSets() {
        return Arrays.asList(new RuleSetModel("Project Rule Set", new Location("pmd.xml", LocationContext.PROJECT)),
                             new RuleSetModel("Workspace Rule Set", new Location("Projext X/pmd.xml", LocationContext.WORKSPACE)),
                             new RuleSetModel("Filesystem Rule Set", new Location("x:\\pmx.xml", LocationContext.FILESYSTEM)),
                             new RuleSetModel("Remote Rule Set", new Location("http://example.org/pmd.xml", LocationContext.REMOTE)));
    }
    
    private void assertValid(final String actual) throws SAXException, IOException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Source schemaSource = new StreamSource(ProjectModelSerializerTest.class.getResourceAsStream("eclipse-pmd-0.8.xsd"));
        final Schema schema = schemaFactory.newSchema(schemaSource);
        final Validator validator = schema.newValidator();
        final Source xmlSource = new StreamSource(new StringReader(actual));
        validator.validate(xmlSource);
    }
    
    /**
     * Verifies that {@link ProjectModelSerializer#deserialize(java.io.InputStream, String)} deserializes the attributes
     * of {@link ProjectModel} correctly.
     */
    @Test
    public void deserializeProjectModel() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(createXmlConfiguration().getBytes(Charsets.UTF_8));
        
        final ProjectModel projectModel = new ProjectModelSerializer().deserialize(stream, "TestProjectName");
        
        assertEquals("Project name", "TestProjectName", projectModel.getProjectName());
        assertTrue("PMD should be enabled", projectModel.isPMDEnabled());
        assertEquals("Number of rule sets", 4, projectModel.getRuleSets().size());
    }

    /**
     * Verifies that {@link ProjectModelSerializer#deserialize(java.io.InputStream, String)} deserializes the attributes
     * of {@link ProjectModel} correctly.
     */
    @Test
    public void deserializeProjectModelWithoutRuleSets() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(createXmlConfigurationWithoutRuleSets().getBytes(Charsets.UTF_8));
        
        final ProjectModel projectModel = new ProjectModelSerializer().deserialize(stream, "TestProjectName");
        
        assertEquals("Project name", "TestProjectName", projectModel.getProjectName());
        assertFalse("PMD should be disabled", projectModel.isPMDEnabled());
        assertEquals("Number of rule sets", 0, projectModel.getRuleSets().size());
    }
    
    /**
     * Verifies that {@link ProjectModelSerializer#deserialize(java.io.InputStream, String)} deserializes the attributes
     * of a project {@link RuleSetModel} correctly.
     */
    @Test
    public void deserializeProjectRuleSetModel() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(createXmlConfiguration().getBytes(Charsets.UTF_8));
        
        final ProjectModel projectModel = new ProjectModelSerializer().deserialize(stream, "TestProjectName");
        
        assertRuleSetModel(projectModel, LocationContext.PROJECT, "Project Rule Set", "pmd.xml");
    }

    /**
     * Verifies that {@link ProjectModelSerializer#deserialize(java.io.InputStream, String)} deserializes the attributes
     * of a workspace {@link RuleSetModel} correctly.
     */
    @Test
    public void deserializeWorkspaceRuleSetModel() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(createXmlConfiguration().getBytes(Charsets.UTF_8));
        
        final ProjectModel projectModel = new ProjectModelSerializer().deserialize(stream, "TestProjectName");
        
        assertRuleSetModel(projectModel, LocationContext.WORKSPACE, "Workspace Rule Set", "Projext X/pmd.xml");
    }

    /**
     * Verifies that {@link ProjectModelSerializer#deserialize(java.io.InputStream, String)} deserializes the attributes
     * of a filesystem {@link RuleSetModel} correctly.
     */
    @Test
    public void deserializeFilesystemRuleSetModel() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(createXmlConfiguration().getBytes(Charsets.UTF_8));
        
        final ProjectModel projectModel = new ProjectModelSerializer().deserialize(stream, "TestProjectName");
        
        assertRuleSetModel(projectModel, LocationContext.FILESYSTEM, "Filesystem Rule Set", "x:\\pmx.xml");
    }
    
    /**
     * Verifies that {@link ProjectModelSerializer#deserialize(java.io.InputStream, String)} deserializes the attributes
     * of a remote {@link RuleSetModel} correctly.
     */
    @Test
    public void deserializeRemoteRuleSetModel() throws IOException {
        final ByteArrayInputStream stream = new ByteArrayInputStream(createXmlConfiguration().getBytes(Charsets.UTF_8));
        
        final ProjectModel projectModel = new ProjectModelSerializer().deserialize(stream, "TestProjectName");
        
        assertRuleSetModel(projectModel, LocationContext.REMOTE, "Remote Rule Set", "http://example.org/pmd.xml");
    }

    private void assertRuleSetModel(final ProjectModel projectModel, final LocationContext context, final String name, final String path) {
        final RuleSetModel remoteRuleSet = extractRuleSetModel(projectModel, context);
        assertEquals("Name of the " + context + " rule set", name, remoteRuleSet.getName());
        assertEquals("Path of the " + context + " rule set", path, remoteRuleSet.getLocation().getPath());
    }
    
    /**
     * Extracts a rule set model from a project model depending on its location context. This method also verifies that
     * only one rule set model with the provided location context exists ({@code getOnlyElement(...)} throws an
     * {@code IllegalArgumentException} if there is more than one model with the provided location context).
     */
    private RuleSetModel extractRuleSetModel(final ProjectModel model, final LocationContext context) {
        return getOnlyElement(filter(model.getRuleSets(), new LocationContextFilter(context)));
    }
    
    private static final class LocationContextFilter implements Predicate<RuleSetModel> {

        private final LocationContext context;
        
        public LocationContextFilter(final LocationContext context) {
            this.context = context;
        }
        
        @Override
        public boolean apply(final RuleSetModel model) {
            return model.getLocation().getContext() == context;
        }
        
    }

}
