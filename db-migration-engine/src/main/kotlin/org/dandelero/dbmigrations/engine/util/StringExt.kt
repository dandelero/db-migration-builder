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

import org.apache.commons.text.StringSubstitutor

/**
 * Substitutes the placeholders in this string with values from the map.
 * @param context the context containing placeholder values to be substituted.
 * @return the updates string.
 */
fun String.substitutePlaceholders(context: Map<String, Any>): String {
    return String(StringSubstitutor(context).replace(this).toByteArray())
}

/**
 * Prefixes the given string value with this string if the value is not empty/null.
 * @param value the value string that needs to be prefixed.
 * @return the prefixed string or an empty string.
 */
fun String.prefixNotNull(value: String?): String {
    return if (value.isNullOrBlank()) {
        ""
    } else {
        this + value
    }
}