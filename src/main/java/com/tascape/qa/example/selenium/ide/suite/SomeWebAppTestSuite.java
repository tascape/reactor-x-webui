package com.tascape.qa.example.selenium.ide.suite;

import com.tascape.qa.example.selenium.ide.driver.SomeWebApp;
import com.tascape.qa.example.selenium.ide.test.SomeWebAppSeleniumIdeTests;
import com.tascape.qa.th.suite.SeleniumIdeSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class SomeWebAppTestSuite extends SeleniumIdeSuite {
    private static final Logger LOG = LoggerFactory.getLogger(SomeWebAppTestSuite.class);

    @Override
    public String getName() {
        return "Selenium IDE Test Suite Example One";
    }

    @Override
    public void setUpTestClasses() {
        this.addTestClass(SomeWebAppSeleniumIdeTests.class);
    }

    @Override
    public String getProductUnderTest() {
        return "Some App";
    }

    @Override
    protected void setUpEnvironment() throws Exception {
        SomeWebApp app = new SomeWebApp();
        LOG.info("Check system property {}", SomeWebApp.SYSPROP_URL);
        app.setUrl(this.getSuiteProperty(SomeWebApp.SYSPROP_URL, "http://paypal.com"));

        this.putTestDirver(SomeWebAppSeleniumIdeTests.DRIVER_SOME_WEB_APP, app);
    }

    @Override
    protected void tearDownEnvironment() {
        // do nothing
    }
}
