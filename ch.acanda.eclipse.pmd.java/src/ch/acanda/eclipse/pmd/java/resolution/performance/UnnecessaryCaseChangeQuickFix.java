// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.performance;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.replace;
import static java.text.MessageFormat.format;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule
 * <a href="http://pmd.sourceforge.net/rules/java/strings.html#UnnecessaryCaseChange">UnnecessaryCaseChange</a>. It
 * replaces the <code>.toUpperCase().equals(...)</code> with <code>.equalsIgnoreCase(...)</code> and
 * <code>.toUpperCase().equalsIgnoreCase(...)</code> with <code>.equalsIgnoreCase(...)</code>.
 *
 * @author Philip Graf
 */
public class UnnecessaryCaseChangeQuickFix extends ASTQuickFix<MethodInvocation> {

    private final boolean containsEqualsIgnoreCase;

    public UnnecessaryCaseChangeQuickFix(final PMDMarker marker) {
        super(marker);
        final String markerText = marker.getMarkerText();
        containsEqualsIgnoreCase = markerText != null && markerText.contains("equalsIgnoreCase");
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return containsEqualsIgnoreCase ? PMDPluginImages.QUICKFIX_REMOVE : PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        if (containsEqualsIgnoreCase) {
            final String markerText = marker.getMarkerText();
            if (markerText.contains("toLowerCase")) {
                return "Remove .toLowerCase()";
            }
            if (markerText.contains("toUpperCase()")) {
                return "Remove .toUpperCase()";
            }
        }
        return "Replace with .equalsIgnoreCase()";
    }

    @Override
    public String getDescription() {
        final String markerText = marker.getMarkerText();
        if (markerText != null) {
            String replacement = markerText;
            if (!containsEqualsIgnoreCase) {
                replacement = replacement.replaceFirst("\\.equals", ".equalsIgnoreCase");
            }
            replacement = replacement.replaceFirst("\\.to(Upper|Lower)Case\\s*\\(\\s*\\)", "");
            return format("Replaces <b>{0}</b> with <b>{1}</b>.", markerText, replacement);
        }
        return "Replaces the expression with .equalsIgnoreCase().";
    }

    @Override
    protected NodeFinder<CompilationUnit, MethodInvocation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    /**
     * Removes the <code>.toString()</code> from <code>"foo".toString()</code> if the expression is only a part of an
     * statement. Removes the expression completely if it is the whole statement.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean apply(final MethodInvocation node) {
        final AST ast = node.getAST();
        final MethodInvocation invocation = ast.newMethodInvocation();
        if (node.getExpression().getNodeType() == ASTNode.METHOD_INVOCATION) {
            invocation.setExpression(removeCaseChange((MethodInvocation) node.getExpression()));
            invocation.setName(ast.newSimpleName("equalsIgnoreCase"));
            invocation.arguments().add(copy((Expression) node.arguments().get(0)));
            return replace(node, invocation);
        }
        return false;
    }

    private Expression removeCaseChange(final MethodInvocation withCaseChange) {
        final String identifier = withCaseChange.getName().getIdentifier();
        if ("toUpperCase".equals(identifier) || "toLowerCase".equals(identifier)) {
            return copy(withCaseChange.getExpression());
        }
        return copy(withCaseChange);
    }

}
