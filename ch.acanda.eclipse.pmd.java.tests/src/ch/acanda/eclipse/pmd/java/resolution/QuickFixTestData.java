// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution;

import static org.junit.Assert.assertFalse;

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
 *             &lt;!-- The expected image of the quick fix. This must be the name of a field in
 *                  {@link ch.acanda.eclipse.pmd.ui.util.PMDPluginImages PMDPluginImages}.
 *                  The image is optional. If no image is provided the test verifies only that
 *                  the image is not {@code null}. -->
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
            final Optional<String> pmdReferenceId = getOptionalValue(doc, "pmdReferenceId");
            final Optional<String> language = getOptionalValue(doc, "language");
            final NodeList tests = doc.getElementsByTagName("test");
            for (int i = 0, size = tests.getLength(); i < size; i++) {
                data.add(getParameters(pmdReferenceId, language, (Element) tests.item(i)));
            }
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Invalid test case", e);
        }
        return data;
    }

    private static TestParameters getParameters(final Optional<String> pmdReferenceId, final Optional<String> language,
            final Element test) {
        final TestParameters params = new TestParameters();
        params.pmdReferenceId = pmdReferenceId;
        params.language = language;
        params.name = test.getAttribute("name");
        final Element setup = (Element) test.getElementsByTagName("setup").item(0);
        final NodeList source = setup.getElementsByTagName("source");
        params.source = ltrim(source.item(0).getFirstChild().getNodeValue());
        params.offset = params.source.length();
        params.source += ((Element) source.item(0)).getElementsByTagName("marker").item(0).getFirstChild().getNodeValue();
        params.length = params.source.length() - params.offset;
        params.source += rtrim(source.item(0).getChildNodes().item(2).getNodeValue());
        params.rulename = getOptionalValue(setup, "rulename");
        final Element expected = (Element) test.getElementsByTagName("expected").item(0);
        params.expectedSource = getValue(expected, "source");
        params.expectedImage = getOptionalValue(expected, "image");
        params.expectedLabel = getOptionalValue(expected, "label");
        params.expectedDescription = getOptionalValue(expected, "description");
        return params;
    }

    private static Optional<String> getOptionalValue(final Element element, final String tagName) {
        return getOptionalValue(element.getElementsByTagName(tagName));
    }

    private static Optional<String> getOptionalValue(final NodeList elements) {
        if (elements.getLength() == 0) {
            return Optional.absent();
        }
        return Optional.of(elements.item(0).getFirstChild().getNodeValue().trim());
    }

    private static String getValue(final Element element, final String tagName) {
        return getValue(element.getElementsByTagName(tagName));
    }

    private static Optional<String> getOptionalValue(final Document element, final String tagName) {
        return getOptionalValue(element.getElementsByTagName(tagName));
    }

    private static String getValue(final NodeList elements) {
        assertFalse(elements.getLength() == 0);
        return elements.item(0).getFirstChild().getNodeValue().trim();
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
        int pos = s.length() - 1;
        while (pos >= 0 && s.charAt(pos) <= ' ') {
            pos--;
        }
        return s.substring(0, pos + 1);
    }

    public static final class TestParameters {
        public Optional<String> pmdReferenceId;
        public Optional<String> language;
        public String name;
        public int offset;
        public int length;
        public String source;
        public Optional<String> rulename;
        public String expectedSource;
        public Optional<String> expectedImage;
        public Optional<String> expectedLabel;
        public Optional<String> expectedDescription;
    }

}
