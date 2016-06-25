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

package ch.acanda.eclipse.pmd.java.resolution;

import java.util.Map;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MultiTextEdit;

import ch.acanda.eclipse.pmd.marker.PMDMarker;

/**
 * Base class for a Java quick fix that uses {@code ASTRewrite} to modify the AST.
 *
 * @author Philip Graf
 *
 * @param <T> The type of AST node that will be passed to {@link #rewrite(ASTNode, ASTRewrite)}.
 */
public abstract class ASTRewriteQuickFix<T extends ASTNode> extends JavaQuickFix<T> {

    private MultiTextEdit rootTextEdit;

    public ASTRewriteQuickFix(final PMDMarker marker) {
        super(marker);
    }


    @Override
    protected void startFixingMarkers(final CompilationUnit ast) {
        rootTextEdit = new MultiTextEdit();
    }

    @Override
    protected boolean fixMarker(final T node, final IDocument document, final Map<?, ?> options) throws JavaModelException {
        final ASTRewrite rewrite = ASTRewrite.create(node.getAST());
        final boolean isSuccessful = rewrite(node, rewrite);
        if (isSuccessful) {
            rootTextEdit.addChild(rewrite.rewriteAST(document, options));
        }
        return isSuccessful;
    }

    @Override
    protected void finishFixingMarkers(final CompilationUnit ast, final IDocument document, final Map<?, ?> options)
            throws BadLocationException {
        rootTextEdit.apply(document);
    }

    /**
     * Applies the quick fix to the provided node. The marker's range lies within the node's range and the node's type
     * is the same as the one returned by {@link #getNodeType()}.
     *
     * @param rewrite
     *
     * @return {@code true} iff the quick fix was applied successfully, i.e. the PMD problem was resolved.
     */
    abstract protected boolean rewrite(final T node, ASTRewrite rewrite) throws JavaModelException;

}
