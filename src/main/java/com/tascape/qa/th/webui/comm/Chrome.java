/*
 * Copyright 2016 tascape.
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
package com.tascape.qa.th.webui.comm;

import java.util.Arrays;
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

    public static String SYSPROP_CHROME_DRIVER = "webdriver.chrome.driver";

    public Chrome() {
        String chromeServer = System.getProperty(SYSPROP_CHROME_DRIVER);
        if (chromeServer == null) {
            throw new RuntimeException("Cannot find system property " + SYSPROP_CHROME_DRIVER);
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments(Arrays.asList("allow-running-insecure-content", "ignore-certificate-errors"));
        //options.addExtensions(new File("/path/to/extension.crx"));
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        super.setWebDriver(new ChromeDriver(capabilities));
    }

    @Override
    public int getPageLoadTimeMillis(String url) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getAjaxLoadTimeMillis(Ajax ajax) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
