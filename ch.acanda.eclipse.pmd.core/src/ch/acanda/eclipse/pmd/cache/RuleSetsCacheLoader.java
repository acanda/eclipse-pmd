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

package ch.acanda.eclipse.pmd.cache;

import static com.google.common.base.Optional.presentInstances;
import static com.google.common.collect.Iterables.transform;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetReferenceId;
import net.sourceforge.pmd.RuleSets;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.builder.LocationResolver;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.repository.ProjectModelRepository;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @author Philip Graf
 */
public class RuleSetsCacheLoader extends CacheLoader<String, RuleSets> {

    private final ProjectModelRepository repository = new ProjectModelRepository();

    @Override
    public RuleSets load(final String projectName) {
        PMDPlugin.getDefault().info("RuleSetsCache: loading rule sets for project " + projectName);
        try {
            final ProjectModel projectModel = repository.load(projectName).or(new ProjectModel(projectName));
            final ImmutableSortedSet<RuleSetModel> ruleSetModels = projectModel.getRuleSets();
            final Iterable<RuleSetReferenceId> ids = presentInstances(transform(ruleSetModels, new ToReferenceId(projectName)));
            return new RuleSetFactory().createRuleSets(ImmutableList.copyOf(ids));
        } catch (final RuleSetNotFoundException e) {
            PMDPlugin.getDefault().error("Cannot load rule sets for project " + projectName, e);
            return new RuleSets();
        }
    }

    private static final class ToReferenceId implements Function<RuleSetModel, Optional<RuleSetReferenceId>> {

        private final IProject project;

        public ToReferenceId(final String projectName) {
            project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        }

        @Override
        public Optional<RuleSetReferenceId> apply(final RuleSetModel model) {
            final Optional<String> resolvedLocation = LocationResolver.resolveIfExists(model.getLocation(), project);
            return resolvedLocation.transform(new Function<String, RuleSetReferenceId>() {
                @Override
                public RuleSetReferenceId apply(final String location) {
                    return new RuleSetReferenceId(location);
                }
            });
        }

    }

}
