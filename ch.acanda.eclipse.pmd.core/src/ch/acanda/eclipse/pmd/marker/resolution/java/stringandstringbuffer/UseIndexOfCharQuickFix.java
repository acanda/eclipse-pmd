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

package ch.acanda.eclipse.pmd.marker.resolution.java.stringandstringbuffer;

import static ch.acanda.eclipse.pmd.marker.resolution.ASTUtil.replace;

import java.util.List;

import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
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
 * href="http://pmd.sourceforge.net/rules/java/strings.html#UseIndexOfChar">UseIndexOfChar</a>. It replaces
 * <code>s.indexOf("a")</code> with <code>s.indexOf('a')</code>.
 * 
 * @author Philip Graf
 */
public class UseIndexOfCharQuickFix extends ASTQuickFix<MethodInvocation> {
    
    public UseIndexOfCharQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        return "Replace with String.indexOf(char)";
    }
    
    @Override
    public String getDescription() {
        return "Replaces String.indexOf(String) with String.indexOf(char).";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, MethodInvocation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Replaces the string literal <code>"a"</code> in <code>s.indexOf("a")</code> with the character literal
     * <code>'a'</code>.
     */
    @Override
    protected boolean apply(final MethodInvocation node) {
        @SuppressWarnings("unchecked")
        final List<Expression> arguments = node.arguments();
        if (arguments.size() == 1 && arguments.get(0) instanceof StringLiteral) {
            final CharacterLiteral character = node.getAST().newCharacterLiteral();
            final StringLiteral string = (StringLiteral) arguments.get(0);
            character.setEscapedValue(toCharValue(string.getEscapedValue()));
            replace(string, character);
            return true;
        }
        return false;
    }
    
    private static String toCharValue(final String stringValue) {
        return stringValue.replace('"', '\'');
    }
    
}
