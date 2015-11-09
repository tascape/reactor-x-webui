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
package com.tascape.qa.th.webui.example.webdriver.driver;

import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.webui.comm.WebBrowser;
import com.tascape.qa.th.webui.driver.WebPage;

/**
 *
 * @author linsong wang
 */
public class GoogleSearchUi extends EntityDriver {

    @Override
    public String getName() {
        return "Google Search UI";
    }

    public void search(String term) throws Exception {
        SearchPage searchPage = WebPage.getPage(SearchPage.class, this);
        searchPage.get();
        searchPage.submitSearch(term);
    }

    @Override
    public void reset() throws Exception {
        WebBrowser browser = WebBrowser.class.cast(this.getEntityCommunication());
        SearchPage searchPage = WebPage.getPage(SearchPage.class, this);
        searchPage.get();
    }
}
