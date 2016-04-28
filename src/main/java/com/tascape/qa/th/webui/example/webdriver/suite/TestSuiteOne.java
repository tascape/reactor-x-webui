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
package com.tascape.qa.th.webui.example.webdriver.suite;

import com.tascape.qa.th.webui.example.webdriver.driver.GoogleDotCom;
import com.tascape.qa.th.webui.example.webdriver.test.GoogleSearchTests;
import com.tascape.qa.th.webui.comm.WebBrowser;
import com.tascape.qa.th.suite.AbstractSuite;

/**
 *
 * @author linsong wang
 */
public class TestSuiteOne extends AbstractSuite {

    private WebBrowser browser;

    private final GoogleDotCom google = new GoogleDotCom();

    @Override
    public String getName() {
        return "Web Driver Test Suite Example One";
    }

    @Override
    public void setUpTestClasses() {
        this.addTestClass(GoogleSearchTests.class);
    }

    @Override
    public String getProductUnderTest() {
        return google.getName();
    }

    @Override
    protected void setUpEnvironment() throws Exception {
        browser = WebBrowser.newFirefox(false);
        browser.landscape();
        google.setWebBrowser(browser);
        google.launch("https://www.google.com");

        this.putTestDirver(GoogleSearchTests.GOOGLE_DOT_COM, google);
    }

    @Override
    protected void tearDownEnvironment() {
        if (browser != null) {
            browser.quit();
        }
    }
}
