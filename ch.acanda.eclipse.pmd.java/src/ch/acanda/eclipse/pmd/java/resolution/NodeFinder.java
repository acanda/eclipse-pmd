// =====================================================================
//
// Copyright (C) 2012 - 2019, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.base.Optional;

/**
 * Implementations of this interface are used to find a node of an AST which matches implementation specific criteria.
 * 
 * @author Philip Graf
 * 
 * @param R The type of the root node from which the AST will be traversed.
 * @param N The type of the node for which this finder is looking.
 */
public interface NodeFinder<R extends ASTNode, N extends ASTNode> {
    
    /**
     * @return A node from the provided AST if a matching node could be found.
     */
    Optional<N> findNode(final R ast);
    
}
