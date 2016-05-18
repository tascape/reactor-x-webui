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
package com.tascape.qa.th.webui.driver;

import com.tascape.qa.th.Utils;
import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.exception.EntityCommunicationException;
import com.tascape.qa.th.webui.comm.WebBrowser;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
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

    private  final Map<Class<? extends WebPage>, WebPage> loadedPages = new HashMap<>();

    public <T extends WebPage> T open(Class<T> pageClass)
        throws EntityCommunicationException {
        WebPage page = loadedPages.get(pageClass);
        if (page == null) {
            page = PageFactory.initElements(webBrowser.getWebDriver(), pageClass);
            page.setApp(this);
            loadedPages.put(pageClass, page);
        }
        page.get();
        return pageClass.cast(page);
    }

    /**
     * Launches web app with base URL.
     *
     * @param baseUrl such as https://google.com
     *
     * @throws InterruptedException in case of error
     */
    public void launch(String baseUrl) throws InterruptedException {
        this.baseUrl = baseUrl;
        webBrowser.get(baseUrl);
        Utils.sleep(this.getLaunchDelayMillis(), "Wait for app launch");
    }
    
    public void relaunch() throws InterruptedException {
        webBrowser.get(baseUrl);
        Utils.sleep(this.getLaunchDelayMillis(), "Wait for app re-launch");        
    }

    public WebBrowser getWebBrowser() {
        return webBrowser;
    }

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public abstract int getLaunchDelayMillis();

    public File takeScreenshot() {
        long start = System.currentTimeMillis();
        LOG.debug("Take screenshot");
        try {
            File png = this.saveIntoFile("ss", "png", "");
            File f = webBrowser.takeBrowerScreenshot();
            FileUtils.copyFile(f, png);
            LOG.trace("time {} ms", System.currentTimeMillis() - start);
            return png;
        } catch (IOException ex) {
            throw new WebUiException("Cannot take screenshot", ex);
        }
    }
}
