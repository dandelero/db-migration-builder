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
package org.dandelero.dbmigrations.engine.version.simple

import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.version.Version
import org.dandelero.dbmigrations.engine.util.prefixNotNull

/**
 * Represents a 4-digit numbering scheme, such as `1.2.32.192`, according to the following scheme: <br />
 * ```
 * ${major}.${minor}.${build}.${revision}
 * ```
 * where the `.` is the separator char.
 * <br />
 * A minimum of 2 digits is required, and valid versions include:
 * <br />
 * ```
 * 0.0.0.2
 * 1.0
 * 1.2.3
 * 93.2291.34.0
 * ```
 * <br />
 * Created at: 31/10/19 7:50 am
 * @param separator the separator string to be used between digits for serialization.
 * @author dandelero
 */
class FourDigitVersion(
    private val separator: String,
    val major: Int,
    val minor: Int,
    val build: Int? = null,
    val revision: Int? = null
) : Version {

    /**
     * The string representation of this scheme.
     */
    private var stringValue: String

    init {
        if (build == null && revision != null) {
            // No build => can't have a revision!
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails(
                    "A revision number cannot exist without a build number"))
        }

        if (!(major >= 0 && minor >= 0 && (build ?: 0) >= 0 && (revision ?: 0) >= 0)) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails(
                    "Invalid digits detected in the major/minor/build/revision values"))
        }

        // Now compose the string representation to keep things efficient.
        val (buildString, revisionString) = with(separator.prefixNotNull(build?.toString() ?: "")) {
            val buildString = this
            if (buildString.isEmpty()) {
                Pair(buildString, "")
            } else {
                val ps = separator.prefixNotNull(revision?.toString() ?: "")
                Pair(buildString, ps)
            }
        }

        stringValue = "$major$separator$minor$buildString$revisionString"
    }

    /**
     * Compares this instance with another [FourDigitVersion].
     * @param other the other [FourDigitVersion] instance.
     * @return -1, 0, 1 if this instance is less than, equivalent to, or greater than the other instance.
     */
    override fun compareTo(other: Version?): Int {
        if (other == null || other !is FourDigitVersion) {
            return 1
        }

        // major.
        if (major > other.major) {
            return 1
        }
        if (major < other.major) {
            return -1
        }

        // minor.
        if (minor > other.minor) {
            return 1
        } else if (minor < other.minor) {
            return -1
        }

        // build.
        if (build == null && other.build != null) {
            // 1.0 vs 1.0.2
            return -1
        } else if (build != null && other.build == null) {
            // 1.0.2 vs 1.0
            return 1
        } else if (build != null && other.build != null) {
            // 1.0.2 vs 1.0.4
            var result = build.compareTo(other.build)
            if (result != 0) {
                return result
            }

            // revision.
            if (revision == null && other.revision != null) {
                // 1.0.3 vs 1.0.3.2
                return -1
            } else if (revision != null && other.revision == null) {
                // 1.0.3.2 vs 1.0.3
                return 1
            } else if (revision != null && other.revision != null) {
                // 1.0.3.2 vs 1.0.3.8
                result = revision.compareTo(other.revision)
                if (result != 0) {
                    return result
                }
            }
        }

        return 0
    }

    /**
     * Checks for equivalence between this and the provided instance.
     * @param other the other instance.
     * @return true if they are equivalent.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FourDigitVersion

        if (major != other.major) return false
        if (minor != other.minor) return false
        if (build != other.build) return false
        if (revision != other.revision) return false

        return true
    }

    /**
     * @return the hashcode value.
     */
    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + (build ?: 0)
        result = 31 * result + (revision ?: 0)
        return result
    }

    /**
     * Converts the scheme to a string with the specified separator between the digits.
     * @return the string representation of the scheme.
     */
    override fun toString(): String = stringValue

    /**
     * @return the name of this version.
     */
    override fun getNameString(): String = toString()
}