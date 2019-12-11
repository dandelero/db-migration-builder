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

/**
 * The category of delta scripts supported by the application.
 * <br />
 * Delta scripts can be applied upon upgrade or rollback, or in both upgrade and rollback operations.
 * <br />
 * Created at: 28/10/19 5:59 pm
 *
 * @author dandelero
 */
public enum DeltaScriptCategory {

    /**
     * Upgrade scripts to take the database to the next state.
     */
    UPGRADE,

    /**
     * Rollback scripts to restore the database to the prior state.
     */
    ROLLBACK,

    /**
     * General scripts that are to be applied for any state transition in the database.
     */
    BIDIRECTIONAL;
}