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

package ch.acanda.eclipse.pmd.builder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.acanda.eclipse.pmd.marker.MarkerUtil;
import ch.acanda.eclipse.pmd.marker.MarkerUtil.Range;
import ch.acanda.eclipse.pmd.marker.resolution.QuickFixTestData;
import ch.acanda.eclipse.pmd.marker.resolution.QuickFixTestData.TestParameters;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterators;

/**
 * Quick fixes usually depend on an exact location of a violation to be able to work correctly. This regression test
 * verifies that the range values of PMD violations (begin/end line, begin/end column) are as expected.
 * 
 * @author Philip Graf
 */
@RunWith(value = Parameterized.class)
public class PMDIntegrationTest {
    
    private static final ImmutableCollection<String> TEST_DATA_XML = ImmutableList.of(
            "java/basic/ExtendsObject.xml",
            "java/design/DefaultLabelNotLastInSwitchStmt.xml",
            "java/design/EqualsNull.xml",
            "java/design/SingularField.xml",
            "java/design/UseCollectionIsEmpty.xml",
            "java/design/UseNotifyAllInsteadOfNotify.xml",
            "java/emptycode/EmptyFinallyBlock.xml",
            "java/emptycode/EmptyIfStmt.xml",
            "java/emptycode/EmptyInitializer.xml",
            "java/emptycode/EmptyStatementBlock.xml",
            "java/emptycode/EmptyStatementNotInLoop.xml",
            "java/emptycode/EmptyStaticInitializer.xml",
            "java/emptycode/EmptySwitchStatements.xml",
            "java/emptycode/EmptySynchronizedBlock.xml",
            "java/emptycode/EmptyTryBlock.xml",
            "java/emptycode/EmptyWhileStmt.xml",
            "java/naming/SuspiciousHashcodeMethodName.xml",
            "java/optimization/LocalVariableCouldBeFinal.xml",
            "java/optimization/MethodArgumentCouldBeFinal.xml",
            "java/optimization/RedundantFieldInitializer.xml",
            "java/optimization/SimplifyStartsWith.xml");
    
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
            try (final InputStream stream = QuickFixTestData.class.getResourceAsStream(tests)) {
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
    public void violationRange() throws IOException, RuleSetNotFoundException, PMDException {
        if (params.language == null) {
            fail(testDataXml + ": language is missing");
        }
        final LanguageVersion languageVersion = LanguageVersion.findByTerseName(params.language);
        final String fileExtension = "." + languageVersion.getLanguage().getExtensions().get(0);
        final File sourceFile = File.createTempFile(getClass().getSimpleName(), fileExtension);
        try {
            final PMDConfiguration configuration = new PMDConfiguration();
            final Reader reader = new StringReader(params.source);
            final RuleContext context = PMD.newRuleContext(sourceFile.getName(), sourceFile);
            context.setLanguageVersion(languageVersion);
            final RuleSets ruleSets = new RuleSetFactory().createRuleSets(params.pmdReferenceId);
            new SourceCodeProcessor(configuration).processSourceCode(reader, ruleSets, context);

            // Only verify when PMD actually reports an error. There are a few test cases where the quick fix can
            // handle violations that PMD does not (yet) find.
            if (!context.getReport().isEmpty()) {
                // PMD might find more than one violation. If there is one with a matching range then the test passes.
                final boolean hasViolationWithMatchingRange = Iterators.any(context.getReport().iterator(),
                        new Predicate<RuleViolation>() {
                            @Override
                            public boolean apply(final RuleViolation violation) {
                                final Range range = MarkerUtil.getAbsoluteRange(params.source, violation);
                                return params.offset == range.getStart() && params.length == range.getEnd() - range.getStart();
                            }
                        });
                assertTrue(testDataXml + " > " + params.name + ": couldn't find violation with expected range (offset = " + params.offset
                        + ", length = " + params.length + ")", hasViolationWithMatchingRange);
            }
        } finally {
            sourceFile.delete();
        }
    }

}
