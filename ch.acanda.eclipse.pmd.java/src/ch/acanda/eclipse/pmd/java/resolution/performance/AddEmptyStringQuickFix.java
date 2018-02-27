// =====================================================================
//
// Copyright (C) 2012 - 2018, Philip Graf
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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule
 * <a href="http://pmd.sourceforge.net/rules/java/optimizations.html#AddEmptyString">AddEmptyString</a>. It replaces the
 * empty string in additive expressions like <code>"" + 123</code> with <code>String.valueOf(123)</code>.
 *
 * @author Philip Graf
 */
public class AddEmptyStringQuickFix extends ASTQuickFix<InfixExpression> {

    public AddEmptyStringQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }

    @Override
    public String getLabel() {
        return "Remove empty string";
    }

    @Override
    public String getDescription() {
        return "Removes the empty string.";
    }

    @Override
    protected NodeFinder<CompilationUnit, InfixExpression> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    @Override
    protected boolean needsTypeResolution() {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean apply(final InfixExpression node) {
        final Expression rightOperand = node.getRightOperand();

        // "" + "abc" -> "abc"
        // "" + x.toString() -> x.toString()
        if (isString(rightOperand)) {
            return replace(node, copy(rightOperand));
        }

        // "" + 'a' -> "a"
        if (isCharacterLiteral(rightOperand)) {
            final AST ast = node.getAST();
            final StringLiteral stringLiteral = ast.newStringLiteral();
            final String escapedCharacter = ((CharacterLiteral) rightOperand).getEscapedValue();
            stringLiteral.setEscapedValue(convertToEscapedString(escapedCharacter));
            return replace(node, stringLiteral);
        }

        // "" + x -> String.valueOf(x)
        final AST ast = node.getAST();
        final MethodInvocation toString = ast.newMethodInvocation();
        toString.setExpression(ast.newSimpleName("String"));
        toString.setName(ast.newSimpleName("valueOf"));
        toString.arguments().add(copy(rightOperand));
        return replace(node, toString);
    }

    private boolean isString(final Expression expression) {
        return isStringLiteral(expression) || isStringExpression(expression);
    }

    private boolean isStringLiteral(final Expression expression) {
        return expression.getNodeType() == ASTNode.STRING_LITERAL;
    }

    private boolean isStringExpression(final Expression expression) {
        final ITypeBinding typeBinding = expression.resolveTypeBinding();
        if (typeBinding != null) {
            return String.class.getName().equals(typeBinding.getQualifiedName());
        }
        return false;
    }

    private boolean isCharacterLiteral(final Expression expression) {
        return expression.getNodeType() == ASTNode.CHARACTER_LITERAL;
    }

    private String convertToEscapedString(final String escapedCharacter) {
        return '"' + escapedCharacter.substring(1, escapedCharacter.length() - 1) + '"';
    }

}
