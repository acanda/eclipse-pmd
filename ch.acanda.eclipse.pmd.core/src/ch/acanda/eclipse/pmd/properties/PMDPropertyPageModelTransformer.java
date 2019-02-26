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

package ch.acanda.eclipse.pmd.properties;

import static ch.acanda.eclipse.pmd.domain.LocationContext.FILE_SYSTEM;
import static ch.acanda.eclipse.pmd.domain.LocationContext.PROJECT;
import static ch.acanda.eclipse.pmd.domain.LocationContext.REMOTE;
import static ch.acanda.eclipse.pmd.domain.LocationContext.WORKSPACE;
import static com.google.common.collect.Iterables.transform;

import org.eclipse.core.resources.IProject;

import ch.acanda.eclipse.pmd.builder.LocationResolver;
import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.properties.PMDPropertyPageViewModel.RuleSetViewModel;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableBiMap;

/**
 * Transforms the domain model to and from the PMD property page's view model.
 *
 * @author Philip Graf
 */
public final class PMDPropertyPageModelTransformer {

    /**
     * Maps a location context to a label of a rule set type and vice versa. The label is used as the value shown in the
     * "Type" column of the table in the PMD property page.
     */
    private static final ImmutableBiMap<LocationContext, String> CONTEXT_TYPE_MAP = ImmutableBiMap.of(WORKSPACE, "Workspace",
                                                                                                      PROJECT, "Project",
                                                                                                      FILE_SYSTEM, "File System",
                                                                                                      REMOTE, "Remote");

    /**
     * Transforms a rule set view model to a rule set domain model.
     */
    private static final Function<RuleSetViewModel, RuleSetModel> TO_RULESETMODEL = new Function<RuleSetViewModel, RuleSetModel>() {
        @Override
        public RuleSetModel apply(final RuleSetViewModel viewModel) {
            return toDomainModel(viewModel);
        }
    };

    private PMDPropertyPageModelTransformer() {
        // hide constructor of utility class
    }

    /**
     * Returns an iterable transforming a rule set domain model to a rule set view model on demand, i.e. the returned
     * iterable is a view on the supplied iterable.
     */
    public static Iterable<RuleSetViewModel> toViewModels(final Iterable<RuleSetModel> domainModels, final IProject project) {
        return transform(domainModels, new Function<RuleSetModel, RuleSetViewModel>() {
            @Override
            public RuleSetViewModel apply(final RuleSetModel domainModel) {
                return toViewModel(domainModel, project);
            }
        });
    }

    /**
     * Transforms a rule set domain model to a rule set view model.
     */
    public static RuleSetViewModel toViewModel(final RuleSetModel ruleSetModel, final IProject project) {
        final String name = ruleSetModel.getName();
        final String type = CONTEXT_TYPE_MAP.get(ruleSetModel.getLocation().getContext());
        final String location = ruleSetModel.getLocation().getPath();
        final boolean isValidLocation = LocationResolver.resolveIfExists(ruleSetModel.getLocation(), project).isPresent();
        final String resolvedLocation = LocationResolver.resolve(ruleSetModel.getLocation(), project);
        return new RuleSetViewModel(name, type, location, isValidLocation, resolvedLocation);
    }

    /**
     * Returns an iterable transforming a rule set view model to a rule set domain model on demand, i.e. the returned
     * iterable is a view on the supplied iterable.
     */
    public static Iterable<RuleSetModel> toDomainModels(final Iterable<RuleSetViewModel> viewModels) {
        return transform(viewModels, TO_RULESETMODEL);
    }

    /**
     * Transforms a rule set view model to a rule set domain model.
     */
    public static RuleSetModel toDomainModel(final RuleSetViewModel viewModel) {
        final String name = viewModel.getName();
        final String path = viewModel.getLocation();
        final LocationContext context = CONTEXT_TYPE_MAP.inverse().get(viewModel.getType());
        return new RuleSetModel(name, new Location(path, context));
    }
}
