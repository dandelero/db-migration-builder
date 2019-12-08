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
package org.dandelero.dbmigrations.api.delta;

import org.dandelero.dbmigrations.api.module.Module;
import org.dandelero.dbmigrations.api.version.Version;

import java.util.List;

/**
 * Provide services for interacting with {@link DeltaScript} instances.
 * <br />
 * Created at: 29/10/19 7:38 am
 *
 * @author dandelero
 */
public interface DeltaScriptService<M extends Module, V extends Version> {

    /**
     * Gets the change scripts for the given category in the version.
     *
     * @param category the category of scripts sought.
     * @param module   the module to search within.
     * @param version  the version whose scripts are sought.
     * @return the list of scripts found.
     */
    List<DeltaScript> getScripts(DeltaScriptCategory category, M module, V version);
}
