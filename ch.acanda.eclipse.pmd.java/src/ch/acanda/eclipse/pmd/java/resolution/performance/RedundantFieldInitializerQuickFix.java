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

package ch.acanda.eclipse.pmd.java.resolution.performance;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/java/optimizations.html#RedundantFieldInitializer">
 * RedundantFieldInitializer</a>. It removes the redundant field initializer.
 * 
 * @author Philip Graf
 */
public class RedundantFieldInitializerQuickFix extends ASTQuickFix<VariableDeclarationFragment> {
    
    public RedundantFieldInitializerQuickFix(final PMDMarker marker) {
        super(marker);
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_REMOVE;
    }
    
    @Override
    public String getLabel() {
        return "Remove redundant field initializer";
    }
    
    @Override
    public String getDescription() {
        final String description;
        final String name = extractName(marker.getMarkerText());
        if (name.length() > 0) {
            description = "Removes the redundant initializer of field " + name + ".";
        } else {
            description = "Removes the redundant field initializer.";
        }
        return description;
    }
    
    /**
     * Extracts the field name from the marker text. The marker text has the following format:
     * <code><i>fieldName</i> [ '[]' ] '=' <i>defaultValue<i></code>
     */
    private String extractName(final String markerText) {
        final StringBuilder name = new StringBuilder();
        if (markerText != null) {
            for (int i = 0; i < markerText.length(); i = markerText.offsetByCodePoints(i, 1)) {
                final int codePoint = markerText.codePointAt(i);
                if (!Character.isJavaIdentifierPart(codePoint)) {
                    break;
                }
                name.appendCodePoint(markerText.codePointAt(i));
            }
        }
        return name.toString();
    }
    
    @Override
    protected NodeFinder<CompilationUnit, VariableDeclarationFragment> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Removes the redundant field initializer.
     */
    @Override
    protected boolean apply(final VariableDeclarationFragment node) {
        node.setInitializer(null);
        return true;
    }
    
}
