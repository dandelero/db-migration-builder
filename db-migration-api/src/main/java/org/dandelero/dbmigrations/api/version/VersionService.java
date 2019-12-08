/*
 * Copyright 2019 Dandelero (dandelero@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.dandelero.dbmigrations.api.version;

import org.dandelero.dbmigrations.api.module.Module;

/**
 * Provides a service for interacting with versions within a module.
 * <br />
 * Created at: 29/10/19 7:14 am
 *
 * @param <M> the type of module this service interacts with.
 * @param <V> the type of version scheme this service supports.
 * @author dandelero
 */
public interface VersionService<M extends Module, V extends Version> {

    /**
     * Gets the latest version for the specified module.
     *
     * @param module the module whose latest version is to be retrieved.
     * @return the latest version available or null.
     */
    V latestVersion(M module);

    /**
     * Gets the specific version in the module.
     *
     * @param module the module whose specific version is to be retrieved.
     * @return that version or null.
     */
    V getVersion(M module, String versionString);

}

