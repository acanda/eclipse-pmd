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

package ch.acanda.eclipse.pmd.marker.resolution.java.design;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.marker.resolution.ASTUtil;
import ch.acanda.eclipse.pmd.marker.resolution.Finders;
import ch.acanda.eclipse.pmd.marker.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/java/design.html#UseCollectionIsEmpty">UseCollectionIsEmpty</a>. It replaces
 * {@code x.size() == 0} with {@code x.isEmpty()}.
 * 
 * @author Philip Graf
 */
public class UseCollectionIsEmptyQuickFix extends ASTQuickFix<InfixExpression> {
    
    public UseCollectionIsEmptyQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        return "Replace with call to isEmpty()";
    }
    
    @Override
    public String getDescription() {
        return "Replaces call to size() with call to isEmpty().";
    }
    
    @Override
    protected NodeFinder getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Replaces {@code x.size() == 0} or {@code 0 == x.size()} with {@code x.isEmpty()}. Replaces {@code x.size() != 0}
     * or {@code 0 != x.size()} with {@code !x.isEmpty()}.
     */
    @Override
    protected boolean apply(final InfixExpression node) {
        final MethodInvocation size;
        if(node.getLeftOperand() instanceof MethodInvocation) {
            size = (MethodInvocation) node.getLeftOperand();
        } else if (node.getRightOperand() instanceof MethodInvocation) {
            size = (MethodInvocation) node.getRightOperand();
        } else {
            return false;
        }
        
        final AST ast = node.getAST();
        final MethodInvocation invocation = (MethodInvocation) ast.createInstance(MethodInvocation.class);
        invocation.setExpression(ASTUtil.copy(size.getExpression()));
        final SimpleName isEmpty = (SimpleName) ast.createInstance(SimpleName.class);
        isEmpty.setIdentifier("isEmpty");
        invocation.setName(isEmpty);
        
        final Expression replacement;
        if (Operator.NOT_EQUALS.equals(node.getOperator())) {
            final PrefixExpression not = (PrefixExpression) ast.createInstance(PrefixExpression.class);
            not.setOperator(org.eclipse.jdt.core.dom.PrefixExpression.Operator.NOT);
            not.setOperand(invocation);
            replacement = not;
        } else {
            replacement = invocation;
        }
        
        ASTUtil.replace(node, replacement);
        return true;
    }
    
}
