// =====================================================================
//
// Copyright (C) 2012 - 2016, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution.design;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.CompilationUnit;
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
 * href="http://pmd.sourceforge.net/rules/java/design.html#UseNotifyAllInsteadOfNotify">UseNotifyAllInsteadOfNotify</a>.
 * It replaces {@code x.notify()} with {@code x.notifyAll()}.
 * 
 * @author Philip Graf
 */
public class UseNotifyAllInsteadOfNotifyQuickFix extends ASTQuickFix<MethodInvocation> {
    
    private static final Pattern METHOD_INVOCATION_EXPRESSION = Pattern.compile("^(.*)\\s*\\.\\s*notify\\s*\\(\\s*\\)$");
    private final String methodInvocationExpression;
    
    public UseNotifyAllInsteadOfNotifyQuickFix(final PMDMarker marker) {
        super(marker);
        final Matcher matcher = METHOD_INVOCATION_EXPRESSION.matcher(marker.getMarkerText());
        if (matcher.matches()) {
            methodInvocationExpression = matcher.group(1);
        } else {
            methodInvocationExpression = null;
        }
    }
    
    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }
    
    @Override
    public String getLabel() {
        if (methodInvocationExpression != null) {
            return "Replace with " + methodInvocationExpression + ".notifyAll()";
        }
        return "Replace notify() with notifyAll()";
    }
    
    @Override
    public String getDescription() {
        if (methodInvocationExpression != null) {
            return "Replaces <b>" + marker.getMarkerText() + "</b> with <b>" + methodInvocationExpression + ".notifyAll()</b>.";
        }
        return "Replaces notify() with notifyAll().";
    }
    
    @Override
    protected NodeFinder<CompilationUnit, MethodInvocation> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }
    
    /**
     * Replaces {@code x.notify()} with {@code x.notifyAll()}.
     */
    @Override
    protected boolean apply(final MethodInvocation node) {
        node.getName().setIdentifier("notifyAll");
        return true;
    }
    
}
