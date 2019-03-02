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

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.osgi.framework.Version;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterators;

import ch.acanda.eclipse.pmd.java.resolution.QuickFixTestData.TestParameters;
import ch.acanda.eclipse.pmd.marker.MarkerUtil;
import ch.acanda.eclipse.pmd.marker.MarkerUtil.Range;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * Quick fixes usually depend on an exact location of a violation to be able to work correctly. This regression test
 * verifies that the range values of PMD violations (begin/end line, begin/end column) are as expected.
 *
 * @author Philip Graf
 */
@RunWith(value = Parameterized.class)
public class PMDIntegrationTest {

    private static final ImmutableCollection<String> TEST_DATA_XML = ImmutableList.of(
            "bestpractices/DefaultLabelNotLastInSwitchStmt.xml",
            "bestpractices/MethodReturnsInternalArray.xml",
            "bestpractices/UseCollectionIsEmpty.xml",
            "bestpractices/UseVarargs.xml",
            "codestyle/ExtendsObject.xml",
            "codestyle/LocalVariableCouldBeFinal.xml",
            "codestyle/MethodArgumentCouldBeFinal.xml",
            "codestyle/UnnecessaryReturn.xml",
            "design/SingularField.xml",
            "design/UselessOverridingMethod.xml",
            "design/UseUtilityClass.xml",
            "errorprone/EmptyFinallyBlock.xml",
            "errorprone/EmptyIfStmt.xml",
            "errorprone/EmptyInitializer.xml",
            "errorprone/EmptyStatementBlock.xml",
            "errorprone/EmptyStatementNotInLoop.xml",
            "errorprone/EmptyStaticInitializer.xml",
            "errorprone/EmptySwitchStatements.xml",
            "errorprone/EmptySynchronizedBlock.xml",
            "errorprone/EmptyTryBlock.xml",
            "errorprone/EmptyWhileStmt.xml",
            "errorprone/EqualsNull.xml",
            "errorprone/SuspiciousHashcodeMethodName.xml",
            "multithreading/UseNotifyAllInsteadOfNotify.xml",
            "performance/AddEmptyString.xml",
            "performance/AppendCharacterWithChar.xml",
            "performance/ByteInstantiationAutoboxing.xml",
            "performance/ByteInstantiationValueOf.xml",
            "performance/IntegerInstantiationAutoboxing.xml",
            "performance/IntegerInstantiationValueOf.xml",
            "performance/LongInstantiationAutoboxing.xml",
            "performance/LongInstantiationValueOf.xml",
            "performance/RedundantFieldInitializer.xml",
            "performance/ShortInstantiationAutoboxing.xml",
            "performance/ShortInstantiationValueOf.xml",
            "performance/SimplifyStartsWith.xml",
            "performance/StringToString.xml",
            "performance/UseIndexOfChar.xml",
            "performance/UnnecessaryCaseChange.xml");

    private final String testDataXml;
    private final TestParameters params;

    public PMDIntegrationTest(final String testDataXml, final TestParameters params) {
        this.testDataXml = testDataXml;
        this.params = params;
    }

    @Parameters
    public static Collection<Object[]> getTestData() {
        final Builder<Object[]> testData = ImmutableList.builder();
        for (final String tests : TEST_DATA_XML) {
            try (InputStream stream = QuickFixTestData.class.getResourceAsStream(tests)) {
                assertNotNull("Test data file " + tests + " not found.", stream);
                final Collection<TestParameters> data = QuickFixTestData.createTestData(stream);
                for (final TestParameters params : data) {
                    testData.add(new Object[] { tests, params });
                }
            } catch (final IOException e) {
                fail(e.getMessage());
            }
        }
        return testData.build();
    }

    @Test
    public void violationRangeAndRuleId() throws IOException, RuleSetNotFoundException, PMDException {
        checkState(params.language.isPresent(), "%s: language is missing", testDataXml);
        checkState(params.pmdReferenceId.isPresent(), "%s: pmdReferenceId is missing", testDataXml);

        final Matcher languageMatcher = Pattern.compile("(.*)\\s+(\\d+[\\.\\d]+)").matcher(params.language.get());
        checkState(languageMatcher.matches(), "%s: language must be formated '<terse-name> <version>'", testDataXml);

        final Language language = LanguageRegistry.findLanguageByTerseName(languageMatcher.group(1));
        if (language == null) {
            failDueToInvalidTerseName(languageMatcher.group(1));
        }
        final LanguageVersion languageVersion = language.getVersion(languageMatcher.group(2));
        final String fileExtension = "." + languageVersion.getLanguage().getExtensions().get(0);
        final File sourceFile = File.createTempFile(getFilePrefix(params), fileExtension);
        try {
            final PMDConfiguration configuration = new PMDConfiguration();
            final Reader reader = new StringReader(params.source);
            final RuleContext context = PMD.newRuleContext(sourceFile.getName(), sourceFile);
            context.setLanguageVersion(languageVersion);
            final RuleSets ruleSets = new RuleSetFactory().createRuleSets(params.pmdReferenceId.get());
            new SourceCodeProcessor(configuration).processSourceCode(reader, ruleSets, context);

            // Only verify when PMD actually reports an error. There are a few test cases where the quick fix can
            // handle violations that PMD does not (yet) find.
            if (!context.getReport().isEmpty()) {
                // PMD might find more than one violation. If there is one with a matching range then the test passes.
                final Optional<RuleViolation> violation = Iterators.tryFind(context.getReport().iterator(),
                        new Predicate<RuleViolation>() {
                            @Override
                            public boolean apply(final RuleViolation violation) {
                                final Range range = MarkerUtil.getAbsoluteRange(params.source, violation);
                                return params.offset == range.getStart() && params.length == range.getEnd() - range.getStart();
                            }
                        });
                assertTrue(testDataXml + " > " + params.name + ": couldn't find violation with expected range (offset = " + params.offset
                        + ", length = " + params.length + ")", violation.isPresent());

                final JavaQuickFixGenerator generator = new JavaQuickFixGenerator();
                final PMDMarker marker = mock(PMDMarker.class);
                final String ruleId = MarkerUtil.createRuleId(violation.get().getRule());
                when(marker.getRuleId()).thenReturn(ruleId);
                when(marker.getMarkerText()).thenReturn("");
                final JavaQuickFixContext quickFixContext = new JavaQuickFixContext(new Version(languageVersion.getVersion()));

                assertTrue("The Java quick fix generator should have quick fixes for " + ruleId,
                        generator.hasQuickFixes(marker, quickFixContext));
                assertTrue("The Java quick fix generator should have at least one quick fix besides the SuppressWarningsQuickFix for "
                        + ruleId,
                        generator.getQuickFixes(marker, quickFixContext).size() > 1);
            }
        } finally {
            sourceFile.delete();
        }
    }

    private static String getFilePrefix(final TestParameters params) {
        return params.pmdReferenceId.transform(PMDIntegrationTest::getRuleId).or("") + '-' + params.name + '-';
    }

    private static String getRuleId(final String pmdReferenceId) {
        final Matcher matcher = Pattern.compile(".*/([^/]+)").matcher(pmdReferenceId);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }

    private void failDueToInvalidTerseName(final String languageTerseName) {
        final String msg;
        if (LanguageRegistry.getLanguages().isEmpty()) {
            msg = String.format("Cannot find the language for terse name '%s'"
                    + " as there aren't any registered languages in the PMD language registry.",
                    languageTerseName);
        } else {
            final String knownLanguages =
                    LanguageRegistry.getLanguages().stream()
                            .map(l -> l.getTerseName())
                            .collect(Collectors.joining(", "));
            msg = String.format("The language terse name '%s' is not supported by PMD. The supported language terse names are: %s.",
                    languageTerseName, knownLanguages);
        }
        fail(msg);
    }

}
