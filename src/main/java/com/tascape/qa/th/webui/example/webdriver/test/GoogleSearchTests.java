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
package com.tascape.qa.th.webui.example.webdriver.test;

import com.tascape.qa.th.webui.example.webdriver.driver.GoogleDotCom;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.data.TestDataProvider;
import com.tascape.qa.th.data.TestIterationData;
import com.tascape.qa.th.driver.TestDriver;
import com.tascape.qa.th.test.AbstractTest;
import org.junit.Test;

/**
 *
 * @author linsong wang
 */
public class GoogleSearchTests extends AbstractTest {

    public static final TestDriver GOOGLE_DOT_COM = new TestDriver(GoogleSearchTests.class, GoogleDotCom.class);

    private final GoogleDotCom google;

    public GoogleSearchTests() {
        this.google = super.getEntityDriver(GOOGLE_DOT_COM);
    }

    @Test
    @TestDataProvider(klass = TestIterationData.class, method = "useIterations", parameter = "4")
    public void testSearch() throws Exception {
        TestIterationData data = this.getTestData(TestIterationData.class);
        this.google.search("com.tascape.qa " + data.getIteration());
        Utils.sleep(5000, "wait");
        google.takeScreenshot();
    }

    @Override
    public String getApplicationUnderTest() {
        return google.getName();
    }
}
