package com.tascape.qa.example.selenium.webdriver.driver;

import com.tascape.qa.th.driver.WebPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author linsong wang
 */
public class SearchPage extends WebPage {

    @CacheLookup
    @FindBy(id = "gbqfq")
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
