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

import com.tascape.reactor.Utils;
import com.tascape.reactor.driver.EntityDriver;
import com.tascape.reactor.exception.EntityCommunicationException;
import com.tascape.reactor.webui.comm.WebBrowser;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
@SuppressWarnings("ProtectedField")
public abstract class WebApp extends EntityDriver {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WebApp.class);

    protected WebBrowser webBrowser;

    protected String version;

    private String baseUrl;

    private final Map<Class<? extends WebPage>, WebPage> loadedPages = new HashMap<>();

    /**
     * Loads a URL to open a new page.
     *
     * @param <T>       page type
     * @param pageClass page class
     *
     * @return page instance
     *
     * @throws EntityCommunicationException anything goes wrong
     */
    public <T extends WebPage> T open(Class<T> pageClass) throws EntityCommunicationException {
        WebPage page = loadedPages.get(pageClass);
        if (page == null) {
            page = PageFactory.initElements(webBrowser.getWebDriver(), pageClass);
            page.setApp(this);
        }
        page.load();
        page.get();
        loadedPages.put(pageClass, page);
        return pageClass.cast(page);
    }

    /**
     * Claims the current page, and casts it into expected page. Timeout in 15 seconds.
     *
     * @param <T>       page type
     * @param pageClass page class
     *
     * @return expected page instance
     *
     * @throws NotExpectedPageException a runtime exception
     */
    public <T extends WebPage> T claim(Class<T> pageClass) {
        return claim(pageClass, 15000);
    }

    /**
     * Claims the current page, and casts it into expected page.
     *
     * @param <T>           page type
     * @param pageClass     page class
     * @param timeoutMillis timeout in millisecond
     *
     * @return expected page instance
     *
     * @throws NotExpectedPageException a runtime exception
     */
    public <T extends WebPage> T claim(Class<T> pageClass, long timeoutMillis) {
        WebPage page = loadedPages.get(pageClass);
        if (page == null) {
            page = PageFactory.initElements(webBrowser.getWebDriver(), pageClass);
            page.setApp(this);
            loadedPages.put(pageClass, page);
        }
        long end = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < end) {
            try {
                page.hasLoaded();
                return pageClass.cast(page);
            } catch (Throwable t) {
                try {
                    webBrowser.takeBrowserScreenshot();
                } catch (IOException ex) {
                    LOG.warn("{}", ex.getMessage());
                }
                LOG.debug("{}, retry", t.getMessage());
            }
        }
        throw new NotExpectedPageException(page.getPath());
    }

    /**
     * Clicks on an element to open a new page.
     *
     * @param <T>       page type
     * @param element   element to click on
     * @param pageClass page class
     *
     * @return page instance
     *
     * @throws EntityCommunicationException anything goes wrong
     */
    public <T extends WebPage> T open(WebElement element, Class<T> pageClass) throws EntityCommunicationException {
        WebPage page = loadedPages.get(pageClass);
        if (page == null) {
            page = PageFactory.initElements(webBrowser.getWebDriver(), pageClass);
            page.setApp(this);
            loadedPages.put(pageClass, page);
        }
        page.load(element);
        return pageClass.cast(page);
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Launches web app with base URL.
     *
     * @throws InterruptedException in case of error
     */
    public void launch() throws InterruptedException {
        webBrowser.get(this.getBaseUrl());
        Utils.sleep(this.getLaunchDelayMillis(), "Wait for app launch");
    }

    public WebBrowser getWebBrowser() {
        return webBrowser;
    }

    public BrowserMobProxy getBrowserProxy() {
        return webBrowser.getBrowserProxy();
    }

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
        this.webBrowser.setDriver(this);
    }

    public abstract int getLaunchDelayMillis();

    public File takeBrowserScreenshot() {
        try {
            return webBrowser.takeBrowserScreenshot();
        } catch (IOException ex) {
            LOG.warn("Cannot take browser screenshot {}", ex.getMessage());
        }
        return null;
    }

    /**
     * Throws UnsupportedOperationException.
     *
     * @param timeoutMinutes
     */
    public void interactManually(int timeoutMinutes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
