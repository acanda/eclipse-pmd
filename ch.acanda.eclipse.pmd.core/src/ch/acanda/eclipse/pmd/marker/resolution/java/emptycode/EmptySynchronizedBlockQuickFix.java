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

package ch.acanda.eclipse.pmd.marker.resolution.java.emptycode;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.marker.resolution.Finders;
import ch.acanda.eclipse.pmd.marker.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/empty.html#EmptySynchronizedBlock"
 * >EmptySynchronizedBlock</a>. It removes the empty synchronized block.
 * 
 * @author Philip Graf
 */
public class EmptySynchronizedBlockQuickFix extends ASTQuickFix<SynchronizedStatement> {
    
    public EmptySynchronizedBlockQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }
    
    @Override
    public String getLabel() {
        return "Remove the synchronized block";
    }
    
    @Override
    public String getDescription() {
        return "Removes the empty synchronized block.";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, SynchronizedStatement> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    @Override
    protected boolean apply(final SynchronizedStatement node) {
        node.delete();
        return true;
    }
    
}
