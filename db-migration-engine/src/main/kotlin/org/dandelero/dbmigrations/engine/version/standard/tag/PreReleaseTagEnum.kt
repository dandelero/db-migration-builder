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
package org.dandelero.dbmigrations.engine.version.standard.tag

/**
 * Enumerates the types of tags supported by the application.
 * <br />
 * Created at: 31/10/19 8:02 am
 * @param label the label name.
 * @param weight the weight of the label.
 * @author dandelero
 */
enum class PreReleaseTagEnum(val label: String, val weight: Int) {

    /**
     * An alpha version.
     */
    ALPHA("alpha", 1),

    /**
     * A beta version.
     */
    BETA("beta", 2),

    /**
     * A release candidate version.
     */
    RELEASE_CANDIDATE("rc", 3);

    companion object {

        /**
         * Creates an instance of the enumeration from the label.
         * @param label the label value.
         * @return the corresponding enum if it is supported; null otherwise.
         */
        fun fromLabel(label: String): PreReleaseTagEnum? {
            values().forEach { v ->
                if (v.label == label) {
                    return v
                }
            }
            return null
        }
    }
}