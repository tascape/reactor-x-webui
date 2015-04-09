package com.tascape.qa.example.selenium.perf.driver;

import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.comm.WebBrowser;

/**
 *
 * @author linsong wang
 */
public class GithubUi extends EntityDriver {

    @Override
    public String getName() {
        return "Github UI";
    }

    @Override
    public void reset() throws Exception {
        WebBrowser browser = WebBrowser.class.cast(this.getEntityCommunication());
        browser.get("https://github.com/");
    }
}
