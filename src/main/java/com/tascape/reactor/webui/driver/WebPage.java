/*
 * Copyright (c) 2015 - present Nebula Bay.
 * All rights reserved.
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
package com.tascape.reactor.webui.driver;

import com.tascape.reactor.webui.comm.WebBrowser;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
@SuppressWarnings("ProtectedField")
public abstract class WebPage extends LoadableComponent<WebPage> {
    private static final Logger LOG = LoggerFactory.getLogger(WebPage.class);

    protected WebApp app;

    protected WebBrowser webBrowser;

    @CacheLookup
    @FindBy(tagName = "body")
    protected WebElement body;

    /**
     * Gets the path of this page, comparing to web app base URL.
     *
     * @return such as /photos/edit.html
     */
    public abstract String getPath();

    void setApp(WebApp app) {
        this.app = app;
        this.webBrowser = app.getWebBrowser();
    }

    @Override
    protected void load() {
        String url = app.getBaseUrl() + this.getPath();
        LOG.debug("load page {}", url);
        webBrowser.get(url);
    }

    protected void load(WebElement element) {
        LOG.trace("load page by clicking {}", element);
        webBrowser.click(element);
        isLoaded();
    }

    public void hasLoaded() throws Error {
        isLoaded();
    }

    public void refresh() {
        webBrowser.navigate().refresh();
    }

    /**
     * Shows a fragment of a web page.
     *
     * @param <T>           fragment type
     * @param fragmentClass fragment class
     *
     * @return fragment instance
     */
    public <T extends WebFragment> T show(Class<T> fragmentClass) {
        return this.show(null, fragmentClass);
    }

    /**
     * Shows a fragment of a web page.
     *
     * @param <T>           fragment type
     * @param element       web element to click
     * @param fragmentClass fragment class
     *
     * @return fragment instance
     */
    public <T extends WebFragment> T show(WebElement element, Class<T> fragmentClass) {
        if (element != null) {
            webBrowser.click(element);
        }
        WebFragment fragment = PageFactory.initElements(webBrowser.getWebDriver(), fragmentClass);
        fragment.setPage(this);
        fragment.load();
        return fragmentClass.cast(fragment);
    }

    public WebApp getApp() {
        return app;
    }
    
    public void takeBrowserScreenshot() {
        app.takeBrowserScreenshot();
    }
}
