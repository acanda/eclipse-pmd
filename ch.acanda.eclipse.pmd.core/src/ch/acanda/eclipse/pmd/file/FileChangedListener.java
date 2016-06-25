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

package ch.acanda.eclipse.pmd.file;

import java.nio.file.Path;

/**
 * A {@code FileChangeListener} receives a notification when a file has been change when it's registered with a
 * {@link FileWatcher}.
 * 
 * @author Philip Graf
 */
public interface FileChangedListener {

    void fileChanged(Path file);

}
