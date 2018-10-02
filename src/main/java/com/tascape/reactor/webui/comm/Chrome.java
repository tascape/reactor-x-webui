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
import java.util.Arrays;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class Chrome extends WebBrowser {
    private static final Logger LOG = LoggerFactory.getLogger(Chrome.class);

    public static final String SYSPROP_DRIVER = "webdriver.chrome.driver";

    static {
        String driver = System.getProperty(SYSPROP_DRIVER);
        if (driver == null) {
            String driverFile = SystemUtils.IS_OS_WINDOWS ? "chromedriver.exe" : "chromedriver";
            File d = SystemConfiguration.HOME_PATH.resolve(DRIVER_DIRECTORY).resolve(driverFile).toFile();
            if (d.exists() && d.isFile()) {
                LOG.info("Use chromedriver at {}", d.getAbsolutePath());
                System.setProperty(SYSPROP_DRIVER, d.getAbsolutePath());
            } else {
                LOG.warn("Cannot find chromedriver file");
                downloadDriver(d);
            }
        } else {
            LOG.info("Use driver specified by system property {}={}", SYSPROP_DRIVER, driver);
        }
    }

    private static void downloadDriver(File driverFile) {
        LOG.info("download latest chromedriver");
        throw new RuntimeException("Cannot find chromedriver. Please set system property "
                + SYSPROP_DRIVER + ", or download chromedriver into directory " + driverFile.getParent()
                + ". Check download page http://chromedriver.storage.googleapis.com/index.html");
    }

    public Chrome() {
        System.setProperty("webdriver.chrome.logfile",
                super.getLogPath().getParent().resolve("chromedriver.log").toString());
        ChromeOptions options = new ChromeOptions();
        options.addArguments(Arrays.asList("start-maximized", "allow-running-insecure-content",
                "ignore-certificate-errors"));
        //options.addExtensions(new File("/path/to/extension.crx"));

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        super.initDesiredCapabilities(capabilities);
        super.setProxy(capabilities);
        super.setLogging(capabilities);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        super.setWebDriver(new ChromeDriver(options.merge(capabilities)));
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
