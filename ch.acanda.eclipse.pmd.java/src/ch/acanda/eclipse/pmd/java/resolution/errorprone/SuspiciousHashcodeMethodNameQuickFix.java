// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.errorprone;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule
 * <a href="http://pmd.sourceforge.net/pmd-5.0.0/rules/java/naming.html#SuspiciousHashcodeMethodName"
 * >SuspiciousHashcodeMethodName</a>. It renames the method to {@code hashCode}.
 *
 * @author Philip Graf
 */
public class SuspiciousHashcodeMethodNameQuickFix extends ASTQuickFix<MethodDeclaration> {

    public SuspiciousHashcodeMethodNameQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        return "Rename method to 'hashCode'";
    }

    @Override
    public String getDescription() {
        return "Renames the method <b>hashcode()</b> to <b>hashCode()</b>.";
    }

    @Override
    protected NodeFinder<CompilationUnit, MethodDeclaration> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    /**
     * Sets the name of the method to "hashCode".
     */
    @Override
    protected boolean apply(final MethodDeclaration node) {
        node.getName().setIdentifier("hashCode");
        return true;
    }

}
