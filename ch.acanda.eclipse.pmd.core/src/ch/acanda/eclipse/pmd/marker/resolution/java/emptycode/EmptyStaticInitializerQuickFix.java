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

package ch.acanda.eclipse.pmd.marker.resolution.java.emptycode;

import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.marker.resolution.Finders;
import ch.acanda.eclipse.pmd.marker.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/empty.html#EmptyStaticInitializer"
 * >EmptyStaticInitializer</a>. It removes the empty static initializer.
 * 
 * @author Philip Graf
 */
public class EmptyStaticInitializerQuickFix extends ASTQuickFix<Initializer> {
    
    public EmptyStaticInitializerQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }
    
    @Override
    public String getLabel() {
        return "Remove the static initializer";
    }
    
    @Override
    public String getDescription() {
        return "Removes the empty static initializer.";
    }
    
    @Override
    protected NodeFinder getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    @Override
    protected boolean apply(final Initializer node) {
        node.delete();
        return true;
    }
    
}
