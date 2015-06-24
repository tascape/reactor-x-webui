package com.tascape.qa.example.selenium.perf.test;

import com.tascape.qa.example.selenium.perf.driver.GithubUi;
import com.tascape.qa.th.comm.WebBrowser;
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

    public static final TestDriver DRIVER_GITHUB = new TestDriver(TestGithubPageLoad.class, "DRIVER_GITHUB");

    private final GithubUi github;

    public TestGithubPageLoad() {
        this.github = this.getEntityDriver(DRIVER_GITHUB, GithubUi.class);
    }

    @Test
    public void testPageLoad() throws Exception {
        String url = "https://github.com/tascape/testharness";
        WebBrowser wb = WebBrowser.class.cast(this.github.getEntityCommunication());
        int ms = wb.getPageLoadTimeMillis(url);
        LOG.info("page load time {} ms", ms);
    }
}
