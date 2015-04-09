package com.tascape.qa.example.selenium.ide.driver;

import com.tascape.qa.th.driver.EntityDriver;

/**
 *
 * @author linsong wang
 */
public class SomeWebApp extends EntityDriver {

    public static final String SYSPROP_URL = "qa.driver.somewebapp.url";

    private String url;

    @Override
    public String getName() {
        return "Some Web App";
    }

    @Override
    public void reset() throws Exception {
        // do nothing
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
