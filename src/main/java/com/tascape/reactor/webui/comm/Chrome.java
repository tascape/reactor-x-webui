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
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.SystemUtils;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class Chrome extends WebBrowser {

    public static final String SYSPROP_DRIVER = "webdriver.chrome.driver";

    public static final String SYSPROP_OPTIONS = "webdriver.chrome.options";

    private static final Logger LOG = LoggerFactory.getLogger(Chrome.class);

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

    public Chrome() throws InterruptedException {
        String logFile = "chromedriver-" + sysConfig.getHostName() + "-" + Thread.currentThread().getName() + "-" + UUID.randomUUID() + ".log"
            .replaceAll("[^a-zA-Z0-9\\.\\-]", "-");
        System.setProperty("webdriver.chrome.logfile",
            super.getLogPath().getParent().resolve(logFile).toString());
        ChromeOptions options = this.getChromeOptions()
            .setAcceptInsecureCerts(true)
            .setPageLoadStrategy(PageLoadStrategy.NORMAL)
            .setHeadless(super.isHeadless());
        super.setProxy(options);
        super.setLogging(options);
        super.setWebDriver(new ChromeDriver(options));
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

    private ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        Set<String> optionStrings = Set.of(
            "allow-running-insecure-content",
            "disable-dev-shm-usage",
            "disable-popup-blocking",
            "disable-infobars",
            "ignore-certificate-errors",
            "no-sandbox",
            "start-maximized");
        String[] customeOptions = sysConfig.getProperty(Chrome.SYSPROP_OPTIONS, "").split(",");
        if (customeOptions.length == 0) {
            LOG.debug("you can specify comma-delimited chrome options with system property {}", Chrome.SYSPROP_OPTIONS);
        }
        optionStrings.addAll(Arrays.asList(customeOptions));
        optionStrings.forEach(o -> {
            LOG.debug("Chrome option {}", optionStrings);
        });
        options.addArguments(optionStrings.toArray(new String[0]));
        //options.addExtensions(new File("/path/to/extension.crx"));
        return options;
    }
}
