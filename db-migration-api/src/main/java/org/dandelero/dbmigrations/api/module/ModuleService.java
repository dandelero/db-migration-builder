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
package org.dandelero.dbmigrations.api.module;

import java.util.List;

/**
 * Provides services for using {@link Module} instances.
 * <br />
 * Created at: 29/10/19 7:04 am
 *
 * @param <M> the module type.
 * @author dandelero
 */
public interface ModuleService<M extends Module> {

    /**
     * Finds the module with the given name.
     *
     * @param moduleName the name of the module.
     * @return the module matching the name; null if not found.
     */
    M findModuleByName(String moduleName);

    /**
     * @return all the modules available.
     */
    List<M> listAllModules();

}