package com.tascape.qa.example.selenium.perf.suite;

import com.tascape.qa.example.selenium.perf.driver.GithubUi;
import com.tascape.qa.example.selenium.perf.test.TestGithubPageLoad;
import com.tascape.qa.th.comm.Firefox;
import com.tascape.qa.th.comm.WebBrowser;
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
