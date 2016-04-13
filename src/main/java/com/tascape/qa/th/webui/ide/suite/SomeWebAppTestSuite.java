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
package com.tascape.qa.th.webui.ide.suite;

import com.tascape.qa.th.webui.ide.driver.SomeWebApp;
import com.tascape.qa.th.webui.ide.test.SomeWebAppSeleniumIdeTests;
import com.tascape.qa.th.webui.suite.SeleniumIdeSuite;
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
