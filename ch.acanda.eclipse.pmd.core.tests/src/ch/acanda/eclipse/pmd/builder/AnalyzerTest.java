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

package ch.acanda.eclipse.pmd.builder;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Unit tests for {@link Analyzer}.
 * 
 * @author Philip Graf
 */
public class AnalyzerTest {
    
    /**
     * Verifies that {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor)} can analyze Java files.
     */
    @Test
    public void analyzeJava() {
        analyze("class A extends Object {}", "UTF-8", "java", "rulesets/java/basic.xml/ExtendsObject", "ExtendsObject");
    }
    
    /**
     * Verifies that {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor)} can analyze xml files.
     */
    @Test
    public void analyzeXML() {
        analyze("<cdata><![CDATA[[bar]]></cdata>", "UTF-8", "xml", "rulesets/xml/basic.xml/MistypedCDATASection", "MistypedCDATASection");
    }

    /**
     * Verifies that {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor)} can analyze jsp files.
     */
    @Test
    public void analyzeJSP() {
        analyze("<jsp:forward page='a.jsp'/>", "UTF-8", "jsp", "rulesets/jsp/basic.xml/NoJspForward", "NoJspForward");
    }

    /**
     * Verifies that {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor)} can analyze xsl files.
     */
    @Test
    public void analyzeXSL() {
        analyze("<variable name=\"var\" select=\"//item/descendant::child\"/>", "UTF-8", "xsl",
                "rulesets/xsl/xpath.xml/AvoidAxisNavigation", "AvoidAxisNavigation");
    }

    /**
     * Verifies that {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor)} can analyze Ecmascript files.
     */
    @Test
    public void analyzeEcmascript() {
        analyze("var z = 1.12345678901234567;", "UTF-8", "js",
                "rulesets/ecmascript/basic.xml/InnaccurateNumericLiteral", "InnaccurateNumericLiteral");
    }

    /**
     * Verifies that {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor)} doesn't throw a NullPointerException
     * when the file to analyze does not have a file extension.
     */
    @Test
    public void analyzeFileWithoutExtension() {
        analyze("Hello World", "UTF-8", null, "rulesets/java/basic.xml/ExtendsObject");
    }
    
    /**
     * Prepares the arguments, calls {@link Analyzer#analyze(IFile, RuleSets, ViolationProcessor), and verifies that it
     * invokes {@link ViolationProcessor#annotate(IFile, Iterator) with the correct rule violations.
     */
    public void analyze(final String content, final String charset, final String fileExtension, final String ruleSetRefId,
            final String... violatedRules) {
        try {
            final IFile file = mock(IFile.class);
            final ViolationProcessor violationProcessor = mock(ViolationProcessor.class);
            when(file.isAccessible()).thenReturn(true);
            when(file.getFileExtension()).thenReturn(fileExtension);
            when(file.getCharset()).thenReturn(charset);
            when(file.getContents()).thenReturn(new ByteArrayInputStream(content.getBytes(charset)));
            final IPath path = mock(IPath.class);
            when(file.getRawLocation()).thenReturn(path);
            when(path.toFile()).thenReturn(new File("test." + fileExtension));
            
            final RuleSets ruleSets = new RuleSetFactory().createRuleSets(ruleSetRefId);
            new Analyzer().analyze(file, ruleSets, violationProcessor);
            
            final int invokations = violatedRules.length > 0 ? 1 : 0;
            verify(violationProcessor, times(invokations)).annotate(same(file), violations(violatedRules));
        } catch (CoreException | IOException | RuleSetNotFoundException e) {
            throw new RuntimeException(e);
        }
        
    }
    
    private Iterator<RuleViolation> violations(final String... ruleNames) {
        return argThat(new RuleViolationIteratorMatcher(ruleNames));
    }

    private static class RuleViolationIteratorMatcher extends BaseMatcher<Iterator<RuleViolation>> {
        
        private final List<String> expectedRuleNames;

        public RuleViolationIteratorMatcher(final String... ruleNames) {
            expectedRuleNames = Lists.newArrayList(ruleNames);
        }

        @Override
        public boolean matches(final Object item) {
            if (item instanceof Iterator) {
                @SuppressWarnings("unchecked")
                final Iterator<RuleViolation> violations = (Iterator<RuleViolation>) item;
                final Iterator<String> actualRuleNames = Iterators.transform(violations, new RuleNameExtractor());
                return Iterators.elementsEqual(expectedRuleNames.iterator(), actualRuleNames);
            }
            return false;
        }
        
        @Override
        public void describeTo(final Description description) {
            description.appendText("Iterator returning the following violations " + Iterables.toString(expectedRuleNames));
        }
        
        private static class RuleNameExtractor implements Function<RuleViolation, String> {
            @Override
            public String apply(final RuleViolation violation) {
                final Rule rule = violation.getRule();
                if (rule != null) {
                    return rule.getName();
                }
                return null;
            }
        }

    }
    
}
