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
package com.tascape.reactor.webui.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class DefaultWebApp extends WebApp {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebApp.class);

    public static final String URL = "http://127.0.0.1:28088/rr/";

    @Override
    public String getName() {
        return URL;
    }

    @Override
    public String getVersion() {
        return "1.2.2";
    }

    @Override
    public void reset() throws Exception {
        LOG.debug("nothing");
    }

    @Override
    public int getLaunchDelayMillis() {
        return 5000;
    }
}
