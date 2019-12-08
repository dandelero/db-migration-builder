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
package org.dandelero.dbmigrations.engine.util

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode

/**
 * Extension function to load a map at a given hierarchy.
 * @param keyHierarchy the key hierarchy.
 * @param delimiter the delimiter string.
 * @return the map at the hierarchy specified.
 * @throws ApplicationException if the map does not exist at the hierarchy.
 */
fun Map<String, Any?>.getRequiredMapHierarchy(keyHierarchy: String, delimiter: String = "/"): Map<String, Any?> {
    return keyHierarchy.split(delimiter).fold(this, { map, key ->
        map.getRequiredMap(key)
    })
}

/**
 * Extension function to get a map at the given key.
 * @param key the key.
 * @return the map at the key specified.
 * @throws ApplicationException if the map does not exist at the key.
 */
fun Map<String, Any?>.getRequiredMap(key: String): Map<String, Any?> {
    return get(key) as? Map<String, Any?>
            ?: throw ApplicationException(ErrorCode.MISSING_RESOURCE.withDetails("Missing '$key' config"))
}

/**
 * Gets the required value at the given key.
 * @param key the key name.
 * @return the value value.
 * @throws ApplicationException if the value does not exist at the key.
 */
fun Map<String, Any?>.getRequired(key: String): Any {
    return get(key) ?: throw ApplicationException(ErrorCode.MISSING_RESOURCE.withDetails("Missing '$key' config"))
}

/**
 * Gets the string at the given key.
 * @param key the key name.
 * @return the string value.
 * @throws ApplicationException if the string does not exist at the key.
 */
fun Map<String, Any?>.getRequiredString(key: String): String {
    return getRequired(key) as? String
            ?: throw ApplicationException(ErrorCode.MISSING_RESOURCE.withDetails(("Missing '$key' config")))
}

/**
 * Gets the optional string at the given key.
 * @param key the key name.
 * @return the string value or null.
 */
fun Map<String, Any?>.getOptionalString(key: String): String? {
    return getRequired(key) as? String
}

/**
 * Gets the boolean at the given key.
 * @param key the key name.
 * @return the boolean value.
 * @throws ApplicationException if the value does not exist at the key.
 */
fun Map<String, Any?>.getRequiredBoolean(key: String): Boolean {
    return getRequired(key) as? Boolean
            ?: throw ApplicationException(ErrorCode.MISSING_RESOURCE.withDetails("Missing '$key' config"))
}