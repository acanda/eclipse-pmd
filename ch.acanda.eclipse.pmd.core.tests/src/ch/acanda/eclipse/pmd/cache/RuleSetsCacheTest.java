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

package ch.acanda.eclipse.pmd.cache;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import net.sourceforge.pmd.RuleSets;

import org.junit.Test;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.domain.WorkspaceModel;

import com.google.common.cache.CacheLoader;

/**
 * Unit tests for {@link RuleSetsCache}.
 * 
 * @author Philip Graf
 */
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public final class RuleSetsCacheTest {
    
    private static final String PROJECT_NAME_1 = "Foo";
    private static final String PROJECT_NAME_2 = "Bar";

    private static final RuleSets RULE_SETS_FOO_1 = new TestRuleSets("RULE_SETS_FOO_1");
    private static final RuleSets RULE_SETS_FOO_2 = new TestRuleSets("RULE_SETS_FOO_2");
    private static final RuleSets RULE_SETS_BAR_1 = new TestRuleSets("RULE_SETS_BAR_1");
    private static final RuleSets RULE_SETS_BAR_2 = new TestRuleSets("RULE_SETS_BAR_2");

    /**
     * Verifies that the first cache access loads the rule sets from the cache loader.
     */
    @Test
    public void firstGetLoadsFromCache() throws Exception {
        final RuleSetsCache cache = new RuleSetsCache(getCacheLoaderMock(), getWorkspaceModel());

        final RuleSets actualRuleSets = cache.getRuleSets(PROJECT_NAME_1);

        assertSame("First cache access should return rule sets from loader", RULE_SETS_FOO_1, actualRuleSets);
    }
    
    /**
     * Verifies that the second cache access returns the cached rule sets.
     */
    @Test
    public void secondGetDoesNotLoad() throws Exception {
        final RuleSetsCache cache = new RuleSetsCache(getCacheLoaderMock(), getWorkspaceModel());
        
        cache.getRuleSets(PROJECT_NAME_1);
        final RuleSets actualRuleSets = cache.getRuleSets(PROJECT_NAME_1);

        assertSame("Second cache access should return cached rule sets", RULE_SETS_FOO_1, actualRuleSets);
    }

    /**
     * Verifies that the second cache access loads the rule sets if the project model's rule sets have been changed
     * after the first access.
     */
    @Test
    public void secondGetLoadsWhenProjectRuleSetsWereChanged() throws Exception {
        final WorkspaceModel workspaceModel = getWorkspaceModel();
        final RuleSetsCache cache = new RuleSetsCache(getCacheLoaderMock(), workspaceModel);
        cache.getRuleSets(PROJECT_NAME_1);
        final RuleSetModel ruleSetModel = new RuleSetModel("abc", new Location("path", LocationContext.WORKSPACE));
        workspaceModel.getOrCreateProject(PROJECT_NAME_1).setRuleSets(Arrays.asList(ruleSetModel));

        final RuleSets actualRuleSets = cache.getRuleSets(PROJECT_NAME_1);
        
        assertSame("Second cache access should reload rule sets", RULE_SETS_FOO_2, actualRuleSets);
    }
    
    /**
     * Verifies that the second cache access loads the rule sets if the project model's rule sets have been changed
     * after the first access. In this case, the project model was added after the rule sets cache was created.
     */
    @Test
    public void secondGetLoadsWhenLaterAddedProjectRuleSetsWereChanged() throws Exception {
        final WorkspaceModel workspaceModel = getWorkspaceModel();
        final RuleSetsCache cache = new RuleSetsCache(getCacheLoaderMock(), workspaceModel);
        workspaceModel.add(new ProjectModel(PROJECT_NAME_2));
        cache.getRuleSets(PROJECT_NAME_2);
        final RuleSetModel ruleSetModel = new RuleSetModel("abc", new Location("path", LocationContext.WORKSPACE));
        workspaceModel.getOrCreateProject(PROJECT_NAME_2).setRuleSets(Arrays.asList(ruleSetModel));
        
        final RuleSets actualRuleSets = cache.getRuleSets(PROJECT_NAME_2);
        
        assertSame("Second cache access should reload rule sets", RULE_SETS_BAR_2, actualRuleSets);
    }

    /**
     * Verifies that the second cache access loads the rule sets if the project model has been removed and added after
     * the first access.
     */
    @Test
    public void secondGetLoadsWhenProjectWasREmovedAndAddedAfterFirstGet() throws Exception {
        final WorkspaceModel workspaceModel = getWorkspaceModel();
        final RuleSetsCache cache = new RuleSetsCache(getCacheLoaderMock(), workspaceModel);
        cache.getRuleSets(PROJECT_NAME_1);
        workspaceModel.remove(PROJECT_NAME_1);
        workspaceModel.add(new ProjectModel(PROJECT_NAME_1));

        final RuleSets actualRuleSets = cache.getRuleSets(PROJECT_NAME_1);
        
        assertSame("Second cache access should reload rule sets", RULE_SETS_FOO_2, actualRuleSets);
    }

    private CacheLoader<String, RuleSets> getCacheLoaderMock() throws Exception {
        @SuppressWarnings("unchecked")
        final CacheLoader<String, RuleSets> loader = mock(CacheLoader.class);
        when(loader.load(PROJECT_NAME_1)).thenReturn(RULE_SETS_FOO_1, RULE_SETS_FOO_2);
        when(loader.load(PROJECT_NAME_2)).thenReturn(RULE_SETS_BAR_1, RULE_SETS_BAR_2);
        return loader;
    }
    
    private WorkspaceModel getWorkspaceModel() {
        final WorkspaceModel workspaceModel = new WorkspaceModel();
        workspaceModel.add(new ProjectModel(PROJECT_NAME_1));
        return workspaceModel;
    }
    
    private static final class TestRuleSets extends RuleSets {
        
        private final String name;
        
        public TestRuleSets(final String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
    }
}
