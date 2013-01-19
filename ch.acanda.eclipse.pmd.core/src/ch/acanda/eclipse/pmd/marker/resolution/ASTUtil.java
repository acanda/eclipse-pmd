// =====================================================================
//
// Copyright (C) 2012, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.marker.resolution;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

/**
 * Utilitites for manipulationg ASTs.
 * 
 * @author Philip Graf
 */
public final class ASTUtil {
    
    private ASTUtil() {
        // hide constructor of utility class
    }

    /**
     * Returns a deep copy of the subtree of AST nodes rooted at the given node. The resulting nodes are owned by the
     * same AST as the given node. Even if the given node has a parent, the result node will be unparented.
     * <p>
     * Source range information on the original nodes is automatically copied to the new nodes. Client properties (
     * <code>properties</code>) are not carried over.
     * </p>
     * <p>
     * The node's <code>AST</code> and the target <code>AST</code> must support the same API level.
     * </p>
     * 
     * @param node The node to copy, or <code>null</code> if none.
     * 
     * @return The copied node, or <code>null</code> if <code>node</code> is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> T copy(final T node) {
        return (T) ASTNode.copySubtree(node.getAST(), node);
    }
    
    /**
     * Returns a deep copy of the subtrees of AST nodes rooted at the given list of nodes. The resulting nodes are owned
     * by the given AST, which may be different from the ASTs of the nodes in the list. Even if the nodes in the list
     * have parents, the nodes in the result will be unparented.
     * <p>
     * Source range information on the original nodes is automatically copied to the new nodes. Client properties (
     * <code>properties</code>) are not carried over.
     * </p>
     * 
     * @param nodes The node to copy, or <code>null</code> if none.
     * 
     * @return The copied nodes, or <code>null</code> if <code>node</code> is <code>null</code>
     */
    @SuppressWarnings("unchecked")
    public static <T extends ASTNode> List<T> copy(final List<T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return nodes;
        }
        return ASTNode.copySubtrees(nodes.get(0).getAST(), nodes);
    }
    
    /**
     * Replaces a node in an AST with another node. If the replacement is successful the original node is deleted.
     * 
     * @param node The node to replace.
     * @param replacement The replacement node.
     * @return <code>true</code> if the node was successfully replaced.
     */
    public static boolean replace(final ASTNode node, final ASTNode replacement) {
        final ASTNode parent = node.getParent();
        final StructuralPropertyDescriptor descriptor = node.getLocationInParent();
        if (descriptor != null) {
            if (descriptor.isChildProperty()) {
                parent.setStructuralProperty(descriptor, replacement);
                node.delete();
                return true;
            } else if (descriptor.isChildListProperty()) {
                @SuppressWarnings("unchecked")
                final List<ASTNode> children = (List<ASTNode>) parent.getStructuralProperty(descriptor);
                children.set(children.indexOf(node), replacement);
                node.delete();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Replaces a node in an AST with other nodes. If the replacement is successful the original node is deleted.
     * 
     * @param node The node to replace.
     * @param replacement The replacement nodes.
     * @return <code>true</code> if the node was successfully replaced.
     */
    public static boolean replace(final ASTNode node, final List<? extends ASTNode> replacement) {
        final ASTNode parent = node.getParent();
        final StructuralPropertyDescriptor descriptor = node.getLocationInParent();
        if (descriptor != null && descriptor.isChildListProperty()) {
            @SuppressWarnings("unchecked")
            final List<ASTNode> children = (List<ASTNode>) parent.getStructuralProperty(descriptor);
            children.addAll(children.indexOf(node), replacement);
            node.delete();
            return true;
        }
        return false;
    }
    
}
