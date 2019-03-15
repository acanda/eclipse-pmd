// =====================================================================
//
// Copyright (C) 2012 - 2019, Philip Graf
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v10.html
//
// =====================================================================

package ch.acanda.eclipse.pmd.cache;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableSortedSet;

import ch.acanda.eclipse.pmd.PMDPlugin;
import ch.acanda.eclipse.pmd.builder.LocationResolver;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.repository.ProjectModelRepository;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSetReferenceId;
import net.sourceforge.pmd.RuleSets;

/**
 * @author Philip Graf
 */
public class RuleSetsCacheLoader extends CacheLoader<String, RuleSets> {

    private final ProjectModelRepository repository = new ProjectModelRepository();

    @Override
    public RuleSets load(final String projectName) {
        PMDPlugin.getDefault().info("RuleSetsCache: loading rule sets for project " + projectName);
        try {
            final ProjectModel projectModel = repository.load(projectName).orElseGet(() -> new ProjectModel(projectName));
            final ImmutableSortedSet<RuleSetModel> ruleSetModels = projectModel.getRuleSets();
            final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            final List<RuleSetReferenceId> ids = ruleSetModels.stream()
                    .<Optional<String>>map(model -> LocationResolver.resolveIfExists(model.getLocation(), project))
                    .filter(location -> location.isPresent())
                    .map(location -> new RuleSetReferenceId(location.get()))
                    .collect(Collectors.toList());
            return new RuleSetFactory().createRuleSets(ids);
        } catch (final RuleSetNotFoundException e) {
            PMDPlugin.getDefault().error("Cannot load rule sets for project " + projectName, e);
            return new RuleSets();
        }
    }

}
