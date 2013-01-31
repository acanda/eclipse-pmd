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

import static ch.acanda.eclipse.pmd.marker.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.marker.resolution.ASTUtil.replace;

import java.util.List;

import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.marker.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.marker.resolution.Finders;
import ch.acanda.eclipse.pmd.marker.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/empty.html#EmptyFinallyBlock"
 * >EmptyFinallyBlock</a>. It removes the empty finally block.
 * 
 * @author Philip Graf
 */
public class EmptyFinallyBlockQuickFix extends ASTQuickFix<TryStatement> {
    
    public EmptyFinallyBlockQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }
    
    @Override
    public String getLabel() {
        return "Remove the finally block";
    }
    
    @Override
    public String getDescription() {
        return "Removes the empty finally block.";
    }
    
    @Override
    protected NodeFinder getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Removes the finally block. Additionally removes the try if there are no catch blocks while keeping the try block
     * statements.
     */
    @Override
    protected boolean apply(final TryStatement node) {
        boolean success = true;
        if (node.catchClauses().isEmpty()) {
            @SuppressWarnings("unchecked")
            final List<Statement> statements = node.getBody().statements();
            if (replace(node, copy(statements))) {
                node.delete();
            } else {
                success = false;
            }
        } else {
            node.setFinally(null);
        }
        return success;
    }
    
}
