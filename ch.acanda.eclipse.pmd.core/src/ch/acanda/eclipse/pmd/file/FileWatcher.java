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

package ch.acanda.eclipse.pmd.file;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ch.acanda.eclipse.pmd.PMDPlugin;

/**
 * Watches files and notifies registered listeners when they have changed.
 *
 * @author Philip Graf
 */
public final class FileWatcher {

    private final WatchService watchService;

    /**
     * Maps an absolute directory path to its watch key.
     */
    private final Map<Path, WatchKey> watchKeys;

    /**
     * Maps an absolute file path to its listeners.
     */
    private final Multimap<Path, FileChangedListener> listeners;

    /**
     * Maps an absolute directory path to its absolute file paths that are being watched.
     */
    private final Multimap<Path, Path> watchedFiles;

    private Optional<WatcherThread> watcherThread = Optional.empty();

    public FileWatcher() throws IOException {
        watchService = FileSystems.getDefault().newWatchService();
        watchKeys = new HashMap<>();
        listeners = HashMultimap.create();
        watchedFiles = HashMultimap.create();
    }

    public Subscription subscribe(final Path file, final FileChangedListener listener) throws IOException {
        final Path absoluteFile = file.toAbsolutePath();
        listeners.put(absoluteFile, listener);

        final Path absoluteDirectory = file.getParent();
        watchedFiles.put(absoluteDirectory, absoluteFile);

        if (!watchKeys.containsKey(absoluteDirectory)) {
            final WatchKey watchKey = absoluteDirectory.register(watchService, ENTRY_MODIFY, ENTRY_DELETE);
            watchKeys.put(absoluteDirectory, watchKey);
            if (watchKeys.size() == 1) {
                startWatcher();
            }
        }

        return new Subscription() {
            @Override
            public void cancel() {
                listeners.remove(absoluteFile, listener);
                watchedFiles.remove(absoluteDirectory, absoluteFile);
                if (!watchedFiles.containsKey(absoluteDirectory)) {
                    watchKeys.remove(absoluteDirectory);
                    if (watchKeys.isEmpty()) {
                        stopWatcher();
                    }
                }
            }
        };
    }

    private void startWatcher() {
        final WatcherThread watcher = new WatcherThread();
        watcherThread = Optional.of(watcher);
        watcher.start();
    }

    private void stopWatcher() {
        watcherThread.ifPresent(WatcherThread::interrupt);
        watcherThread = Optional.empty();
    }

    private final class WatcherThread extends Thread {

        public WatcherThread() {
            super("eclipse-pmd RuleSetWatcher");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    final WatchKey watchKey = watchService.take();
                    if (watchKey.isValid()) {
                        final Path directory = (Path) watchKey.watchable();
                        for (final WatchEvent<?> event : watchKey.pollEvents()) {
                            if (event.kind() != OVERFLOW) {
                                final String filename = event.context().toString();
                                final Path file = directory.resolve(filename);
                                PMDPlugin.getDefault().info(event.kind() + ": " + file);
                                for (final FileChangedListener listener : listeners.get(file)) {
                                    listener.fileChanged(file);
                                }
                            }
                        }
                    }
                    watchKey.reset();
                }
            } catch (final InterruptedException | ClosedWatchServiceException e) {
                PMDPlugin.getDefault().info(getName() + " stopped");
            }
        }

    }

}
