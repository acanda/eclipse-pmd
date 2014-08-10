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

package ch.acanda.eclipse.pmd.java.resolution.migration;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.replace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href=http://pmd.sourceforge.net/rules/java/migrating.html#ShortInstantiation"
 * >ShortInstantiation</a>. It replaces an Short instantiation with autoboxing.
 *
 * @author Philip Graf
 */
public class ShortInstantiationAutoboxingQuickFix extends ASTQuickFix<ClassInstanceCreation> {

    private final Pattern ARGUMENT = Pattern.compile("\\((.*)\\)");

    public ShortInstantiationAutoboxingQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        return "Use autoboxing";
    }

    @Override
    public String getDescription() {
        final Matcher matcher = ARGUMENT.matcher(marker.getMarkerText());
        if (matcher.find()) {
            return "Replaces the Short instantiation with <b>" + matcher.group(1) + "</b>.";
        }
        return "Uses autoboxing instead of an explicit Short instantiation";
    }

    @Override
    protected NodeFinder<CompilationUnit, ClassInstanceCreation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    /**
     * Replaces the Short instantiation with its argument, e.g. {@code new Short(123 + x)} with {@code 123 + x}.
     */
    @Override
    protected boolean apply(final ClassInstanceCreation node) {
        final Expression argument = (Expression) node.arguments().get(0);
        replace(node, copy(argument));
        return true;
    }

}
