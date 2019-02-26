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

package ch.acanda.eclipse.pmd.java.resolution.errorprone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.ASTUtil;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/design.html#EqualsNull">EqualsNull</a>. It
 * replaces {@code x.equals(null)} with {@code x == null}.
 * 
 * @author Philip Graf
 */
public class EqualsNullQuickFix extends ASTQuickFix<MethodInvocation> {
    
    private static final Pattern METHOD_INVOCATION_EXPRESSION = Pattern.compile("^(.*)\\s*\\.\\s*equals\\s*\\(\\s*null\\s*\\)$");
    private final String methodInvocationExpression;
    
    public EqualsNullQuickFix(final PMDMarker marker) {
        super(marker);
        final Matcher matcher = METHOD_INVOCATION_EXPRESSION.matcher(marker.getMarkerText());
        if (matcher.matches()) {
            methodInvocationExpression = matcher.group(1);
        } else {
            methodInvocationExpression = null;
        }
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        if (methodInvocationExpression != null) {
            return "Replace with " + methodInvocationExpression + " == null";
        }
        return "Replace equals with ==";
    }
    
    @Override
    public String getDescription() {
        if (methodInvocationExpression != null) {
            return "Replaces <b>" + marker.getMarkerText() + "</b> with <b>" + methodInvocationExpression + " == null</b>.";
        }
        return "Replaces equals with ==.";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, MethodInvocation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Replaces {@code x.equals(null)} with {@code x == null}.
     */
    @Override
    protected boolean apply(final MethodInvocation node) {
        final AST ast = node.getAST();
        final InfixExpression infix = (InfixExpression) ast.createInstance(InfixExpression.class);
        infix.setOperator(Operator.EQUALS);
        infix.setLeftOperand(ASTUtil.copy(node.getExpression()));
        infix.setRightOperand((NullLiteral) ast.createInstance(NullLiteral.class));
        ASTUtil.replace(node, infix);
        return true;
    }
    
}
