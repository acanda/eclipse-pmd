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

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;

import ch.acanda.eclipse.pmd.marker.PMDMarker;
import ch.acanda.eclipse.pmd.ui.util.PMDPluginImages;

/**
 * Quick fix for all PMD rule violations in Java 5 and later. It adds a {@code @SuppressWarnings} annotation for the
 * respective rule to the enclosing class, method, field or parameter.
 *
 * @author Philip Graf
 */
@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.TooManyMethods" })
public final class SuppressWarningsQuickFix extends ASTQuickFix<ASTNode> {

    public SuppressWarningsQuickFix(final PMDMarker marker) {
        super(marker);
    }

    @Override
    protected ImageDescriptor getImageDescriptor() {
        return PMDPluginImages.QUICKFIX_ADD;
    }

    @Override
    public String getLabel() {
        return "Add @SuppressWarnings 'PMD." + marker.getRuleName() + "'";
    }

    @Override
    public String getDescription() {
        return "Adds @SuppressWarnings(\"PMD." + marker.getRuleName() + "\").";
    }

    @Override
    protected NodeFinder<CompilationUnit, ASTNode> getNodeFinder(final Position position) {
        return Finders.positionWithinNode(position, AbstractTypeDeclaration.class, AnnotationTypeMemberDeclaration.class,
                EnumConstantDeclaration.class, FieldDeclaration.class, MethodDeclaration.class, VariableDeclarationStatement.class,
                CompilationUnit.class);
    }

    @Override
    protected boolean apply(final ASTNode node) {
        final ASTNode annotatableNode = findAnnotatableASTNode(node);
        if (annotatableNode != null) {
            final AST ast = node.getAST();
            final List<IExtendedModifier> modifiers = getModifiers(annotatableNode);
            final Annotation existingAnnotation = findExistingSuppressWarningsAnnotation(modifiers);
            final Annotation annotation = createReplacementSuppressWarningsAnnotation(existingAnnotation, ast);
            if (existingAnnotation == null) {
                final int position = findPosition(modifiers);
                modifiers.add(position, annotation);
            } else {
                ASTUtil.replace(existingAnnotation, annotation);
            }
            return !annotation.equals(existingAnnotation);
        }
        return false;
    }

    private ASTNode findAnnotatableASTNode(final ASTNode node) {
        if (node instanceof CompilationUnit) {
            return findBodyDeclaration(node);
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    private List<IExtendedModifier> getModifiers(final ASTNode node) {
        if (node instanceof VariableDeclarationStatement) {
            return ((VariableDeclarationStatement) node).modifiers();
        }
        return ((BodyDeclaration) node).modifiers();
    }

    private BodyDeclaration findBodyDeclaration(final ASTNode node) {
        final BodyDeclaration[] bodyDeclaration = new BodyDeclaration[1];
        node.accept(new ASTVisitor() {

            @Override
            public boolean visit(final EnumDeclaration node) {
                bodyDeclaration[0] = node;
                return false;
            }

            @Override
            public boolean visit(final TypeDeclaration node) {
                bodyDeclaration[0] = node;
                return false;
            }

            @Override
            public boolean visit(final AnnotationTypeDeclaration node) {
                bodyDeclaration[0] = node;
                return false;
            }

        });
        return bodyDeclaration[0];
    }

    private Annotation findExistingSuppressWarningsAnnotation(final List<IExtendedModifier> modifiers) {
        for (final IExtendedModifier modifier : modifiers) {
            if (modifier.isAnnotation()) {
                final Annotation annotation = (Annotation) modifier;
                final Name typeName = annotation.getTypeName();
                if (typeName.isSimpleName() && "SuppressWarnings".equals(typeName.getFullyQualifiedName())
                        || typeName.isQualifiedName() && "java.lang.SuppressWarnings".equals(typeName.getFullyQualifiedName())) {
                    return annotation;
                }
            }
        }
        return null;
    }

    private Annotation createReplacementSuppressWarningsAnnotation(final Annotation existingAnnotation, final AST ast) {
        final Annotation replacement;

        if (existingAnnotation == null || existingAnnotation.isMarkerAnnotation()) {
            final SingleMemberAnnotation annotation = createAnnotation(ast, SingleMemberAnnotation.class);
            annotation.setValue(createPMDLiteralValue(ast));
            replacement = annotation;

        } else if (existingAnnotation.isSingleMemberAnnotation()) {
            final SingleMemberAnnotation existingSingleMemberAnnotation = (SingleMemberAnnotation) existingAnnotation;
            final SingleMemberAnnotation annotation = createAnnotation(ast, SingleMemberAnnotation.class);
            annotation.setValue(createArrayInitializer(existingSingleMemberAnnotation.getValue()));
            replacement = annotation;

        } else if (existingAnnotation.isNormalAnnotation()) {
            final NormalAnnotation existingNormalAnnotation = (NormalAnnotation) existingAnnotation;
            final NormalAnnotation annotation = createAnnotation(ast, NormalAnnotation.class);
            createAnnotationValues(existingNormalAnnotation, annotation);
            replacement = annotation;

        } else {
            replacement = existingAnnotation;
        }

        return replacement;
    }

    private <T extends Annotation> T createAnnotation(final AST ast, final Class<T> cls) {
        @SuppressWarnings("unchecked")
        final T annotation = (T) ast.createInstance(cls);
        final SimpleName name = (SimpleName) ast.createInstance(SimpleName.class);
        name.setIdentifier("SuppressWarnings");
        annotation.setTypeName(name);
        return annotation;
    }

    /**
     * Create the "PMD.<i>RuleName</i>" string literal for the {@code @SuppressWarnings} annotation.
     */
    private StringLiteral createPMDLiteralValue(final AST ast) {
        final StringLiteral newValue = (StringLiteral) ast.createInstance(StringLiteral.class);
        newValue.setLiteralValue("PMD." + marker.getRuleName());
        return newValue;
    }

    /**
     * Creates the member value pairs of the annotation.
     */
    @SuppressWarnings("unchecked")
    private void createAnnotationValues(final NormalAnnotation existingAnnotation, final NormalAnnotation annotation) {
        final AST ast = annotation.getAST();
        final List<MemberValuePair> values = annotation.values();
        final List<MemberValuePair> existingValues = existingAnnotation.values();
        for (final MemberValuePair existingPair : existingValues) {
            if ("value".equals(existingPair.getName().getFullyQualifiedName())) {
                final MemberValuePair pair = (MemberValuePair) ast.createInstance(MemberValuePair.class);
                pair.setName(ASTUtil.copy(existingPair.getName()));
                pair.setValue(createArrayInitializer(existingPair.getValue()));
                values.add(pair);
            } else {
                values.add(ASTUtil.copy(existingPair));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayInitializer createArrayInitializer(final Expression value) {
        final AST ast = value.getAST();
        final ArrayInitializer array;
        if (value instanceof ArrayInitializer) {
            array = createArrayInitializerAndCopyExpressions(ast, (ArrayInitializer) value);

        } else {
            array = (ArrayInitializer) ast.createInstance(ArrayInitializer.class);
            array.expressions().add(ASTUtil.copy(value));
        }

        array.expressions().add(createPMDLiteralValue(ast));
        return array;
    }

    @SuppressWarnings("unchecked")
    private ArrayInitializer createArrayInitializerAndCopyExpressions(final AST ast, final ArrayInitializer existingArray) {
        final ArrayInitializer array;
        array = (ArrayInitializer) ast.createInstance(ArrayInitializer.class);
        final List<Expression> expressions = array.expressions();
        final List<Expression> existingExpressions = existingArray.expressions();
        for (final Expression existingExpression : existingExpressions) {
            expressions.add(ASTUtil.copy(existingExpression));
        }
        return array;
    }

    /**
     * @return The position after the last existing annotation.
     */
    private int findPosition(final List<IExtendedModifier> modifiers) {
        int position = 0;
        int index = 0;
        for (final IExtendedModifier modifier : modifiers) {
            index++;
            if (modifier.isAnnotation()) {
                position = index;
            }
        }
        return position;
    }

}
