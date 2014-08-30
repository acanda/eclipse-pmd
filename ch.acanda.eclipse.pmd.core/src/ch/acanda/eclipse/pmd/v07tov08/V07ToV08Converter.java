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

package ch.acanda.eclipse.pmd.v07tov08;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

import ch.acanda.eclipse.pmd.domain.Location;
import ch.acanda.eclipse.pmd.domain.LocationContext;
import ch.acanda.eclipse.pmd.domain.ProjectModel;
import ch.acanda.eclipse.pmd.domain.RuleSetModel;
import ch.acanda.eclipse.pmd.repository.ProjectModelRepository;

import com.google.common.collect.ImmutableList;

/**
 * eclipse-pmd up to version 0.7 used to store its settings in the Eclipse preferences store. Since version 0.8 the
 * settings are stored in files in the respective project so they can be shared.
 *
 * @author Philip Graf
 */
@SuppressWarnings("deprecation")
public final class V07ToV08Converter {

    private V07ToV08Converter() {
        // hide constructor of utility class
    }

    /**
     * Loads the settings from the preference store, converts them to the version 0.8 domain model, stores them as files
     * in the project and deletes the old settings in the preference store.
     */
    public static void moveSettings(final IPreferenceStore preferenceStore, final ProjectModelRepository repository) {
        final PMDWorkspaceSettings workspaceSettings = new PMDWorkspaceSettings(preferenceStore);
        final ImmutableList<RuleSetConfiguration> configs = workspaceSettings.getRuleSetsConfigurations();
        if (!configs.isEmpty()) {
            boolean allSettingsMoved = true;
            for (final IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
                if (project.isAccessible()) {
                    moveProjectSettings(project, configs, repository);
                } else {
                    allSettingsMoved = false;
                }
            }
            if (allSettingsMoved) {
                workspaceSettings.deleteSettings();
            }
        }
    }

    private static void moveProjectSettings(final IProject project, final ImmutableList<RuleSetConfiguration> configs,
            final ProjectModelRepository repository) {
        final PMDProjectSettings pmdProjectSettings = new PMDProjectSettings(project);
        final Set<RuleSetConfiguration> activeConfigs = pmdProjectSettings.getActiveRuleSetConfigurations(configs);
        if (!activeConfigs.isEmpty()) {
            final ProjectModel projectModel = new ProjectModel(project.getName());
            projectModel.setPMDEnabled(pmdProjectSettings.isPMDEnabled());
            final List<RuleSetModel> ruleSets = new ArrayList<>(activeConfigs.size());
            for (final RuleSetConfiguration config : activeConfigs) {
                final Location location = getLocation(config, project.getWorkspace().getRoot());
                final RuleSetModel ruleSetModel = new RuleSetModel(config.getName(), location);
                ruleSets.add(ruleSetModel);
            }
            projectModel.setRuleSets(ruleSets);
            repository.save(projectModel);
        }
        if (repository.load(project.getName()).isPresent()) {
            pmdProjectSettings.deleteSettings();
        }
    }

    protected static Location getLocation(final RuleSetConfiguration config, final IWorkspaceRoot workspaceRoot) {
        final LocationContext context = getContext(config);
        if (context == LocationContext.WORKSPACE) {
            return convertWorkspacePath(config, workspaceRoot);
        }
        return new Location(config.getLocation(), context);
    }

    private static Location convertWorkspacePath(final RuleSetConfiguration config, final IWorkspaceRoot workspaceRoot) {
        final Path target = Paths.get(workspaceRoot.getLocationURI()).resolve(config.getLocation()).normalize();
        for (final IProject project : workspaceRoot.getProjects()) {
            final Path projectPath = Paths.get(project.getLocationURI()).normalize();
            if (target.startsWith(projectPath)) {
                final String location = Paths.get(project.getName()).resolve(projectPath.relativize(target)).toString();
                return new Location(location, LocationContext.WORKSPACE);
            }
        }
        // fallback: if the workspace path cannot be converted,
        // replace the workspace location with a file system location
        return new Location(target.toString(), LocationContext.FILESYSTEM);
    }

    private static LocationContext getContext(final RuleSetConfiguration config) {
        final LocationContext context;
        if (config instanceof WorkspaceRuleSetConfiguration) {
            context = LocationContext.WORKSPACE;
        } else if (config instanceof ProjectRuleSetConfiguration) {
            context = LocationContext.PROJECT;
        } else if (config instanceof FileSystemRuleSetConfiguration) {
            context = LocationContext.FILESYSTEM;
        } else if (config instanceof RemoteRuleSetConfiguration) {
            context = LocationContext.REMOTE;
        } else {
            throw new IllegalStateException("Unknown RuleSetConfiguration instance: " + config.getClass().getSimpleName());
        }
        return context;
    }

}
