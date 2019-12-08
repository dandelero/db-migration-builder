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
package org.dandelero.dbmigrations.engine.version.semver1

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.dandelero.dbmigrations.api.application.ApplicationException
import org.dandelero.dbmigrations.api.application.ErrorCode
import org.dandelero.dbmigrations.api.version.Version

/**
 * A simple semantic version implementation that uses the format:
 * <br />
 * &lt;major&rt;.&lt;minor&rt;.&lt;patch&rt;+&lt;date&rt;
 * <br />
 * Created at: 20/11/19 10:16 pm
 * @author dandelero
 * @param digitSeparator the separator string between digits.
 * @param major the major build number.
 * @param minor the minor build number.
 * @param patch the patch number.
 * @param dateSeparator the separator string between the version and date.
 * @param date the date of the build.
 */
class Semver1Version(
    private val digitSeparator: String,
    val major: Int,
    val minor: Int,
    val patch: Int,
    private val dateSeparator: String,
    dateFormat: String,
    val date: LocalDateTime
) : Version {

    /**
     * The pattern for formatting date/time values.
     */
    private val dateTimePattern = DateTimeFormatter.ofPattern(dateFormat)

    /**
     * The string representation of this scheme.
     */
    private var stringValue: String

    init {
        if (major < 0 || minor < 0 || patch < 0) {
            throw ApplicationException(ErrorCode.INVALID_VERSION
                    .withDetails("Positive values required for major($major)/minor($minor)/patch($patch)"))
        }

        if (digitSeparator.isNullOrEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION
                    .withDetails("No digit separator provided"))
        }
        if (dateSeparator.isNullOrEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION
                    .withDetails("No date separator provided"))
        }
        val dateString = dateTimePattern.format(date)
        stringValue = "$major$digitSeparator$minor$digitSeparator$patch$dateSeparator$dateString"
    }

    /**
     * Compares this instance with another [Semver1Version].
     * @param other the other [Semver1Version] instance.
     * @return -1, 0, 1 if this instance is less than, equivalent to, or greater than the other instance.
     */
    override fun compareTo(other: Version?): Int {
        if (other == null || other !is Semver1Version) {
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

        // patch.
        if (patch > other.patch) {
            return 1
        } else if (patch < other.patch) {
            return -1
        }

        return date.compareTo(other.date)
    }

    /**
     * Checks for equivalence between this and the provided instance.
     * @param other the other instance.
     * @return true if they are equivalent.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Semver1Version
        return nameString == other.nameString
    }

    /**
     * @return the hashcode value.
     */
    override fun hashCode(): Int {
        var result = digitSeparator.hashCode()
        result = 31 * result + major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + dateSeparator.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + (dateTimePattern?.hashCode() ?: 0)
        result = 31 * result + stringValue.hashCode()
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