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

package ch.acanda.eclipse.pmd.java.resolution.design;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/design.html#DefaultLabelNotLastInSwitchStmt">
 * DefaultLabelNotLastInSwitchStmt</a>. It moves the default case to the last position.
 * 
 * @author Philip Graf
 */
public class DefaultLabelNotLastInSwitchStmtQuickFix extends ASTQuickFix<SwitchStatement> {
    
    /**
     * The expression of the default label is {@code null}.
     */
    private final static Expression DEFAULT_LABEL = null;
    
    public DefaultLabelNotLastInSwitchStmtQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        return "Move 'default:' to the end";
    }
    
    @Override
    public String getDescription() {
        return "Moves the default label to the end of the switch cases.";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, SwitchStatement> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Moves the default case to the last position. The default case includes the default {@code SwitchCase} and all
     * following statements up to the next {@code SwitchCase}.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean apply(final SwitchStatement node) {
        final List<Statement> statements = node.statements();
        final List<Statement> defaultCaseStatements = new ArrayList<>(statements.size());
        boolean isDefaultCaseStatement = false;
        for (final Statement statement : statements) {
            if (statement instanceof SwitchCase) {
                if (((SwitchCase) statement).getExpression() == DEFAULT_LABEL) {
                    isDefaultCaseStatement = true;
                } else {
                    isDefaultCaseStatement = false;
                }
            }
            if (isDefaultCaseStatement) {
                defaultCaseStatements.add(statement);
            }
        }
        statements.removeAll(defaultCaseStatements);
        statements.addAll(defaultCaseStatements);
        return true;
    }
    
}
