// =====================================================================
//
// Copyright (C) 2012 - 2019, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import ch.acanda.eclipse.pmd.java.resolution.QuickFixTestData.TestParameters;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.WrappingPMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Base class for testing quick fix tests based on {@link ASTQuickFix}. An extending class must provide a static method
 * with the annotation {@link org.junit.runners.Parameterized.Parameters} that returns the parameters for the test case,
 * e.g:
 *
 * <pre>
 * &#064;Parameters
 * public static Collection&lt;Object[]&gt; getTestData() {
 *     return createTestData(ExtendsObjectQuickFixTest.class.getResourceAsStream(&quot;ExtendsObject.xml&quot;));
 * }
 * </pre>
 *
 * The easiest way to implement this method is to use {@link QuickFixTestData#createTestData(InputStream)} and provide
 * an {@code InputStream} to an XML file containing all the test data. See {@link QuickFixTestData} for the format of
 * the XML file.
 *
 * See {@link ch.acanda.eclipse.pmd.java.resolution.codestyle.ExtendsObjectQuickFixTest ExtendsObjectQuickFixTest} for a
 * complete example.
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
            when(marker.getAttribute(eq("ruleName"), isA(String.class))).thenReturn(params.rulename.orElse(null));
            final String markerText = params.source.substring(params.offset, params.offset + params.length);
            if (!markerText.contains("\n")) {
                when(marker.getAttribute(eq("markerText"), isA(String.class))).thenReturn(markerText);
            }
            return (ASTQuickFix<ASTNode>) quickFixClass.getConstructor(PMDMarker.class).newInstance(new WrappingPMDMarker(marker));
        } catch (SecurityException | ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Object[]> createTestData(final InputStream testCase) {
        return Lists.transform(QuickFixTestData.createTestData(testCase), params -> new Object[] { params });
    }

    @Test
    public void apply() throws MalformedTreeException, BadLocationException {
        final ASTQuickFix<ASTNode> quickFix = getQuickFix();
        final org.eclipse.jface.text.Document document = new org.eclipse.jface.text.Document(params.source);
        final CompilationUnit ast = createAST(document, quickFix);
        final ASTNode node = findNode(params, ast, quickFix);

        quickFix.apply(node);

        final String actual = rewriteAST(document, ast);
        assertEquals("Result of applying the quick fix " + quickFix.getClass().getSimpleName() + " to the test " + params.name,
                params.expectedSource, actual);
    }

    private ASTNode findNode(final TestParameters params, final CompilationUnit ast, final ASTQuickFix<ASTNode> quickFix) {
        final Class<? extends ASTNode> nodeType = quickFix.getNodeType();
        final NodeFinder<CompilationUnit, ASTNode> finder = quickFix.getNodeFinder(new Position(params.offset, params.length));
        final Optional<ASTNode> node = finder.findNode(ast);
        assertTrue("Couldn't find node of type " + nodeType.getSimpleName() + "."
                + " Check the position of the marker in test " + params.name + ".", node.isPresent());
        return node.get();
    }

    private CompilationUnit createAST(final org.eclipse.jface.text.Document document, final ASTQuickFix<ASTNode> quickFix) {
        final ASTParser astParser = ASTParser.newParser(AST.JLS8);
        astParser.setSource(document.get().toCharArray());
        astParser.setKind(ASTParser.K_COMPILATION_UNIT);
        astParser.setResolveBindings(quickFix.needsTypeResolution());
        astParser.setEnvironment(new String[0], new String[0], new String[0], true);
        final String name = last(params.pmdReferenceId.orElse("QuickFixTest").split("/"));
        astParser.setUnitName(format("{0}.java", name));
        final String version = last(params.language.orElse("java 1.8").split("\\s+"));
        astParser.setCompilerOptions(ImmutableMap.<String, String>builder()
                .put(JavaCore.COMPILER_SOURCE, version)
                .put(JavaCore.COMPILER_COMPLIANCE, version)
                .put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, version)
                .build());
        final CompilationUnit ast = (CompilationUnit) astParser.createAST(null);
        ast.recordModifications();
        return ast;
    }

    private String last(final String... strings) {
        return strings[strings.length - 1];
    }

    private String rewriteAST(final org.eclipse.jface.text.Document document, final CompilationUnit ast) throws BadLocationException {
        final TextEdit edit = ast.rewrite(document, getRewriteOptions());
        edit.apply(document);
        return document.get();
    }

    private Map<String, String> getRewriteOptions() {
        final Map<String, String> options = new HashMap<>();
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_SWITCH, DefaultCodeFormatterConstants.TRUE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_SWITCHSTATEMENTS_COMPARE_TO_CASES, DefaultCodeFormatterConstants.TRUE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INDENT_BREAKS_COMPARE_TO_CASES, DefaultCodeFormatterConstants.TRUE);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_ASSIGNMENT_OPERATOR, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_ASSIGNMENT_OPERATOR, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ANNOTATION, JavaCore.INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_ARRAY_INITIALIZER, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_LOCAL_VARIABLE, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_METHOD, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_PACKAGE, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_PARAMETER, JavaCore.DO_NOT_INSERT);
        options.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_NEW_LINE_AFTER_ANNOTATION_ON_TYPE, JavaCore.DO_NOT_INSERT);
        return options;
    }

    @Test
    public void getImage() throws IllegalAccessException, NoSuchFieldException, SecurityException {
        final ImageDescriptor imageDescriptor = getQuickFix().getImageDescriptor();
        if (params.expectedImage.isPresent()) {
            final Field field = PMDPluginImages.class.getDeclaredField(params.expectedImage.get());
            assertEquals("Quick fix image descriptor in test " + params.name, field.get(null), imageDescriptor);
        } else {
            assertNotNull("Quick fix image descriptor must not be null (test " + params.name + ")", imageDescriptor);
        }
    }

    @Test
    public void getLabel() {
        final String label = getQuickFix().getLabel();
        if (params.expectedLabel.isPresent()) {
            assertEquals("Quick fix label in test " + params.name, params.expectedLabel.get(), label);
        } else {
            assertNotNull("Quick fix label must not be null (test " + params.name + ")", label);
        }
    }

    @Test
    public void getDescription() {
        final String description = getQuickFix().getDescription();
        if (params.expectedDescription.isPresent()) {
            assertEquals("Quick fix description in test " + params.name, params.expectedDescription.get(), description);
        } else {
            assertNotNull("Quick fix description must not be null (test " + params.name + ")", description);
        }
    }

}
