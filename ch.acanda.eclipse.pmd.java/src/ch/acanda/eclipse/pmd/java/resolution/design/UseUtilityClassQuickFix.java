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

package ch.acanda.eclipse.pmd.java.resolution.design;

import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.java.resolution.ASTUtil;
import ch.acanda.eclipse.pmd.java.resolution.Finders;
import ch.acanda.eclipse.pmd.java.resolution.NodeFinder;
import ch.acanda.eclipse.pmd.java.resolution.ASTRewriteQuickFix;
import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Quick fix for the rule <a
 * href="http://pmd.sourceforge.net/rules/java/design.html#UseUtilityClass">UseUtilityClass</a>. It makes the class
 * final and adds a private constructor.
 *
 * @author Philip Graf
 */
public final class UseUtilityClassQuickFix extends ASTRewriteQuickFix<TypeDeclaration> {

    public UseUtilityClassQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_CHANGE;
    }

    @Override
    public String getLabel() {
        return "Convert to utility class";
    }

    @Override
    public String getDescription() {
        return "Makes the class final and adds a private constructor.";
    }

    @Override
    protected NodeFinder<CompilationUnit, TypeDeclaration> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, getNodeType());
    }

    /**
     * Makes the class final and adds a private constructor.
     */
    @Override
    protected boolean rewrite(final TypeDeclaration typeDeclaration, final ASTRewrite rewrite) throws JavaModelException {
        addFinalIfNecessary(typeDeclaration, rewrite);
        addPrivateConstructor(typeDeclaration, rewrite);
        return true;
    }

    private void addFinalIfNecessary(final TypeDeclaration typeDeclaration, final ASTRewrite rewrite) {
        @SuppressWarnings("unchecked")
        final List<IExtendedModifier> modifiers = typeDeclaration.modifiers();
        if (!Iterables.any(modifiers, isFinal())) {
            final ListRewrite modifierRewrite = rewrite.getListRewrite(typeDeclaration, TypeDeclaration.MODIFIERS2_PROPERTY);
            final Modifier modifier = (Modifier) typeDeclaration.getAST().createInstance(Modifier.class);
            modifier.setKeyword(ModifierKeyword.FINAL_KEYWORD);
            modifierRewrite.insertLast(modifier, null);
        }
    }

    @SuppressWarnings("unchecked")
    private void addPrivateConstructor(final TypeDeclaration typeDeclaration, final ASTRewrite rewrite) {
        final AST ast = typeDeclaration.getAST();
        final MethodDeclaration constructor = (MethodDeclaration) ast.createInstance(MethodDeclaration.class);
        constructor.setConstructor(true);

        final Modifier modifier = (Modifier) ast.createInstance(Modifier.class);
        modifier.setKeyword(ModifierKeyword.PRIVATE_KEYWORD);
        constructor.modifiers().add(modifier);

        constructor.setName(ASTUtil.copy(typeDeclaration.getName()));

        final Block body = (Block) ast.createInstance(Block.class);
        constructor.setBody(body);

        final ListRewrite statementRewrite = rewrite.getListRewrite(body, Block.STATEMENTS_PROPERTY);
        final ASTNode comment = rewrite.createStringPlaceholder("// hide constructor of utility class", ASTNode.EMPTY_STATEMENT);
        statementRewrite.insertFirst(comment, null);

        final int position = findConstructorPosition(typeDeclaration);
        final ListRewrite bodyDeclarationRewrite = rewrite.getListRewrite(typeDeclaration, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
        bodyDeclarationRewrite.insertAt(constructor, position, null);
    }

    private Predicate<? super IExtendedModifier> isFinal() {
        return new Predicate<IExtendedModifier>() {
            @Override
            public boolean apply(final IExtendedModifier modifier) {
                return modifier.isModifier() && ((Modifier) modifier).isFinal();
            }
        };
    }

    /**
     * The new private constructor should be inserted before the first method declaration.
     */
    private int findConstructorPosition(final TypeDeclaration typeDeclaration) {
        @SuppressWarnings("unchecked")
        final List<BodyDeclaration> bodyDeclarations = typeDeclaration.bodyDeclarations();
        for (int i = 0; i < bodyDeclarations.size(); i++) {
            if (bodyDeclarations.get(i) instanceof MethodDeclaration) {
                return i;
            }
        }
        return bodyDeclarations.size();
    }

}
