package ch.acanda.eclipse.pmd.marker.resolution;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.Position;

/**
 * Searches an AST for a node that has the provided type and includes the provided position. If more than one node
 * fit the criteria, the one with the largest distance to the root is returned.
 */
class PositionWithinNodeNodeFinder extends ASTVisitor implements NodeFinder {
    
    private final int start;
    private final int end;
    private final Class<? extends ASTNode> nodeType;
    private ASTNode node;
    
    public PositionWithinNodeNodeFinder(final Position position, final Class<? extends ASTNode> nodeType) {
        start = position.getOffset();
        end = start + position.getLength();
        this.nodeType = nodeType;
    }
    
    @Override
    public boolean preVisit2(final ASTNode node) {
        final int nodeStart = node.getStartPosition();
        final int nodeEnd = nodeStart + node.getLength();
        if (nodeStart <= start && end <= nodeEnd) {
            if (nodeType.isAssignableFrom(node.getClass())) {
                this.node = node;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public ASTNode findNode(final CompilationUnit ast) {
        node = null;
        ast.accept(this);
        return node;
    }
    
}