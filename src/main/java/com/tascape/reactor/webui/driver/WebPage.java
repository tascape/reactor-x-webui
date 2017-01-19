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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.LoadableComponent;
import org.openqa.selenium.support.ui.Select;
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
        element.click();
        isLoaded();
    }

    public void hasLoaded() throws Error {
        isLoaded();
    }

    public void refresh() {
        webBrowser.navigate().refresh();
    }

    public void setSelect(WebElement select, String visibleText) {
        if (null == visibleText) {
            return;
        }
        Select s = webBrowser.castAsSelect(select);
        s.selectByVisibleText(visibleText);
    }

    public void setSelect(By by, String visibleText) {
        setSelect(webBrowser.findElement(by), visibleText);
    }

    public String getSelect(By by) {
        Select s = webBrowser.castAsSelect(webBrowser.findElement(by));
        return s.getFirstSelectedOption().getText();
    }

    public void highlight(WebElement element) {
        this.webBrowser.highlight(element);
    }

    public WebApp getApp() {
        return app;
    }
}
