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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver
 *
 * @author linsong wang
 */
public class IE extends WebBrowser {
    private static final Logger LOG = LoggerFactory.getLogger(IE.class);

    public static final String SYSPROP_DRIVER = "webdriver.ie.driver";

    public static final String DRIVER_NAME = "IEDriverServer.exe";

    static {
        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new RuntimeException("Cannot run IE browser on non-Windows platforms.");
        }

        String driver = System.getProperty(SYSPROP_DRIVER);
        if (driver == null) {
            File d = SystemConfiguration.HOME_PATH.resolve(DRIVER_DIRECTORY).resolve(DRIVER_NAME).toFile();
            if (d.exists() && d.isFile()) {
                LOG.info("Use " + DRIVER_NAME + " at {}", d.getAbsolutePath());
                System.setProperty(SYSPROP_DRIVER, d.getAbsolutePath());
            } else {
                LOG.warn("Cannot find " + DRIVER_NAME + " file");
                throw new RuntimeException("Cannot find " + DRIVER_NAME + ". Please set system property "
                        + SYSPROP_DRIVER + ", or download " + DRIVER_NAME + " into directory " + d.getParent()
                        + ". Check download page http://selenium-release.storage.googleapis.com/index.html");
            }
        } else {
            LOG.info("Use driver specified by system property {}={}", SYSPROP_DRIVER, driver);
        }
    }

    public IE() {
        InternetExplorerOptions options = new InternetExplorerOptions();
        options.setCapability("ie.ensureCleanSession", true);
        options.setCapability("unexpectedAlertBehaviour", "accept");
        super.setProxy(options);
        super.setLogging(options);

        super.setWebDriver(new InternetExplorerDriver(options));
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

    @Override
    public void continueToThisWebsite() {
        LOG.info("click on '{}'", "Continue to this website (not recommended).");;
        try {
            get("javascript:document.getElementById('overridelink').click()");
        } catch (Exception ex) {
            LOG.warn(ex.getLocalizedMessage());
        }
    }
}
