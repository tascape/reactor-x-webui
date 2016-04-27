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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tascape.qa.th.Utils;
import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.exception.EntityCommunicationException;
import com.tascape.qa.th.webui.comm.WebBrowser;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
@SuppressWarnings("ProtectedField")
public abstract class App extends EntityDriver {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(App.class);

    protected WebBrowser webBrowser;

    public abstract int getLaunchDelayMillis();

    private static final Table<Class<? extends Page>, App, Page> PAGES = HashBasedTable.create();

    public static synchronized <T extends Page> T getPage(Class<T> pageClass, App app)
        throws EntityCommunicationException {
        Page pageLoaded = PAGES.get(pageClass, app);
        if (pageLoaded != null) {
            return pageClass.cast(pageLoaded);
        }
        T page = PageFactory.initElements(app.getWebBrowser().getWebDriver(), pageClass);
        page.setApp(app);
        PAGES.put(pageClass, app, page);
        return page;
    }

    protected String version;

    public void launch(String url) throws Exception {
        webBrowser.close();
        webBrowser.get(url);
        Utils.sleep(this.getLaunchDelayMillis(), "Wait for app launch");
    }

    public WebBrowser getWebBrowser() {
        return webBrowser;
    }

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

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
            throw new UIAException("Cannot take screenshot", ex);
        }
    }
}
