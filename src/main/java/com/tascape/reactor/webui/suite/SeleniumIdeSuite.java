/*
 * Copyright 2015 - 2016 Nebula Bay.
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
package com.tascape.reactor.webui.suite;

import com.tascape.reactor.suite.AbstractSuite;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author linsong wang
 */
public abstract class SeleniumIdeSuite extends AbstractSuite {
    private static final Logger LOG = LoggerFactory.getLogger(SeleniumIdeSuite.class);

    private static final SeleniumServer SELENIUM_SERVER;

    static {
        RemoteControlConfiguration rcc = new RemoteControlConfiguration();
        rcc.setTrustAllSSLCertificates(true);
        try {
            SELENIUM_SERVER = new SeleniumServer(false, rcc);
            LOG.info("Start Selenium server");
            SELENIUM_SERVER.start();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot start Selenium server", ex);
        }
        SELENIUM_SERVER.getServer().setStopAtShutdown(true);
    }

    public static SeleniumServer getSeleniumServer() {
        return SELENIUM_SERVER;
    }
}
