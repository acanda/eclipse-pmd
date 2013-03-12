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

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.lang.Language;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

import ch.acanda.eclipse.pmd.PMDPlugin;

import com.google.common.collect.ImmutableMap;

/**
 * Analyzes files for coding problems, bugs and inefficient code, i.e. runs PMD.
 * 
 * @author Philip Graf
 */
public final class Analyzer {
    
    private static final ImmutableMap<String, Language> LANGUAGES;
    static {
        final Map<String, Language> languages = new HashMap<>();
        for (final Language language : Language.values()) {
            for (final String extension : language.getExtensions()) {
                languages.put(extension, language);
            }
        }
        LANGUAGES = ImmutableMap.copyOf(languages);
    }
    
    /**
     * Analyzes a single file.
     * 
     * @param file The file to analyize.
     * @param ruleSets The rule sets against the file will be analyzed.
     * @param violationProcessor The processor that processes the violated rules.
     */
    public void analyze(final IFile file, final RuleSets ruleSets, final ViolationProcessor violationProcessor) {
        try {
            if (isValidFile(file, ruleSets)) {
                final Language language = LANGUAGES.get(file.getFileExtension().toLowerCase());
                if (isValidLanguage(language)) {
                    final PMDConfiguration configuration = new PMDConfiguration();
                    final InputStreamReader reader = new InputStreamReader(file.getContents(), file.getCharset());
                    final RuleContext context = PMD.newRuleContext(file.getName(), file.getRawLocation().toFile());
                    context.setLanguageVersion(language.getDefaultVersion());
                    new SourceCodeProcessor(configuration).processSourceCode(reader, ruleSets, context);
                    final Iterator<RuleViolation> violations = context.getReport().getViolationTree().iterator();
                    violationProcessor.annotate(file, violations);
                }
            }
        } catch (CoreException | PMDException | IOException e) {
            PMDPlugin.getDefault().error("Could not run PMD on file " + file.getRawLocation(), e);
        }
    }
    
    private boolean isValidFile(final IFile file, final RuleSets ruleSets) {
        // derived (i.e. generated or compiled) files are not analyzed
        return !file.isDerived()
                // the file must exist
                && file.isAccessible()
                // the file must have an extension so we can determine the language
                && file.getFileExtension() != null
                // the file must not be excluded in the pmd configuration
                && ruleSets.applies(file.getRawLocation().toFile());
    }
    
    private boolean isValidLanguage(final Language language) {
        return language != null
                && language.getDefaultVersion() != null
                && language.getDefaultVersion().getLanguageVersionHandler() != null;
    }
    
}
