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

package ch.acanda.eclipse.pmd.marker;

import java.io.Reader;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.JavaCharStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

/**
 * Utility for creating, adding and removing PMD markers.
 * 
 * @author Philip Graf
 */
public final class MarkerUtil {
    
    private static final int PMD_TAB_SIZE = new Tabs().size;
    
    private static final String MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdMarker";
    private static final String LONG_MARKER_TYPE = "ch.acanda.eclipse.pmd.core.pmdLongMarker";
    
    private MarkerUtil() {
        // hide constructor of utility class
    }
    
    /**
     * Removes all PMD markers from a file.
     */
    public static void removeAllMarkers(final IFile file) throws CoreException {
        file.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_ZERO);
    }
    
    /**
     * Removes all PMD markers from a project and all the files it contains.
     */
    public static void removeAllMarkers(final IProject project) throws CoreException {
        project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
    }
    
    /**
     * Adds a PMD Marker to a file.
     * 
     * @param file The marker will be added to this file.
     * @param content The content of the file.
     * @param violation The PMD rule violation.
     * @return The created marker.
     * @throws CoreException Thrown when the file does not exist or its project is closed.
     */
    public static IMarker addMarker(final IFile file, final String content, final RuleViolation violation) throws CoreException {
        final boolean isLongMarker = violation.getBeginLine() != violation.getEndLine();
        final IMarker marker = file.createMarker(isLongMarker ? LONG_MARKER_TYPE : MARKER_TYPE);
        final PMDMarker pmdMarker = new PMDMarker(marker);
        marker.setAttribute(IMarker.MESSAGE, violation.getDescription());
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
        marker.setAttribute(IMarker.LINE_NUMBER, Math.max(violation.getBeginLine(), 0));
        final Range range = getAbsoluteRange(content, violation);
        final int start = Math.max(range.getStart(), 0);
        marker.setAttribute(IMarker.CHAR_START, start);
        final int end = Math.max(range.getEnd(), 0);
        marker.setAttribute(IMarker.CHAR_END, end);
        if (!isLongMarker) {
            pmdMarker.setMarkerText(content.substring(start, end));
        }
        final Rule rule = violation.getRule();
        final String ruleId = rule.getLanguage().getTerseName() + "." + rule.getRuleSetName().toLowerCase() + "." + rule.getName();
        pmdMarker.setRuleId(ruleId);
        pmdMarker.setViolationClassName(violation.getClassName());
        pmdMarker.setRuleName(rule.getName());
        return marker;
    }
    
    public static Range getAbsoluteRange(final String content, final RuleViolation violation) {
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
    
    private static int getAbsolutePosition(final String content, final int lineOffset, final int pmdCharOffset) {
        int pmdCharCounter = 0;
        int absoluteOffset = lineOffset;
        while (pmdCharCounter < pmdCharOffset) {
            if (absoluteOffset < content.length()) {
                final char c = content.charAt(absoluteOffset);
                if (c == '\t') {
                    pmdCharCounter = (pmdCharCounter / PMD_TAB_SIZE + 1) * PMD_TAB_SIZE;
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
    
    public static final class Range {
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
