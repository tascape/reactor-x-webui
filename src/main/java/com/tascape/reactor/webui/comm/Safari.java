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
package com.tascape.reactor.webui.comm;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://github.com/SeleniumHQ/selenium/issues/3796.
 *
 * @author linsong wang
 */
public class Safari extends WebBrowser {
    private static final Logger LOG = LoggerFactory.getLogger(Safari.class);

    public Safari() {
        SafariOptions options = new SafariOptions();
        DesiredCapabilities capabilities = DesiredCapabilities.safari();
        capabilities.setAcceptInsecureCerts(true);
        super.setProxy(capabilities);
        super.setWebDriver(new SafariDriver(options.merge(capabilities)));
    }

    /**
     * Not support yet.
     *
     * @param url NA
     *
     * @return NA
     *
     * @throws Exception NA
     */
    @Override
    public int getPageLoadTimeMillis(String url) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Not support yet.
     *
     * @param ajax NA
     *
     * @return NA
     *
     * @throws Exception NA
     */
    @Override
    public int getAjaxLoadTimeMillis(Ajax ajax) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
