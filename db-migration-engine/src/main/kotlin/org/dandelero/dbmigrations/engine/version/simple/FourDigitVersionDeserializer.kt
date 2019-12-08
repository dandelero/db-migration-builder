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
import org.dandelero.dbmigrations.api.version.VersionDeserializer
import org.slf4j.LoggerFactory

/**
 * Deserializes [FourDigitVersion] instances from their string representation.
 * <br />
 * Created at: 1/11/19 8:08 am
 *
 * @param digitSeparator the digit separator string.
 * @author dandelero
 */
class FourDigitVersionDeserializer(private val digitSeparator: String) : VersionDeserializer {

    /**
     * The logger instance.
     */
    private val logger = LoggerFactory.getLogger(FourDigitVersionDeserializer::class.java)

    init {
        if (digitSeparator.isNullOrEmpty()) {
            throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("Digit separator cannot be empty"))
        }
    }

    /**
     * The length of the [digitSeparator] parameter.
     */
    private val digitSeparatorLength = digitSeparator.length

    /**
     * Deserializes the given version string into a version object.
     * @param versionString the string representation of the version.
     * @return the reconstructed version or null.
     */
    override fun deserialize(versionString: String): Version? {
        var currentString: String = versionString

        // 1.0.3.1
        // Major number.
        val majorNumberStr = getNextNumber(currentString) ?: return null
        val majorNumber = majorNumberStr.toIntOrNull() ?: return null
        currentString = currentString.substring(majorNumberStr.length)
        if (!currentString.startsWith(digitSeparator)) {
            return null
        }
        currentString = currentString.substring(digitSeparatorLength)

        // 0.3.1
        // Minor number.
        val minorNumberStr = getNextNumber(currentString) ?: return null
        val minorNumber = minorNumberStr.toInt()
        currentString = currentString.substring(minorNumberStr.length)
        if (currentString.isEmpty()) { // (!currentString.startsWith(digitSeparator)) {
            // We have a major and minor number, which is good enough!
            return FourDigitVersion(digitSeparator, majorNumber, minorNumber)
        }

        // 3.1
        currentString = currentString.substring(digitSeparatorLength)
        if (currentString.isEmpty() || !currentString[0].isDigit()) {
            logger.debug("'$versionString' contains an additional value that is not a build number")
            return FourDigitVersion(digitSeparator, majorNumber, minorNumber)
        }

        val buildNumberStr = getNextNumber(currentString)
                ?: return FourDigitVersion(digitSeparator, majorNumber, minorNumber)
        val buildNumber = buildNumberStr.toInt()
        currentString = currentString.substring(buildNumberStr.length)

        if (currentString.isEmpty()) {
            // We have a major, minor and build numbers.
            return FourDigitVersion(digitSeparator, majorNumber, minorNumber, buildNumber)
        }

        // We should have a revision number now.
        currentString = currentString.substring(digitSeparatorLength)
        if (currentString.isEmpty() || !currentString[0].isDigit()) {
            logger.debug("'$versionString' contains an additional value that is not a revision number")
            return FourDigitVersion(digitSeparator, majorNumber, minorNumber, buildNumber)
        }
        // 1
        val revisionNumberStr = getNextNumber(currentString)
                ?: return FourDigitVersion(digitSeparator, majorNumber, minorNumber, buildNumber)
        val revisionNumber = revisionNumberStr.toInt()
        return FourDigitVersion(digitSeparator, majorNumber, minorNumber, buildNumber, revisionNumber)
    }

    /**
     * Gets the next set of digits in the string from the given index (inclusive).
     * @param objectValueString the string value.
     * @param fromIndex the index to start looking from (inclusive)
     * @return the next number (as a string) or null.
     */
    private fun getNextNumber(objectValueString: String, fromIndex: Int = 0): String? {
        var toIndex = fromIndex
        while (toIndex < objectValueString.length && objectValueString[toIndex].isDigit()) {
            toIndex++
        }

        if (toIndex != fromIndex) {
            val digits = objectValueString.substring(fromIndex until toIndex)
            if (digits.startsWith("0") && digits.length > 1) {
                // You cannot specify "0002", it has to be "2".
                throw ApplicationException(ErrorCode.INVALID_VERSION.withDetails("Invalid version digits: cannot start with a 0"))
            }
            return digits
        }
        return null
    }
}