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
import org.eclipse.jface.text.Position;

/**
 * Static utility methods pertaining to {@link NodeFinder} instances.
 * 
 * @author Philip Graf
 */
public final class Finders {
    
    private Finders() {
        // hide constructor of utility class
    }
    
    /**
     * Creates a {@link NodeFinder} that finds a node depending on its type and a position that encloses the node.
     * 
     * @param nodeType The type of the node.
     * @param position The position that encloses the node completely.
     */
    public static NodeFinder nodeWithinPosition(final Class<? extends ASTNode> nodeType, final Position position) {
        return new NodeWithinPositionNodeFinder(position, nodeType);
    }
    
    /**
     * Creates a {@link NodeFinder} that finds a node depending on its type and a position that lies within the node.
     * 
     * @param nodeType The type of the node.
     * @param position The position that lies within the node completely.
     */
    public static NodeFinder positionWithinNode(final Position position, final Class<? extends ASTNode> nodeType) {
        return new PositionWithinNodeNodeFinder(position, nodeType);
    }
    
}
