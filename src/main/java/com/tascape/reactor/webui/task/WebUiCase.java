/*
 * Copyright (c) 2015 - present Nebula Bay.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tascape.reactor.webui.task;

import com.tascape.reactor.webui.driver.WebApp;

/**
 *
 * @author linsong wang
 */
public interface WebUiCase {

    default void runManually(WebApp app) throws Exception {
        runManually(app, 30);
    }

    /**
     * The method starts a GUI to let an user send requests to web service manually.
     * Please make sure to set timeout long enough for manual interaction.
     *
     * @param app            the web ui app instance used
     * @param timeoutMinutes timeout in minutes to fail the manual steps
     *
     * @throws Exception if case of error
     */
    default void runManually(WebApp app, int timeoutMinutes) throws Exception {
        app.interactManually(timeoutMinutes);
    }
}
