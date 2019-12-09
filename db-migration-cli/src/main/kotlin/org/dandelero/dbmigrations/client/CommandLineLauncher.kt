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
package org.dandelero.dbmigrations.client

/**
 * Launches the application from command-line, with all the necessary parameters.
 * <br />
 * Created at: 10/11/19 12:43 pm
 * @author dandelero
 */
object CommandLineLauncher {

    @JvmStatic
    fun main(args: Array<String>) {
        CommandLineRunner(args).run()
    }
}