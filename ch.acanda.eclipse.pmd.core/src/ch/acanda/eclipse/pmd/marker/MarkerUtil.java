// =====================================================================
//
// Copyright (C) 2012 - 2016, Philip Graf
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
        final WrappingPMDMarker pmdMarker = new WrappingPMDMarker(marker);
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
        pmdMarker.setRuleId(createRuleId(rule));
        pmdMarker.setViolationClassName(violation.getClassName());
        pmdMarker.setVariableName(violation.getVariableName());
        pmdMarker.setRuleName(rule.getName());
        pmdMarker.setLanguage(violation.getRule().getLanguage().getTerseName());
        return marker;
    }

    public static String createRuleId(final Rule rule) {
        return rule.getLanguage().getTerseName() + "." + rule.getRuleSetName().toLowerCase() + "." + rule.getName();
    }

    public static Range getAbsoluteRange(final String content, final RuleViolation violation) {
        Range range;
        try {
            range = calculateAbsoluteRange(content, violation);
        } catch (final BadLocationException e) {
            range = new Range(0, 0);
        }
        return range;
    }

    private static Range calculateAbsoluteRange(final String content, final RuleViolation violation) throws BadLocationException {
        final Document document = new Document(content);

        // violation line and column start at one, the marker's start and end positions at zero
        final int start = getAbsolutePosition(content, document.getLineOffset(violation.getBeginLine() - 1), violation.getBeginColumn());
        final int end = getAbsolutePosition(content, document.getLineOffset(violation.getEndLine() - 1), violation.getEndColumn());

        // for some rules PMD creates violations with the end position before the start position
        final Range range;
        if (start <= end) {
            range = new Range(start - 1, end);
        } else {
            range = new Range(end - 1, start);
        }

        return range;
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
            // if (start <= end) {
            this.start = start;
            this.end = end;
            // } else {
            // this.start = end;
            // this.end = start;
            // }
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
