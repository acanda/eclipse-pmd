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

package ch.acanda.eclipse.pmd.java.resolution;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import ch.acanda.eclipse.pmd.marker.PMDMarker;

/**
 * Base class for Java quick fix that modifies the AST.
 *
 * @author Philip Graf
 *
 * @param <T> The type of AST node that will be passed to {@link #apply(ASTNode)}.
 */
public abstract class ASTQuickFix<T extends ASTNode> extends JavaQuickFix<T> {

    public ASTQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected void startFixingMarkers(final CompilationUnit ast) {
        ast.recordModifications();
    }

    @Override
    protected boolean fixMarker(final T node, final IDocument document, final Map<?, ?> options) {
        return apply(node);
    }

    @Override
    protected void finishFixingMarkers(final CompilationUnit ast, final IDocument document, final Map<?, ?> options)
            throws BadLocationException {
        ast.rewrite(document, options).apply(document);
    }

    /**
     * Applies the quick fix to the provided node. The marker's range lies within the node's range and the node's type
     * is the same as the one returned by {@link #getNodeType()}.
     *
     * @return {@code true} iff the quick fix was applied successfully, i.e. the PMD problem was resolved.
     */
    protected abstract boolean apply(final T node);

}
