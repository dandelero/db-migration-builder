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
 * Main exception class for the application.
 * <br />
 * Created at: 31/10/19 7:47 am
 *
 * @author dandelero
 */
public class ApplicationException extends Exception {

    /**
     * The application error code.
     */
    private ErrorCode code;

    /**
     * Constructs an exception instance.
     *
     * @param errorCode the application error code.
     * @param cause     the cause of this exception.
     */
    public ApplicationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode;
    }

    /**
     * Constructs an exception instance.
     *
     * @param errorCode the application error code.
     */
    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode;
    }

    /**
     * @return the application error code.
     */
    public int getCode() {
        return code.getCode();
    }

    /**
     * @return the application error details.
     */
    public String getDetails() {
        return code.getDetails();
    }

}
