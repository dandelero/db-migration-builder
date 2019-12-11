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

/**
 * A module is a logical grouping of database resources that is independently versioned from other aspects of the
 * database.
 * <p/>
 * For example, in a complex system, you may have modules for "Account", "HR", "Remuneration", and each of those
 * modules can be modified (and versioned) independent of other modules.
 * <br />
 * Typical hierarchy:
 * <ul>
 *     <li>
 *         Accounting
 *         <ul>
 *             <li>1.0.0</li>
 *             <li>1.0.1</li>
 *         </ul>
 *     </li>
 *     <li>
 *         HR
 *         <ul>
 *             <li>0.2.3</li>
 *             <li>0.3.0</li>
 *         </ul>
 *     </li>
 * </ul>
 * <p>
 * <br />
 * Created at: 29/10/19 6:52 am
 *
 * @author dandelero
 */
public interface Module extends Comparable<Module> {

    /**
     * @return the name of this module.
     */
    String getName();

    /**
     * @return true if this is the default module.
     */
    boolean isDefault();

}
