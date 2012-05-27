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

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Iterator;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.JavaCharStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

/**
 * Processes the rule violations found by a PMD analysis.
 * 
 * @author Philip Graf
 */
public class ViolationProcessor {
    
    private static final int PMD_TAB_SIZE = new Tabs().size;
    
    public void annotate(final IFile file, final Iterator<RuleViolation> violations) throws CoreException, IOException {
        MarkerUtil.removeAllMarkers(file);
        final String content = new String(Files.readAllBytes(file.getRawLocation().toFile().toPath()), file.getCharset());
        while (violations.hasNext()) {
            final RuleViolation violation = violations.next();
            final Range range = getAbsoluteRange(content, violation);
            MarkerUtil.addMarker(file, violation.getDescription(), violation.getBeginLine(), range.getStart(), range.getEnd());
        }
    }
    
    private Range getAbsoluteRange(final String content, final RuleViolation violation) {
        final Document document = new Document(content);
        try {
            // violation line and column start at one, the marker's start and end positions at zero
            final int start = getAbsolutePosition(content, document.getLineOffset(violation.getBeginLine() - 1), violation.getBeginColumn());
            final int end = getAbsolutePosition(content, document.getLineOffset(violation.getEndLine() - 1), violation.getEndColumn());
            return new Range(start - 1, end);
        } catch (final BadLocationException e) {
            return new Range(0, 0);
        }
    }
    
    private int getAbsolutePosition(final String content, final int lineOffset, final int pmdCharOffset) {
        int pmdCharCounter = 0;
        int absoluteOffset = lineOffset;
        while (pmdCharCounter < pmdCharOffset) {
            if (absoluteOffset < content.length()) {
                final char c = content.charAt(absoluteOffset);
                if (c == '\t') {
                    pmdCharCounter += PMD_TAB_SIZE;
                } else {
                    pmdCharCounter++;
                }
            } else {
                break;
            }
            absoluteOffset++;
        }
        return absoluteOffset;
    }
    
    private static final class Range {
        private final int start;
        private final int end;
        
        public Range(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
        
        public int getStart() {
            return start;
        }
        
        public int getEnd() {
            return end;
        }
    }
    
    /**
     * Provides access to {@link JavaCharStream#tabSize}. The tab size is used to calculate the correct start and end
     * character for the marker. {@link RuleViolation#getBeginColumn()} and {@link RuleViolation#getEndColumn()} count a
     * tabulator character as {@code JavaCharStream.tabSize} characters, while the marker counts a tabulator as one
     * character.
     */
    private static final class Tabs extends JavaCharStream {
        
        public int size = super.tabSize;
        
        public Tabs() {
            super((Reader) null);
        }
        
    }
    
}
