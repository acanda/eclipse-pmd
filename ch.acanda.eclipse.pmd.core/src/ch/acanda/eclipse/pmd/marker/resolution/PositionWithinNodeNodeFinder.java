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

package ch.acanda.eclipse.pmd.marker.resolution;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jface.text.Position;

/**
 * Searches an AST for a node that has the provided type and includes the provided position. If more than one node fit
 * the criteria, the one with the largest distance to the root is returned.
 */
class PositionWithinNodeNodeFinder extends ASTVisitor implements NodeFinder {
    
    private final int start;
    private final int end;
    private final Class<?>[] nodeTypes;
    private ASTNode node;
    
    public PositionWithinNodeNodeFinder(final Position position, final Class<?>... nodeTypes) {
        start = position.getOffset();
        end = start + position.getLength();
        this.nodeTypes = nodeTypes;
    }
    
    @Override
    public boolean preVisit2(final ASTNode node) {
        final int nodeStart = node.getStartPosition();
        final int nodeEnd = nodeStart + node.getLength();
        if (nodeStart <= start && end <= nodeEnd) {
            for (final Class<?> nodeType : nodeTypes) {
                if (nodeType.isAssignableFrom(node.getClass())) {
                    this.node = node;
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public ASTNode findNode(final ASTNode ast) {
        node = null;
        ast.accept(this);
        return node;
    }
    
}
