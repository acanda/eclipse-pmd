// =====================================================================
//
// Copyright (C) 2012 - 2013, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.cache;

import static ch.acanda.eclipse.pmd.domain.ProjectModel.RULESETS_PROPERTY;
import static ch.acanda.eclipse.pmd.domain.WorkspaceModel.PROJECTS_PROPERTY;
import static java.util.concurrent.TimeUnit.HOURS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.sourceforge.pmd.RuleSets;
import ch.acanda.eclipse.pmd.domain.DomainModel.AddElementPropertyChangeEvent;
import ch.acanda.eclipse.pmd.domain.DomainModel.RemoveElementPropertyChangeEvent;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.WorkspaceModel;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * The rule set cache caches the PMD rule sets so they do not have to be rebuilt every time PMD is invoked.
 *
 * @author Philip Graf
 */
public final class RuleSetsCache {

    /**
     * Maps a project name to the projects rule set.
     */
    private final LoadingCache<String, RuleSets> cache;

    private final ProjectModelListener projectModelListener = new ProjectModelListener();

    public RuleSetsCache(final CacheLoader<String, RuleSets> loader, final WorkspaceModel workspaceModel) {
        // by expiring the rule sets we make sure to notice changes in remote configurations
        cache = CacheBuilder.newBuilder().expireAfterWrite(1, HOURS).build(loader);

        for (final ProjectModel projectModel : workspaceModel.getProjects()) {
            projectModel.addPropertyChangeListener(RULESETS_PROPERTY, projectModelListener);
        }
        workspaceModel.addPropertyChangeListener(PROJECTS_PROPERTY, new WorkspaceModelListener());
    }

    /**
     * Returns the PMD rule sets of the provided project. The rule sets are taken from the cache if already available or
     * loaded from the repository if not.
     *
     * @param projectName The name of the project.
     * @return The PMD rule sets of the project.
     */
    public RuleSets getRuleSets(final String projectName) {
        return cache.getUnchecked(projectName);
    }

    /**
     * Keeps track of added and removed project models.
     */
    private final class WorkspaceModelListener implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {

            if (event instanceof AddElementPropertyChangeEvent) {
                // A project has been added. Add a listener to invalidate its cache entry when its rule sets change
                final ProjectModel projectModel = (ProjectModel) ((AddElementPropertyChangeEvent) event).getAddedElement();
                projectModel.addPropertyChangeListener(RULESETS_PROPERTY, projectModelListener);

            } else if (event instanceof RemoveElementPropertyChangeEvent) {
                // A project has been removed. Invalidate it's cache entry to release the cached resources.
                final ProjectModel projectModel = (ProjectModel) ((RemoveElementPropertyChangeEvent) event).getRemovedElement();
                cache.invalidate(projectModel.getProjectName());
                projectModel.removePropertyChangeListener(RULESETS_PROPERTY, projectModelListener);
            }
        }
    }

    /**
     * Invalidates the cache entry of a project if there have been made any changes to the respective project model so
     * its rule set is rebuilt based on the new model data the next time it is used.
     */
    private final class ProjectModelListener implements PropertyChangeListener {
        @Override
        public void propertyChange(final PropertyChangeEvent event) {
            final String projectName = ((ProjectModel) event.getSource()).getProjectName();
            cache.invalidate(projectName);
        }
    }

}
