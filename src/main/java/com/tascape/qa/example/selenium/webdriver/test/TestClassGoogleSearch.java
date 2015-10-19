package com.tascape.qa.example.selenium.webdriver.test;

import com.tascape.qa.example.selenium.webdriver.driver.GoogleSearchUi;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.data.TestDataProvider;
import com.tascape.qa.th.data.TestIterationData;
import com.tascape.qa.th.driver.TestDriver;
import com.tascape.qa.th.test.JUnit4Test;
import org.junit.Test;

/**
 *
 * @author linsong wang
 */
public class TestClassGoogleSearch extends JUnit4Test {

    public static final TestDriver SEARCH_UI = new TestDriver(TestClassGoogleSearch.class, GoogleSearchUi.class);

    private final GoogleSearchUi searchUi;

    public TestClassGoogleSearch() {
        this.searchUi = this.getEntityDriver(SEARCH_UI);
    }

    @Test
    @TestDataProvider(klass = TestIterationData.class, method = "useIterations", parameter = "4")
    public void testSearch() throws Exception {
        TestIterationData data = this.getTestData(TestIterationData.class);
        this.searchUi.search("test automation " + data.getIteration());
        Utils.sleep(5000, "wait");
    }
}
