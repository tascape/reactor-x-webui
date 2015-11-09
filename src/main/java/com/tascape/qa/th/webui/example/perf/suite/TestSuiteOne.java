/*
 * Copyright 2015.
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
package com.tascape.qa.th.webui.example.perf.suite;

import com.tascape.qa.th.webui.example.perf.driver.GithubUi;
import com.tascape.qa.th.webui.example.perf.test.TestGithubPageLoad;
import com.tascape.qa.th.webui.comm.Firefox;
import com.tascape.qa.th.webui.comm.WebBrowser;
import com.tascape.qa.th.suite.AbstractSuite;

/**
 *
 * @author linsong wang
 */
public class TestSuiteOne extends AbstractSuite {

    private Firefox firefox;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setUpTestClasses() {
        this.addTestClass(TestGithubPageLoad.class);
    }

    @Override
    public String getProductUnderTest() {
        return "na";
    }

    @Override
    protected void setUpEnvironment() throws Exception {
        firefox = WebBrowser.newFirefox(true);
        firefox.landscape();
        GithubUi github = new GithubUi();
        github.setEntityCommunication(firefox);

        this.putTestDirver(TestGithubPageLoad.DRIVER_GITHUB, github);
    }

    @Override
    protected void tearDownEnvironment() {
        if (firefox != null) {
            firefox.quit();
        }
    }
}
