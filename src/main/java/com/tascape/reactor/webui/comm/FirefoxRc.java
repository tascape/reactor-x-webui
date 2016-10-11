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

import com.thoughtworks.selenium.DefaultSelenium;
import java.io.File;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class FirefoxRc extends WebBrowserRc {
    private static final Logger LOG = LoggerFactory.getLogger(FirefoxRc.class);

    public static final String SYSPROP_FF_PROFILE_FOLDER = "reactor.comm.FF_PROFILE_FOLDER";

    public static final String DEFAULT_FF_PROFILE_FOLDER = "~/.firefox/profile/default";

    @Override
    public SeleniumServer initServer() {
        RemoteControlConfiguration rcc = new RemoteControlConfiguration();
        rcc.setTrustAllSSLCertificates(true);
        rcc.setDebugMode(false);
        rcc.setPort(this.port);
        rcc.setBrowserTimeoutInMs(60000);
        rcc.setTimeoutInSeconds(60);
        String folder = sysConfig.getProperty(SYSPROP_FF_PROFILE_FOLDER, DEFAULT_FF_PROFILE_FOLDER);
        File profile = new File(folder);
        if (profile.exists()) {
            LOG.info("Using firefox profile template {}", profile.getAbsolutePath());
            rcc.setFirefoxProfileTemplate(profile);
        }
        try {
            SeleniumServer seleniumServer = new SeleniumServer(rcc);
            LOG.info("Start Selenium remote control server");
            seleniumServer.start();
            return seleniumServer;
        } catch (Exception ex) {
            throw new RuntimeException("Cannot start Selenium remote control server", ex);
        }
    }

    @Override
    public DefaultSelenium initBrowser(String url) {
        String exe = sysConfig.getProperty(Firefox.SYSPROP_FF_BINARY);
        String ff = "*firefox";
        if (exe != null) {
            LOG.info("Use Firefox executable {}", exe);
            ff += " " + exe;
        } else {
            LOG.info("Use default Firefox executable");
        }
        return new DefaultSelenium("localhost", this.port, ff, url);
    }
}
