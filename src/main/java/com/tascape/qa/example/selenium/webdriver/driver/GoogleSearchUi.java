package com.tascape.qa.example.selenium.webdriver.driver;

import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.comm.WebBrowser;
import com.tascape.qa.th.driver.WebPage;

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
