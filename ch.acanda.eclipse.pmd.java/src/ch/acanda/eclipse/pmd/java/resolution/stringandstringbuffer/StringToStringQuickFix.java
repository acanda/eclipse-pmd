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

package ch.acanda.eclipse.pmd.java.resolution.stringandstringbuffer;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.replace;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/java/strings.html#StringToString">StringToString</a>. It removes the
 * <code>.toString()</code> from <code>"foo".toString()</code> if the expression is only a part of an statement. Removes
 * the expression completely if it is the whole statement.
 * 
 * @author Philip Graf
 */
public class StringToStringQuickFix extends ASTQuickFix<MethodInvocation> {
    
    public StringToStringQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }
    
    @Override
    public String getLabel() {
        return "Remove .toString()";
    }
    
    @Override
    public String getDescription() {
        return "Removes .toString() from " + marker.getMarkerText() + "().";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, MethodInvocation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Removes the <code>.toString()</code> from <code>"foo".toString()</code> if the expression is only a part of an
     * statement. Removes the expression completely if it is the whole statement.
     */
    @Override
    protected boolean apply(final MethodInvocation node) {
        if (node.getParent() instanceof ExpressionStatement) {
            // remove "foo".toString() completely if it is a statement as "foo" alone is not a valid statement
            node.getParent().delete();
        } else {
            // remove .toString() if "foo".toString() is only part of a statement
            // e.g. return "foo".toString(); -> return "foo";
            replace(node, copy(node.getExpression()));
        }
        return true;
    }
    
}
