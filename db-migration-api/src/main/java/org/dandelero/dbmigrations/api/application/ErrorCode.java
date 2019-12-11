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
package org.dandelero.dbmigrations.api.application;

/**
 * Stores all the error codes used by the application.
 * <br />
 * Created at: 31/10/19 7:47 am
 *
 * @author dandelero
 */
public enum ErrorCode {

    // Structural.
    RESOURCE_ERROR(1000, "Resource error"),
    MISSING_RESOURCE(1001, "No resource found"),

    // Version.
    INVALID_VERSION(2000, "Invalid version"),
    VERSION_DIRECTORY_ERROR(2001, "Invalid version directory"),

    // Script.
    NO_SCRIPTS_FOUND(3000, "No scripts available/found"),
    INVALID_SCRIPTS_DETECTED(3001, "Unsupported or invalid scripts detected"),
    INVALID_SCRIPT_SEQUENCE(3002, "Invalid script sequence numbers detected"),

    // Module
    INVALID_MODULE(4000, "Invalid module"),
    MODULE_DIRECTORY_ERROR(4001, "Invalid module directory"),

    // Other
    OTHER(9999, "Other error");


    /**
     * The error code.
     */
    private int code;

    /**
     * A descriptive error message.
     */
    private String message;

    /**
     * A detailed error message.
     */
    private String details;

    /**
     * Creates an instance with the code and message.
     *
     * @param code    the error code.
     * @param message the message.
     */
    private ErrorCode(int code, String message) {
        this(code, message, null);
    }

    /**
     * Creates an instance with the code, message and details.
     *
     * @param code    the error code.
     * @param message the message.
     * @param details details around the error.
     */
    private ErrorCode(int code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    /**
     * @return the error code.
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the error message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return any details around the error message; can be null.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the error code details.
     *
     * @param details the details.
     * @return this instance.
     */
    public ErrorCode withDetails(String details) {
        this.details = details;
        return this;
    }
}
