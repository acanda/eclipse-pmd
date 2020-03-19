// =====================================================================
//
// Copyright (C) 2012 - 2020, Philip Graf
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
import ch.acanda.eclipse.pmd.java.resolution.bestpractices.DefaultLabelNotLastInSwitchStmtQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.bestpractices.MethodReturnsInternalArrayQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.bestpractices.UseCollectionIsEmptyQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.bestpractices.UseVarargsQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.codestyle.ExtendsObjectQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.codestyle.LocalVariableCouldBeFinalQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.codestyle.MethodArgumentCouldBeFinalQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.codestyle.UnnecessaryReturnQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.SingularFieldQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.UseUtilityClassQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.design.UselessOverridingMethodQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyIfStmtQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyInitializerQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyStatementBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyStatementNotInLoopQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyStaticInitializerQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptySwitchStatementsQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptySynchronizedBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyTryBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.emptycode.EmptyWhileStmtQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.errorprone.EmptyFinallyBlockQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.errorprone.EqualsNullQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.errorprone.SuspiciousHashcodeMethodNameQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.multithreading.UseNotifyAllInsteadOfNotifyQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.AddEmptyStringQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.AppendCharacterWithCharQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.ByteInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.ByteInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.IntegerInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.IntegerInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.LongInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.LongInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.RedundantFieldInitializerQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.ShortInstantiationAutoboxingQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.ShortInstantiationValueOfQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.SimplifyStartsWithQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.StringToStringQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.UnnecessaryCaseChangeQuickFix;
import ch.acanda.eclipse.pmd.java.resolution.performance.UseIndexOfCharQuickFix;
import ch.acanda.eclipse.pmd.marker.PMDMarker;

@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.ExcessiveImports" })
public final class JavaQuickFixGenerator {

    private static final Version JAVA_5 = new Version(1, 5, 0);
    private static final Version JAVA_8 = new Version(1, 8, 0);

    @SuppressWarnings("unchecked")
    private static final ImmutableListMultimap<String, Class<? extends IMarkerResolution>> QUICK_FIXES =
            new Builder<String, Class<? extends IMarkerResolution>>()
                    .putAll("java.best practices.DefaultLabelNotLastInSwitchStmt", DefaultLabelNotLastInSwitchStmtQuickFix.class)
                    .putAll("java.best practices.MethodReturnsInternalArray", MethodReturnsInternalArrayQuickFix.class)
                    .putAll("java.best practices.UseCollectionIsEmpty", UseCollectionIsEmptyQuickFix.class)
                    .putAll("java.best practices.UseVarargs", UseVarargsQuickFix.class)
                    .putAll("java.code style.ExtendsObject", ExtendsObjectQuickFix.class)
                    .putAll("java.code style.LocalVariableCouldBeFinal", LocalVariableCouldBeFinalQuickFix.class)
                    .putAll("java.code style.MethodArgumentCouldBeFinal", MethodArgumentCouldBeFinalQuickFix.class)
                    .putAll("java.code style.UnnecessaryReturn", UnnecessaryReturnQuickFix.class)
                    .putAll("java.design.SingularField", SingularFieldQuickFix.class)
                    .putAll("java.design.UselessOverridingMethod", UselessOverridingMethodQuickFix.class)
                    .putAll("java.design.UseUtilityClass", UseUtilityClassQuickFix.class)
                    .putAll("java.error prone.EmptyFinallyBlock", EmptyFinallyBlockQuickFix.class)
                    .putAll("java.error prone.EmptyIfStmt", EmptyIfStmtQuickFix.class)
                    .putAll("java.error prone.EmptyInitializer", EmptyInitializerQuickFix.class)
                    .putAll("java.error prone.EmptyStatementBlock", EmptyStatementBlockQuickFix.class)
                    .putAll("java.error prone.EmptyStatementNotInLoop", EmptyStatementNotInLoopQuickFix.class)
                    .putAll("java.error prone.EmptyStaticInitializer", EmptyStaticInitializerQuickFix.class)
                    .putAll("java.error prone.EmptySwitchStatements", EmptySwitchStatementsQuickFix.class)
                    .putAll("java.error prone.EmptySynchronizedBlock", EmptySynchronizedBlockQuickFix.class)
                    .putAll("java.error prone.EmptyTryBlock", EmptyTryBlockQuickFix.class)
                    .putAll("java.error prone.EmptyWhileStmt", EmptyWhileStmtQuickFix.class)
                    .putAll("java.error prone.EqualsNull", EqualsNullQuickFix.class)
                    .putAll("java.error prone.SuspiciousHashcodeMethodName", SuspiciousHashcodeMethodNameQuickFix.class)
                    .putAll("java.multithreading.UseNotifyAllInsteadOfNotify", UseNotifyAllInsteadOfNotifyQuickFix.class)
                    .putAll("java.performance.AddEmptyString", AddEmptyStringQuickFix.class)
                    .putAll("java.performance.AppendCharacterWithChar", AppendCharacterWithCharQuickFix.class)
                    .putAll("java.performance.ByteInstantiation",
                            ByteInstantiationAutoboxingQuickFix.class,
                            ByteInstantiationValueOfQuickFix.class)
                    .putAll("java.performance.IntegerInstantiation",
                            IntegerInstantiationAutoboxingQuickFix.class,
                            IntegerInstantiationValueOfQuickFix.class)
                    .putAll("java.performance.LongInstantiation",
                            LongInstantiationAutoboxingQuickFix.class,
                            LongInstantiationValueOfQuickFix.class)
                    .putAll("java.performance.RedundantFieldInitializer", RedundantFieldInitializerQuickFix.class)
                    .putAll("java.performance.ShortInstantiation",
                            ShortInstantiationAutoboxingQuickFix.class,
                            ShortInstantiationValueOfQuickFix.class)
                    .putAll("java.performance.SimplifyStartsWith", SimplifyStartsWithQuickFix.class)
                    .putAll("java.performance.StringToString", StringToStringQuickFix.class)
                    .putAll("java.performance.UnnecessaryCaseChange", UnnecessaryCaseChangeQuickFix.class)
                    .putAll("java.performance.UseIndexOfChar", UseIndexOfCharQuickFix.class)
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
