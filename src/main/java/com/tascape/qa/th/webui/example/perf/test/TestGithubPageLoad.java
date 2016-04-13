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
package com.tascape.qa.th.webui.example.perf.test;

import com.tascape.qa.th.webui.example.perf.driver.GithubUi;
import com.tascape.qa.th.webui.comm.WebBrowser;
import com.tascape.qa.th.driver.TestDriver;
import com.tascape.qa.th.test.JUnit4Test;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class TestGithubPageLoad extends JUnit4Test {
    private static final Logger LOG = LoggerFactory.getLogger(TestGithubPageLoad.class);

    public static final TestDriver DRIVER_GITHUB = new TestDriver(TestGithubPageLoad.class, GithubUi.class);

    private final GithubUi github;

    public TestGithubPageLoad() {
        this.github = this.getEntityDriver(DRIVER_GITHUB);
    }

    @Test
    public void testPageLoad() throws Exception {
        String url = "https://github.com/tascape/testharness";
        WebBrowser wb = WebBrowser.class.cast(this.github.getEntityCommunication());
        int ms = wb.getPageLoadTimeMillis(url);
        LOG.info("page load time {} ms", ms);
    }
}
