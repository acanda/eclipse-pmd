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

package ch.acanda.eclipse.pmd.java.resolution;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jface.text.Position;

import com.google.common.base.Optional;

/**
 * Searches an AST for a node that has the provided type and includes the provided position. If more than one node fit
 * the criteria, the one with the largest distance to the root is returned.
 */
class PositionWithinNodeNodeFinder<R extends ASTNode, N extends ASTNode> extends ASTVisitor implements NodeFinder<R, N> {
    
    private final int start;
    private final int end;
    private final Class<? extends N>[] nodeTypes;
    private N node;
    
    @SafeVarargs
    public PositionWithinNodeNodeFinder(final Position position, final Class<? extends N>... nodeTypes) {
        start = position.getOffset();
        end = start + position.getLength();
        this.nodeTypes = nodeTypes;
    }
    
    @Override
    public boolean preVisit2(final ASTNode node) {
        final int nodeStart = node.getStartPosition();
        final int nodeEnd = nodeStart + node.getLength();
        if (nodeStart <= start && end <= nodeEnd) {
            for (final Class<? extends N> nodeType : nodeTypes) {
                if (nodeType.isAssignableFrom(node.getClass())) {
                    this.node = nodeType.cast(node);
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public Optional<N> findNode(final R ast) {
        node = null;
        ast.accept(this);
        return Optional.fromNullable(node);
    }
    
}
