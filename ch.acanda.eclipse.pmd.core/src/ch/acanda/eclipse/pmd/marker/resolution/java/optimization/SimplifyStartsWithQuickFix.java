
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

package ch.acanda.eclipse.pmd.marker.resolution.java.optimization;

import static ch.acanda.eclipse.pmd.marker.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.marker.resolution.ASTUtil.replace;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.marker.resolution.Finders;
import ch.acanda.eclipse.pmd.marker.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/java/optimizations.html#SimplifyStartsWith"> SimplifyStartsWith</a>.
 * It rewrites <code>x.startsWith("a")</code> as <code>x.charAt(0) == 'a'</code>.
 * 
 * @author Philip Graf
 */
public class SimplifyStartsWithQuickFix extends ASTQuickFix<MethodInvocation> {
    
    public SimplifyStartsWithQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        return "Rewrite expression";
    }
    
    @Override
    public String getDescription() {
        return "Rewrite the call to String.startsWith() using String.charAt(0).";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, MethodInvocation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Rewrites <code>s.startsWith("a")</code> as <code>s.charAt(0) == 'a'</code>.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean apply(final MethodInvocation node) {
        final AST ast = node.getAST();
        
        final MethodInvocation charAt = ast.newMethodInvocation();
        charAt.setExpression(copy(node.getExpression()));
        charAt.setName(ast.newSimpleName("charAt"));
        charAt.arguments().add(ast.newNumberLiteral("0"));
        
        final CharacterLiteral character = ast.newCharacterLiteral();
        final StringLiteral s = (StringLiteral) node.arguments().get(0);
        character.setEscapedValue(s.getEscapedValue().replace('"', '\''));
        
        final InfixExpression eq = ast.newInfixExpression();
        eq.setOperator(Operator.EQUALS);
        eq.setLeftOperand(charAt);
        eq.setRightOperand(character);
        
        replace(node, eq);
        return true;
    }
    
}
