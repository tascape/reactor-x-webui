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

import com.tascape.reactor.SystemConfiguration;
import java.io.File;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class Edge extends WebBrowser {
    private static final Logger LOG = LoggerFactory.getLogger(Edge.class);

    public static final String SYSPROP_DRIVER = "webdriver.edge.driver";

    static {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new RuntimeException("Cannot run Edge browser on non-Windows platforms.");
        }

        String driver = System.getProperty(SYSPROP_DRIVER);
        if (driver == null) {
            File d = SystemConfiguration.HOME_PATH.resolve(DRIVER_DIRECTORY).resolve("MicrosoftWebDriver.exe").toFile();
            if (d.exists() && d.isFile()) {
                LOG.info("Use MicrosoftWebDriver.exe at {}", d.getAbsolutePath());
                System.setProperty(SYSPROP_DRIVER, d.getAbsolutePath());
            } else {
                LOG.warn("Cannot find MicrosoftWebDriver.exe file");
                throw new RuntimeException("Cannot find MicrosoftWebDriver.exe. Please set system property "
                        + SYSPROP_DRIVER + ", or download MicrosoftWebDriver.exe into directory " + d.getParent()
                        + ". Check download page https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/");
            }
        } else {
            LOG.info("Use chromedriver specified by system property {}={}", SYSPROP_DRIVER, driver);
        }
    }

    public Edge() {
        System.setProperty("webdriver.edge.logfile",
                super.getLogPath().getParent().resolve("MicrosoftWebDriver.log").toString());
        //EdgeOptions options = new EdgeOptions();
        DesiredCapabilities capabilities = DesiredCapabilities.edge();
        super.initDesiredCapabilities(capabilities);
        super.setProxy(capabilities);
        super.setLogging(capabilities);
        super.setWebDriver(EdgeDriver.builder().oneOf(capabilities).build());
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
