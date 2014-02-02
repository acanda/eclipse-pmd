// =====================================================================
//
// Copyright (C) 2012 - 2014, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.stringandstringbuffer;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.replace;

import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/java/strings.html#AppendCharacterWithChar">AppendCharacterWithChar</a>. It
 * replaces <code>buffer.append("a")</code> with <code>buffer.append('a')</code>.
 * 
 * @author Philip Graf
 */
public class AppendCharacterWithCharQuickFix extends ASTQuickFix<StringLiteral> {
    
    public AppendCharacterWithCharQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        return "Replace with " + toCharValue(marker.getMarkerText());
    }
    
    @Override
    public String getDescription() {
        final String str = marker.getMarkerText();
        return "Replaces the string " + str + " with the character " + toCharValue(str) + '.';
    }
    
    @Override
    protected NodeFinder<CompilationUnit, StringLiteral> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Replaces the string literal <code>"a"</code> in <code>buffer.append("a")</code> with the character literal
     * <code>'a'</code>.
     */
    @Override
    protected boolean apply(final StringLiteral node) {
        final CharacterLiteral character = node.getAST().newCharacterLiteral();
        character.setEscapedValue(toCharValue(node.getEscapedValue()));
        replace(node, character);
        return true;
    }
    
    private static String toCharValue(final String stringValue) {
        return stringValue.replace('"', '\'');
    }
    
}
