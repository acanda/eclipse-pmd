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

package ch.acanda.eclipse.pmd.marker.resolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.java.basic.ExtendsObjectQuickFixTest;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Base class for testing quick fix tests based on {@link ASTQuickFix}. An extending class must provide a static method
 * with the annotation {@link Parameters} that returns the parameters for the test case, e.g:
 * 
 * <pre>
 * &#064;Parameters
 * public static Collection&lt;Object[]&gt; getTestData() {
 *     return createTestData(ExtendsObjectQuickFixTest.class.getResourceAsStream(&quot;ExtendsObject.xml&quot;));
 * }
 * </pre>
 * 
 * The easiest way to implement this method is to use {@link #createTestData(InputStream)} and provide an
 * {@code InputStream} to an XML file containing all the test data. The XML file must have the following format:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;tests>
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
 * See {@link ExtendsObjectQuickFixTest} for a complete example.
 * 
 * @author Philip Graf
 * @param <T> The type of the quick fix.
 */
@RunWith(value = Parameterized.class)
@SuppressWarnings({ "PMD.CommentSize", "PMD.AbstractClassWithoutAbstractMethod" })
public abstract class ASTQuickFixTestCase<T extends ASTQuickFix<? extends ASTNode>> {
    
    private final TestParameters params;

    public ASTQuickFixTestCase(final TestParameters parameters) {
        params = parameters;
    }

    @SuppressWarnings("unchecked")
    private ASTQuickFix<ASTNode> getQuickFix() {
        try {
            final Type typeArgument = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            final Class<T> quickFixClass = (Class<T>) typeArgument;
            final IMarker marker = mock(IMarker.class);
            final String markerText = params.source.substring(params.offset, params.offset + params.length);
            when(marker.getAttribute(eq("markerText"), isA(String.class))).thenReturn(markerText);
            return (ASTQuickFix<ASTNode>) quickFixClass.getConstructor(PMDMarker.class).newInstance(new PMDMarker(marker));
        } catch (SecurityException | ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Collection<Object[]> createTestData(final InputStream testCase) {
        final List<Object[]> data = new ArrayList<>();
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = factory.newDocumentBuilder();
            final Document doc = docBuilder.parse(testCase);
            final NodeList tests = doc.getElementsByTagName("test");
            for (int i = 0, size = tests.getLength(); i < size; i++) {
                data.add(new Object[] { getParameters((Element) tests.item(i)) });
            }
        } catch (final ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Invalid test case", e);
        }
        return data;
    }

    private static TestParameters getParameters(final Element test) {
        final TestParameters params = new TestParameters();
        params.name = test.getAttribute("name");
        final Element setup = (Element) test.getElementsByTagName("setup").item(0);
        final NodeList source = setup.getElementsByTagName("source");
        params.source = ltrim(source.item(0).getFirstChild().getNodeValue());
        params.offset = params.source.length();
        params.source += ((Element) source.item(0)).getElementsByTagName("marker").item(0).getFirstChild().getNodeValue();
        params.length = params.source.length() - params.offset;
        params.source += rtrim(source.item(0).getChildNodes().item(2).getNodeValue());
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

    @Test
    public void apply() throws MalformedTreeException, BadLocationException {
        final ASTQuickFix<ASTNode> quickFix = getQuickFix();
        final org.eclipse.jface.text.Document document = new org.eclipse.jface.text.Document(params.source);
        final CompilationUnit ast = createAST(document);
        final ASTNode node = findNode(params, ast, quickFix);
        
        quickFix.apply(node);
        
        final String actual = rewriteAST(document, ast);
        assertEquals("Result of applying the quick fix " + quickFix.getClass().getSimpleName() + " to the test " + params.name,
                params.expectedSource, actual);
    }

    private ASTNode findNode(final TestParameters params, final CompilationUnit ast, final ASTQuickFix<ASTNode> quickFix) {
        final Class<? extends ASTNode> nodeType = quickFix.getNodeType();
        final PositionWithinNodeNodeFinder finder = new PositionWithinNodeNodeFinder(new Position(params.offset, params.length), nodeType);
        final ASTNode node = finder.findNode(ast);
        assertNotNull("Couldn't find node of type " + nodeType.getSimpleName() + "."
                + " Check the position of the marker in test " + params.name + ".", node);
        return node;
    }

    private CompilationUnit createAST(final org.eclipse.jface.text.Document document) {
        final ASTParser astParser = ASTParser.newParser(AST.JLS4);
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setSource(document.get().toCharArray());
        final CompilationUnit ast = (CompilationUnit) astParser.createAST(null);
        ast.recordModifications();
        return ast;
    }

    private String rewriteAST(final org.eclipse.jface.text.Document document, final CompilationUnit ast) throws BadLocationException {
        final TextEdit edit = ast.rewrite(document, getOptions());
        edit.apply(document);
        return document.get();
    }

    private Map<String, String> getOptions() {
        final Map<String, String> options = new HashMap<>();
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        return options;
    }
    
    @Test
    public void getImage() throws IllegalAccessException, NoSuchFieldException, SecurityException {
        final ImageDescriptor imageDescriptor = getQuickFix().getImageDescriptor();
        if (params.expectedImage == null) {
            assertNotNull("Quick fix image descriptor must not be null (test " + params.name + ")", imageDescriptor);
        } else {
            final Field field = PMDPluginImages.class.getDeclaredField(params.expectedImage);
            assertEquals("Quick fix image descriptor in test " + params.name, field.get(null), imageDescriptor);
        }
    }

    @Test
    public void getLabel() {
        final String label = getQuickFix().getLabel();
        if (params.expectedLabel == null) {
            assertNotNull("Quick fix label must not be null (test " + params.name + ")", label);
        } else {
            assertEquals("Quick fix label in test " + params.name, params.expectedLabel, label);
        }
    }

    @Test
    public void getDescription() {
        final String description = getQuickFix().getDescription();
        if (params.expectedDescription == null) {
            assertNotNull("Quick fix description must not be null (test " + params.name + ")", description);
        } else {
            assertEquals("Quick fix description in test " + params.name, params.expectedDescription, description);
        }
    }

    protected static final class TestParameters {
        String name;
        int offset;
        int length;
        String source;
        String expectedSource;
        String expectedImage;
        String expectedLabel;
        String expectedDescription;
    }
}
