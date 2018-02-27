// =====================================================================
//
// Copyright (C) 2012 - 2018, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.performance;

import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.copy;
import static ch.acanda.eclipse.pmd.java.resolution.ASTUtil.replace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for the rule <a href=http://pmd.sourceforge.net/rules/java/migrating.html#LongInstantiation"
 * >LongInstantiation</a>. It replaces a Long instantiation with a call to {@code Long.valueOf(Long)}.
 *
 * @author Philip Graf
 */
public class LongInstantiationValueOfQuickFix extends ASTQuickFix<ClassInstanceCreation> {

    private static final Pattern ARGUMENT = Pattern.compile("\\((.*)\\)");

    public LongInstantiationValueOfQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        return "Use Long.valueOf(long)";
    }

    @Override
    public String getDescription() {
        final Matcher matcher = ARGUMENT.matcher(marker.getMarkerText());
        if (matcher.find()) {
            return "Replaces the Long instantiation with <b>Long.valueOf(" + matcher.group(1) + ")</b>.";
        }
        return "Uses Long.valueOf(long) instead of an explicit Long instantiation";
    }

    @Override
    protected NodeFinder<CompilationUnit, ClassInstanceCreation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    /**
     * Replaces the Long instantiation with its argument, e.g. {@code new Long(123 + x)} with
     * {@code Long.valueOf(123 + x)}.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected boolean apply(final ClassInstanceCreation node) {
        final AST ast = node.getAST();
        final MethodInvocation invocation = ast.newMethodInvocation();
        invocation.setExpression(ast.newSimpleName("Long"));
        invocation.setName(ast.newSimpleName("valueOf"));
        invocation.arguments().add(copy((Expression) node.arguments().get(0)));
        replace(node, invocation);
        return true;
    }

}
