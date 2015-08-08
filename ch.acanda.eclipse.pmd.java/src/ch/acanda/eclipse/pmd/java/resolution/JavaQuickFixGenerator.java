// =====================================================================
//
// Copyright (C) 2012 - 2015, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.java.resolution;

import org.eclipse.ui.IMarkerResolution;
import org.osgi.framework.Version;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableListMultimap.Builder;

import ch.acanda.eclipse.pmd.exception.EclipsePMDException;
import ch.acanda.eclipse.pmd.java.resolution.basic.ExtendsObjectQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.DefaultLabelNotLastInSwitchStmtQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.EqualsNullQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.SingularFieldQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.UseCollectionIsEmptyQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.UseNotifyAllInsteadOfNotifyQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.UseUtilityClassQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.UseVarargsQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyFinallyBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyIfStmtQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyInitializerQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyStatementBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyStatementNotInLoopQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyStaticInitializerQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptySwitchStatementsQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptySynchronizedBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyTryBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyWhileStmtQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.ByteInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.ByteInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.IntegerInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.IntegerInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.LongInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.LongInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.ShortInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.migration.ShortInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.naming.SuspiciousHashcodeMethodNameQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.optimization.AddEmptyStringQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.optimization.LocalVariableCouldBeFinalQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.optimization.MethodArgumentCouldBeFinalQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.optimization.RedundantFieldInitializerQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.optimization.SimplifyStartsWithQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.stringandstringbuffer.AppendCharacterWithCharQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.stringandstringbuffer.StringToStringQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.stringandstringbuffer.UseIndexOfCharQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.sunsecure.MethodReturnsInternalArrayQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.unnecessary.UnnecessaryReturnQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.unnecessary.UselessOverridingMethodQuickFix;
import ch.acanda.eclipse.pmd.marker.PMDMarker;

@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports" })
public final class JavaQuickFixGenerator {

    private static final Version JAVA_5 = new Version(1, 5, 0);
    private static final Version JAVA_8 = new Version(1, 8, 0);

    @SuppressWarnings("unchecked")
    private static final ImmutableListMultimap<String, Class<? extends IMarkerResolution>> QUICK_FIXES =
            new Builder<String, Class<? extends IMarkerResolution>>()
                    .putAll("java.basic.ExtendsObject", ExtendsObjectQuickFix.class)
                    .putAll("java.design.DefaultLabelNotLastInSwitchStmt", DefaultLabelNotLastInSwitchStmtQuickFix.class)
                    .putAll("java.design.EqualsNull", EqualsNullQuickFix.class)
                    .putAll("java.design.SingularField", SingularFieldQuickFix.class)
                    .putAll("java.design.UseCollectionIsEmpty", UseCollectionIsEmptyQuickFix.class)
                    .putAll("java.design.UseNotifyAllInsteadOfNotify", UseNotifyAllInsteadOfNotifyQuickFix.class)
                    .putAll("java.design.UseUtilityClass", UseUtilityClassQuickFix.class)
                    .putAll("java.design.UseVarargs", UseVarargsQuickFix.class)
                    .putAll("java.empty code.EmptyFinallyBlock", EmptyFinallyBlockQuickFix.class)
                    .putAll("java.empty code.EmptyIfStmt", EmptyIfStmtQuickFix.class)
                    .putAll("java.empty code.EmptyInitializer", EmptyInitializerQuickFix.class)
                    .putAll("java.empty code.EmptyStatementBlock", EmptyStatementBlockQuickFix.class)
                    .putAll("java.empty code.EmptyStatementNotInLoop", EmptyStatementNotInLoopQuickFix.class)
                    .putAll("java.empty code.EmptyStaticInitializer", EmptyStaticInitializerQuickFix.class)
                    .putAll("java.empty code.EmptySwitchStatements", EmptySwitchStatementsQuickFix.class)
                    .putAll("java.empty code.EmptySynchronizedBlock", EmptySynchronizedBlockQuickFix.class)
                    .putAll("java.empty code.EmptyTryBlock", EmptyTryBlockQuickFix.class)
                    .putAll("java.empty code.EmptyWhileStmt", EmptyWhileStmtQuickFix.class)
                    .putAll("java.migration.IntegerInstantiation",
                            IntegerInstantiationAutoboxingQuickFix.class,
                            IntegerInstantiationValueOfQuickFix.class)
                    .putAll("java.migration.ByteInstantiation",
                            ByteInstantiationAutoboxingQuickFix.class,
                            ByteInstantiationValueOfQuickFix.class)
                    .putAll("java.migration.ShortInstantiation",
                            ShortInstantiationAutoboxingQuickFix.class,
                            ShortInstantiationValueOfQuickFix.class)
                    .putAll("java.migration.LongInstantiation",
                            LongInstantiationAutoboxingQuickFix.class,
                            LongInstantiationValueOfQuickFix.class)
                    .putAll("java.naming.SuspiciousHashcodeMethodName", SuspiciousHashcodeMethodNameQuickFix.class)
                    .putAll("java.optimization.AddEmptyString", AddEmptyStringQuickFix.class)
                    .putAll("java.optimization.LocalVariableCouldBeFinal", LocalVariableCouldBeFinalQuickFix.class)
                    .putAll("java.optimization.MethodArgumentCouldBeFinal", MethodArgumentCouldBeFinalQuickFix.class)
                    .putAll("java.optimization.RedundantFieldInitializer", RedundantFieldInitializerQuickFix.class)
                    .putAll("java.optimization.SimplifyStartsWith", SimplifyStartsWithQuickFix.class)
                    .putAll("java.string and stringbuffer.AppendCharacterWithChar", AppendCharacterWithCharQuickFix.class)
                    .putAll("java.string and stringbuffer.UseIndexOfChar", UseIndexOfCharQuickFix.class)
                    .putAll("java.string and stringbuffer.StringToString", StringToStringQuickFix.class)
                    .putAll("java.security code guidelines.MethodReturnsInternalArray", MethodReturnsInternalArrayQuickFix.class)
                    .putAll("java.unnecessary.UselessOverridingMethod", UselessOverridingMethodQuickFix.class)
                    .putAll("java.unnecessary.UnnecessaryReturn", UnnecessaryReturnQuickFix.class)
                    .build();

    public boolean hasQuickFixes(final PMDMarker marker, final JavaQuickFixContext context) {
        if (context.getCompilerCompliance().compareTo(JAVA_5) >= 0) {
            // The SuppressWarningsQuickFix is always available when the compiler compliance is set to Java 5+.
            return true;
        }
        return QUICK_FIXES.containsKey(marker.getRuleId());
    }

    public ImmutableList<IMarkerResolution> getQuickFixes(final PMDMarker marker, final JavaQuickFixContext context) {
        final ImmutableList.Builder<IMarkerResolution> quickFixes = ImmutableList.builder();
        if (context.getCompilerCompliance().compareTo(JAVA_8) < 0) {
            for (final Class<? extends IMarkerResolution> quickFixClass : QUICK_FIXES.get(marker.getRuleId())) {
                quickFixes.add(createInstanceOf(quickFixClass, marker));
            }
        }
        if (context.getCompilerCompliance().compareTo(JAVA_5) >= 0) {
            quickFixes.add(new SuppressWarningsQuickFix(marker));
        }
        return quickFixes.build();
    }

    private IMarkerResolution createInstanceOf(final Class<? extends IMarkerResolution> quickFixClass,
            final PMDMarker marker) {
        try {
            return quickFixClass.getConstructor(PMDMarker.class).newInstance(marker);
        } catch (SecurityException | ReflectiveOperationException e) {
            throw new EclipsePMDException("Quick fix class " + quickFixClass + " is not correctly implemented", e);
        }
    }

}