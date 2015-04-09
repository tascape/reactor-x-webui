package com.tascape.qa.th.suite;

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

    private static final SeleniumServer seleniumServer;

    static {
        RemoteControlConfiguration rcc = new RemoteControlConfiguration();
        rcc.setTrustAllSSLCertificates(true);
        try {
            seleniumServer = new SeleniumServer(false, rcc);
            LOG.info("Start Selenium server");
            seleniumServer.start();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot start Selenium server", ex);
        }
        seleniumServer.getServer().setStopAtShutdown(true);
    }

    public static SeleniumServer getSeleniumServer() {
        return seleniumServer;
    }
}
