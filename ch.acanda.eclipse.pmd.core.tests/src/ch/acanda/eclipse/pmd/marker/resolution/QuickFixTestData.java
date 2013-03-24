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

package ch.acanda.eclipse.pmd.marker.resolution;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

import com.google.common.base.Optional;

/**
 * Reads the test data from an xml file with the following format:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;tests>
 *     &lt;!-- The PMD reference id of the rule that is being tested -->
 *     &lt;pmdReferenceId>rulesets/java/basic.xml/ExtendsObject&lt;/pmdReferenceId>
 * 
 *     &lt;!-- The language and version of the provided source code -->
 *     &lt;language>java 1.7&lt;/language>
 * 
 *     &lt;!-- Every test must have a name. It will be shown in assertion messages
 *          so you can identify the failing test. There can be more than one test in a file. -->
 *     &lt;test name="SimpleExtendsObject">
 * 
 *         &lt;!-- The setup contains all the data to set up the test -->
 *         &lt;setup>
 * 
 *             &lt;!-- The source must be a valid Java compilation unit and must contain a marker.
 *                  The marker marks the position where the quick fix should be applied. -->
 *             &lt;source>
 * class Example extends &lt;marker>Object&lt;/marker> {
 * }
 *             &lt;/source>
 * 
 *         &lt;/setup>
 * 
 *         &lt;!-- The 'expected' part contains all the expected values. -->
 *         &lt;expected>
 * 
 *             &lt;!-- The expected source after the quick fix has been applied. -->
 *             &lt;source>
 * class Example {
 * }
 *             &lt;/source>
 * 
 *             &lt;!-- The expected image of the quick fix. This must be the name of a field in {@link PMDPluginImages}.
 *                  The image is optional. If no image is provided the test verifies only that the image is not {@code null}. -->
 *             &lt;image>QUICKFIX_REMOVE&lt;/image>
 * 
 *             &lt;!-- The expected label of the quick fix. The label is optional.
 *                  If no label is provided the test verifies only that the label is not {@code null}. -->
 *             &lt;label>Remove 'extends Object'&lt;/label>
 * 
 *             &lt;!-- The expected description of the quick fix. The description is optional.
 *                  If no description is provided the test verifies only that the description is not {@code null}. -->
 *             &lt;description>Removes &amp;lt;b>extends Object&amp;lt;/b> from the type declaration of Example&lt;/description>
 * 
 *         &lt;/expected>
 *     &lt;/test>
 * &lt;/tests>
 * </pre>
 * 
 * @author Philip Graf
 */
public class QuickFixTestData {
    
    public static List<TestParameters> createTestData(final InputStream testCase) {
        final List<TestParameters> data = new ArrayList<>();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = factory.newDocumentBuilder();
            final Document doc = docBuilder.parse(testCase);
            final Optional<String> pmdReferenceId = getTagValue("pmdReferenceId", doc);
            final Optional<String> language = getTagValue("language", doc);
            final NodeList tests = doc.getElementsByTagName("test");
            for (int i = 0, size = tests.getLength(); i < size; i++) {
                data.add(getParameters(pmdReferenceId, language, (Element) tests.item(i)));
            }
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Invalid test case", e);
        }
        return data;
    }

    private static Optional<String> getTagValue(final String tagName, final Document doc) {
        final NodeList referenceIds = doc.getElementsByTagName(tagName);
        final Optional<String> pmdReferenceId;
        if(referenceIds.getLength() > 0) {
            pmdReferenceId = Optional.fromNullable(referenceIds.item(0).getFirstChild().getNodeValue());
        } else {
            pmdReferenceId = Optional.absent();
        }
        return pmdReferenceId;
    }
    
    private static TestParameters getParameters(final Optional<String> pmdReferenceId, final Optional<String> language, final Element test) {
        final TestParameters params = new TestParameters();
        params.pmdReferenceId = pmdReferenceId.orNull();
        params.language = language.orNull();
        params.name = test.getAttribute("name");
        final Element setup = (Element) test.getElementsByTagName("setup").item(0);
        final NodeList source = setup.getElementsByTagName("source");
        params.source = ltrim(source.item(0).getFirstChild().getNodeValue());
        params.offset = params.source.length();
        params.source += ((Element) source.item(0)).getElementsByTagName("marker").item(0).getFirstChild().getNodeValue();
        params.length = params.source.length() - params.offset;
        params.source += rtrim(source.item(0).getChildNodes().item(2).getNodeValue());
        final NodeList rulenames = setup.getElementsByTagName("rulename");
        params.rulename = rulenames.getLength() == 0 ? null : rulenames.item(0).getFirstChild().getNodeValue();
        final Element expected = (Element) test.getElementsByTagName("expected").item(0);
        params.expectedSource = expected.getElementsByTagName("source").item(0).getFirstChild().getNodeValue().trim();
        final NodeList image = expected.getElementsByTagName("image");
        params.expectedImage = image.getLength() == 0 ? null : image.item(0).getFirstChild().getNodeValue().trim();
        final NodeList label = expected.getElementsByTagName("label");
        params.expectedLabel = label.getLength() == 0 ? null : label.item(0).getFirstChild().getNodeValue().trim();
        final NodeList description = expected.getElementsByTagName("description");
        params.expectedDescription = description.getLength() == 0 ? null : description.item(0).getFirstChild().getNodeValue().trim();
        return params;
    }
    
    private static String ltrim(final String s) {
        final int len = s.length();
        int pos = 0;
        while (pos < len && s.charAt(pos) <= ' ') {
            pos++;
        }
        return s.substring(pos);
    }
    
    private static String rtrim(final String s) {
        final int len = s.length();
        int pos = s.length() - 1;
        while (pos < len && s.charAt(pos) <= ' ') {
            pos--;
        }
        return s.substring(0, pos + 1);
    }
    
    public static final class TestParameters {
        public String pmdReferenceId;
        public String language;
        public String name;
        public int offset;
        public int length;
        public String source;
        public String rulename;
        public String expectedSource;
        public String expectedImage;
        public String expectedLabel;
        public String expectedDescription;
    }

}
