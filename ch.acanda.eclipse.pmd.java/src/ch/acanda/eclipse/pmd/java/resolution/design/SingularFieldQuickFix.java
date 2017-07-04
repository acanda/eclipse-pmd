// =====================================================================
//
// Copyright (C) 2012 - 2017, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.design;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.ASTUtil;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

import com.google.common.base.Optional;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/design.html#SingularField">SingularField</a>.
 * It replaces the field with a local variable.
 * 
 * @author Philip Graf
 */
public final class SingularFieldQuickFix extends ASTQuickFix<VariableDeclarationFragment> {
    
    public SingularFieldQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        return "Replace with a local variable";
    }
    
    @Override
    public String getDescription() {
        return "Replaces the field with a local variable.";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, VariableDeclarationFragment> getNodeFinder(final Position position) {
        return Finders.nodeWithinPosition(getNodeType(), position);
    }
    
    /**
     * Replaces the field declaration with a local variable declaration.
     */
    @Override
    protected boolean apply(final VariableDeclarationFragment node) {
        final String name = node.getName().getIdentifier();
        final AssignmentNodeFinder finder = new AssignmentNodeFinder(name);
        final Optional<Assignment> assignment = finder.findNode(node.getParent().getParent());
        if (assignment.isPresent()) {
            replaceAssignment(node, assignment.get(), !finder.hasMoreThanOneAssignment());
            updateFieldDeclaration(node);
        }
        return assignment.isPresent();
    }
    
    /**
     * Replaces the assignment with a variable declaration. If the assignment is the only one in the block for this
     * variable, the final modifier is added to the declaration.
     */
    @SuppressWarnings("unchecked")
    private void replaceAssignment(final VariableDeclarationFragment node, final Assignment assignment, final boolean finalDeclaration) {
        final FieldDeclaration fieldDeclaration = (FieldDeclaration) node.getParent();
        final VariableDeclarationStatement declaration =
                (VariableDeclarationStatement) node.getAST().createInstance(VariableDeclarationStatement.class);
        declaration.setType(ASTUtil.copy(fieldDeclaration.getType()));
        final VariableDeclarationFragment fragment =
                (VariableDeclarationFragment) node.getAST().createInstance(VariableDeclarationFragment.class);
        fragment.setName(ASTUtil.copy(node.getName()));
        fragment.setInitializer(ASTUtil.copy(assignment.getRightHandSide()));
        declaration.fragments().add(fragment);
        if (finalDeclaration) {
            final Modifier modifier = (Modifier) node.getAST().createInstance(Modifier.class);
            modifier.setKeyword(ModifierKeyword.FINAL_KEYWORD);
            declaration.modifiers().add(modifier);
        }
        ASTUtil.replace(assignment.getParent(), declaration);
    }
    
    /**
     * Updates the field declaration. If the replaced field was the only fragment, the entire field declaration is
     * removed. Otherwise the field declaration stays and only the respective fragment is removed.
     */
    private void updateFieldDeclaration(final VariableDeclarationFragment node) {
        final FieldDeclaration fieldDeclaration = (FieldDeclaration) node.getParent();
        @SuppressWarnings("unchecked")
        final List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
        if (fragments.size() > 1) {
            for (final VariableDeclarationFragment fragment : fragments) {
                if (fragment.getName().getIdentifier().equals(node.getName().getIdentifier())) {
                    fragment.delete();
                }
            }
        } else {
            fieldDeclaration.delete();
        }
    }
    
    /**
     * Finds the assignment that is replaced with a local variable declaration.
     */
    private final class AssignmentNodeFinder extends ASTVisitor implements NodeFinder<ASTNode, Assignment> {
        
        private final String fieldName;
        /**
         * A shadowing block contains a local variable declaration with the same name as the field. Assignments within
         * such a block are thus not valid search results. When a shadowing variable is found, its containing block is
         * added to this list and removed when the block ends, i.e. as long as the collection is not empty, the
         * assignments are not valid search results.
         */
        private final Set<Block> shadowingBlocks = new HashSet<>();
        private Optional<Assignment> searchResult = Optional.absent();
        private boolean moreThanOneAssignment;
        
        AssignmentNodeFinder(final String fieldName) {
            super(false);
            this.fieldName = fieldName;
        }
        
        @Override
        public Optional<Assignment> findNode(final ASTNode node) {
            node.accept(this);
            return searchResult;
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public boolean visit(final VariableDeclarationStatement node) {
            final List<VariableDeclarationFragment> fragments = node.fragments();
            for (final VariableDeclarationFragment fragment : fragments) {
                if (fieldName.equals(fragment.getName().getIdentifier())) {
                    // we found a variable that shadows the field
                    shadowingBlocks.add((Block) node.getParent());
                }
            }
            return false;
        }
        
        @Override
        public void endVisit(final Block node) {
            shadowingBlocks.remove(node);
        }
        
        @Override
        public boolean visit(final Assignment assignment) {
            if (shadowingBlocks.isEmpty()) {
                final Expression lhs = assignment.getLeftHandSide();
                if (lhs instanceof SimpleName) {
                    checkName(assignment, (SimpleName) lhs);
                } else if (lhs instanceof FieldAccess && ((FieldAccess) lhs).getExpression() instanceof ThisExpression) {
                    checkName(assignment, ((FieldAccess) lhs).getName());
                }
            }
            return false;
        }
        
        private void checkName(final Assignment assignment, final SimpleName variableName) {
            if (fieldName.equals(variableName.getIdentifier())) {
                if (!searchResult.isPresent()) {
                    searchResult = Optional.of(assignment);
                } else {
                    moreThanOneAssignment = true;
                }
            }
        }
        
        public boolean hasMoreThanOneAssignment() {
            return moreThanOneAssignment;
        }
        
    }
}
