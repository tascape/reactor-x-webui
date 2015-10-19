package com.tascape.qa.example.selenium.ide.test;

import com.tascape.qa.example.selenium.ide.driver.SomeWebApp;
import com.tascape.qa.example.selenium.ide.test.data.SomeWebAppSeleniumIdeHtmlFiles;
import com.tascape.qa.th.data.TestDataProvider;
import com.tascape.qa.th.driver.TestDriver;
import com.tascape.qa.th.test.SeleniumIdeTests;
import java.io.File;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author linsong wang
 */
public class SomeWebAppSeleniumIdeTests extends SeleniumIdeTests {
    private static final Logger LOG = LoggerFactory.getLogger(SomeWebAppSeleniumIdeTests.class);

    public static final TestDriver DRIVER_SOME_WEB_APP = new TestDriver(SomeWebAppSeleniumIdeTests.class,
        SomeWebApp.class);

    private final SomeWebApp someWebApp;

    public SomeWebAppSeleniumIdeTests() throws Exception {
        this.someWebApp = this.getEntityDriver(DRIVER_SOME_WEB_APP);
    }

    @Test
    @TestDataProvider(klass = SomeWebAppSeleniumIdeHtmlFiles.class, method = "getIdeHtmlFilesFeatureOne")
    public void testFeatureOne() throws Exception {
        SomeWebAppSeleniumIdeHtmlFiles html = this.getTestData(SomeWebAppSeleniumIdeHtmlFiles.class);
        File htmlFile = html.getTestCaseHtmlFile();
        LOG.info("Test feature one - {}", htmlFile.getName());
        boolean pf = this.runSeleniumIdeFirefox(htmlFile, someWebApp.getUrl());
        assertTrue("Fail running test case html file: " + htmlFile, pf);
    }

    @Test
    @TestDataProvider(klass = SomeWebAppSeleniumIdeHtmlFiles.class, method = "getIdeHtmlFilesFeatureTwo")
    public void testFeatureTwo() throws Exception {
        SomeWebAppSeleniumIdeHtmlFiles html = this.getTestData(SomeWebAppSeleniumIdeHtmlFiles.class);
        File htmlFile = html.getTestCaseHtmlFile();
        LOG.info("Test feature two - {}", htmlFile.getName());
        boolean pf = this.runSeleniumIdeFirefox(htmlFile, someWebApp.getUrl());
        assertTrue("Fail running test case html file: " + htmlFile, pf);
    }

    @Override
    public String getApplicationUnderTest() {
        return this.someWebApp.getName();
    }
}
