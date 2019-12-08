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
package org.dandelero.dbmigrations.engine.version.standard

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.engine.util.prefixNotNull

/**
 * A composite [Version] which is composed of another version and optional [PreReleaseTag].
 * For example, 1.0.0 and 3.2-alpha-3 are valid versions with tags.
 * <br />
 * Created at: 31/10/19 8:23 pm
 * @param prefix an optional prefix for the serialized value.
 * @param prefixSeparator an optional separator between the [prefix] and subsequent values.
 * @param version the version.
 * @param tagSeparator an optional separator between the serialized [version] and the [tag].
 * @param tag an optional tag value.
 * @author dandelero
 */
class VersionWithTag(
    private val prefix: String?,
    private val prefixSeparator: String?,
    private val version: Version,
    private val tagSeparator: String? = null,
    private val tag: PreReleaseTag? = null
) : Version {

    init {
        // Validate!
        if (!prefixSeparator.isNullOrEmpty() && prefix.isNullOrEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("You cannot specify a prefix separator without a prefix value"))
        }
    }

    /**
     * The string value of this instance.
     */
    private val stringValue: String = let {
        val tagPortion = tagSeparator?.prefixNotNull(tag?.toString() ?: "") ?: ""
        val schemePortion = (prefix?.plus(prefixSeparator ?: "") ?: "") + version.toString()
        schemePortion + tagPortion
    }

    /**
     * @return the name of this version.
     */
    override fun getNameString(): String {
        return toString()
    }

    override fun compareTo(other: Version?): Int {
        if (other == null || other !is VersionWithTag) {
            return 1
        }

        val result = version.compareTo(other.version)
        if (result != 0) {
            return result
        }

        return if (tag == null && other.tag == null) {
            0
        } else if (tag != null && other.tag != null) {
            tag.compareTo(other.tag)
        } else if (other.tag == null) {
            1
        } else {
            -1
        }
    }

    override fun toString(): String = stringValue
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VersionWithTag

        if (stringValue != other.stringValue) return false

        return true
    }

    override fun hashCode(): Int {
        return stringValue.hashCode()
    }

    companion object {
        // TODO: document!

        fun noPrefix(version: Version): VersionWithTag {
            return VersionWithTag(prefix = "", prefixSeparator = "",
                    tagSeparator = null, version = version,
                    tag = null)
        }

        fun noPrefix(version: Version, tag: PreReleaseTag, tagSeparator: String = "-"): VersionWithTag {
            return VersionWithTag(prefix = "", prefixSeparator = "",
                    tagSeparator = tagSeparator, version = version,
                    tag = tag)
        }
    }
}