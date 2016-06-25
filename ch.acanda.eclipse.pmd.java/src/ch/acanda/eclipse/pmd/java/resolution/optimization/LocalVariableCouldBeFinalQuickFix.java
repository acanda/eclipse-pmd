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

package ch.acanda.eclipse.pmd.java.resolution.optimization;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/optimization.html#LocalVariableCouldBeFinal">
 * LocalVariableCouldBeFinalQuickFix</a>. It adds the final modifier to the variable declaration.
 * 
 * @author Philip Graf
 */
public class LocalVariableCouldBeFinalQuickFix extends ASTQuickFix<VariableDeclarationStatement> {
    
    public LocalVariableCouldBeFinalQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_ADD;
    }
    
    @Override
    public String getLabel() {
        return "Add 'final' modifier";
    }
    
    @Override
    public String getDescription() {
        return "Adds the <b>final</b> modifier.";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, VariableDeclarationStatement> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Adds the final modifier to the variable declaration.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean apply(final VariableDeclarationStatement node) {
        final Modifier modifier = (Modifier) node.getAST().createInstance(Modifier.class);
        modifier.setKeyword(ModifierKeyword.FINAL_KEYWORD);
        node.modifiers().add(modifier);
        return true;
    }
    
}
