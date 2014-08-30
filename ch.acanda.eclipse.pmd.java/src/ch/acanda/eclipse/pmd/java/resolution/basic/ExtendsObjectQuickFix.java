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

package ch.acanda.eclipse.pmd.java.resolution.basic;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/basic.html#ExtendsObject">ExtendsObject</a>. It
 * simply removes the superclass from the type definition.
 * 
 * @author Philip Graf
 */
public class ExtendsObjectQuickFix extends ASTQuickFix<TypeDeclaration> {
    
    public ExtendsObjectQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }
    
    @Override
    public String getLabel() {
        return "Remove 'extends Object'";
    }
    
    @Override
    public String getDescription() {
        return "Removes <b>extends Object</b> from the type declaration of " + marker.getViolationClassName() + ".";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, TypeDeclaration> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Removes {@code extends Object} from the type declaration.
     */
    @Override
    protected boolean apply(final TypeDeclaration node) {
        node.setSuperclassType(null);
        return true;
    }
    
}
