// =====================================================================
//
// Copyright (C) 2012 - 2019, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.bestpractices;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.copy;
import static java.text.MessageFormat.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href="http://pmd.sourceforge.net/rules/java/design.html#UseVarargs">UseVarargs</a>. It
 * replaces {@code void foo(String[] args)} with {@code void foo(String... args)}.
 *
 * @author Philip Graf
 */
public class UseVarargsQuickFix extends ASTQuickFix<SingleVariableDeclaration> {

    private static final Pattern ARRAY_ARGUMENT = Pattern.compile("^(.*)\\[\\s*\\]\\s+([^\\s]+)$");
    private final String description;

    public UseVarargsQuickFix(final PMDMarker marker) {
        super(marker);
        final String text = marker.getMarkerText();
        final Matcher matcher = ARRAY_ARGUMENT.matcher(text);
        if (matcher.matches()) {
            final String type = matcher.group(1).replaceAll("\\s+", "");
            final String arrayDeclaration = format("{0}[] {1}", type, matcher.group(2));
            final String varargsDeclaration = format("{0}... {1}", type, matcher.group(2));
            description = format("Replaces <b>{0}</b> with <b>{1}</b>.", arrayDeclaration, varargsDeclaration);
        } else {
            description = "Replaces the array declaration with a varargs declaration.";
        }
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        return "Replace with varargs";
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    protected NodeFinder<CompilationUnit, SingleVariableDeclaration> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    @Override
    protected boolean apply(final SingleVariableDeclaration node) {
        node.setType(copy(((ArrayType) node.getType()).getComponentType()));
        node.setVarargs(true);
        return true;
        // final SingleVariableDeclaration varargsDeclaration = copy(node);
        // varargsDeclaration.setVarargs(true);
        // final Type type = copy(((ArrayType) node.getType()).getComponentType());
        // varargsDeclaration.setType(type);
        // return replace(node, varargsDeclaration);
    }

}
