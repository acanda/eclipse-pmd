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
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Position;

/**
 * Searches an AST for a node that has the provided type and lies within the provided position. If more than one node
 * fit the criteria, the one with the smallest distance to the root is returned.
 */
class NodeWithinPositionNodeFinder extends ASTVisitor implements NodeFinder {
    
    private final int start;
    private final int end;
    private final Class<? extends ASTNode> nodeType;
    private ASTNode node;
    
    public NodeWithinPositionNodeFinder(final Position position, final Class<? extends ASTNode> nodeType) {
        start = position.getOffset();
        end = start + position.getLength();
        this.nodeType = nodeType;
    }
    
    @Override
    public boolean preVisit2(final ASTNode node) {
        if (this.node != null) {
            return false;
        }
        final int nodeStart = node.getStartPosition();
        final int nodeEnd = nodeStart + node.getLength();
        if (start <= nodeStart && nodeEnd <= end) {
            if (nodeType.isAssignableFrom(node.getClass())) {
                this.node = node;
                return false;
            }
            return true;
        }
        return nodeStart <= start && end <= nodeEnd;
    }
    
    @Override
    public ASTNode findNode(final CompilationUnit ast) {
        node = null;
        ast.accept(this);
        return node;
    }
    
}
