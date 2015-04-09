package com.tascape.qa.example.selenium.webdriver.suite;

import com.tascape.qa.example.selenium.webdriver.driver.GoogleSearchUi;
import com.tascape.qa.example.selenium.webdriver.test.TestClassGoogleSearch;
import com.tascape.qa.th.comm.WebBrowser;
import com.tascape.qa.th.suite.AbstractSuite;

/**
 *
 * @author linsong wang
 */
public class TestSuiteOne extends AbstractSuite {

    private WebBrowser browser;

    @Override
    public String getName() {
        return "Web Driver Test Suite Example One";
    }

    @Override
    public void setUpTestClasses() {
        this.addTestClass(TestClassGoogleSearch.class);
    }

    @Override
    protected void setUpEnvironment() throws Exception {
        browser = WebBrowser.newBrowser(false);
        browser.landscape();
        GoogleSearchUi search = new GoogleSearchUi();
        search.setEntityCommunication(browser);

        this.putDirver(TestClassGoogleSearch.class, TestClassGoogleSearch.SEARCH_UI, search);
    }

    @Override
    protected void tearDownEnvironment() {
        if (browser != null) {
            browser.quit();
        }
    }
}
