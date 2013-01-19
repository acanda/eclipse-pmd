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
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Implementations of this interface are used to find a node of an AST which matches implementation specific criteria.
 * 
 * @author Philip Graf
 */
public interface NodeFinder {
    
    /**
     * @return A node from the provided AST or {@null} if a matching node could not be found.
     */
    ASTNode findNode(final CompilationUnit ast);
    
}
