// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.sunsecure;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.create;
import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.replace;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/sunsecure.html#MethodReturnsInternalArray">MethodReturnsInternalArray</a>. It
 * replaces <code>return foo;</code> with <code>return foo.clone();</code>.
 *
 * @author Philip Graf
 */
public class MethodReturnsInternalArrayQuickFix extends ASTQuickFix<ReturnStatement> {

    public MethodReturnsInternalArrayQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        return "Return a clone of the array";
    }

    @Override
    public String getDescription() {
        final String markerText = marker.getMarkerText();
        final String returnStatement = markerText.substring(0, markerText.length() - 1);
        return "Replaces <b>" + returnStatement + "</b> with <b>" + returnStatement + ".clone()</b>.";
    }

    @Override
    protected boolean apply(final ReturnStatement node) {
        final Expression expression = node.getExpression();
        final AST ast = expression.getAST();
        final MethodInvocation replacement = create(ast, MethodInvocation.class);
        replacement.setExpression(copy(expression));
        final SimpleName name = create(ast, SimpleName.class);
        name.setIdentifier("clone");
        replacement.setName(name);
        return replace(expression, replacement);
    }


    @Override
    protected NodeFinder<CompilationUnit, ReturnStatement> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

}
