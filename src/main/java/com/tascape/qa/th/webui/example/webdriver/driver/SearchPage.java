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
package com.tascape.qa.th.webui.example.webdriver.driver;

import com.tascape.qa.th.webui.driver.Page;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author linsong wang
 */
public class SearchPage extends Page {

    @CacheLookup
    @FindBy(id = "lst-ib")
    private WebElement searchBox;

    public void submitSearch(String term) {
        this.searchBox.clear();
        this.searchBox.sendKeys(term);
        this.searchBox.submit();
    }

    @Override
    protected void load() {
        this.webBrowser.get("http://google.com");
    }

    @Override
    protected void isLoaded() throws Error {
        String url = this.webBrowser.getCurrentUrl();
        assertTrue("Not on the expected page: " + url, url.contains("google.com"));
    }
}
