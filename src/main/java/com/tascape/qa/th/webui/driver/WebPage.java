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
import com.tascape.qa.th.comm.EntityCommunication;
import com.tascape.qa.th.webui.comm.WebBrowser;
import com.tascape.qa.th.driver.EntityDriver;
import com.tascape.qa.th.exception.EntityCommunicationException;
import java.io.File;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
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

    protected WebBrowser webBrowser;

    private EntityDriver entityDriver;

    @CacheLookup
    @FindBy(tagName = "body")
    protected WebElement body;

    private static final Table<Class<? extends WebPage>, EntityDriver, WebPage> PAGES = HashBasedTable.create();

    public static synchronized <T extends WebPage> T getPage(Class<T> pageClass, EntityDriver entityDriver)
        throws EntityCommunicationException {
        WebPage pageLoaded = PAGES.get(pageClass, entityDriver);
        if (pageLoaded != null) {
            return pageClass.cast(pageLoaded);
        }
        EntityCommunication comm = entityDriver.getEntityCommunication();
        if (comm instanceof WebBrowser) {
            WebBrowser wb = WebBrowser.class.cast(comm);
            T page = PageFactory.initElements(wb.getWebDriver(), pageClass);
            page.setWebBrowser(wb);
            page.setEntityDriver(entityDriver);
            PAGES.put(pageClass, entityDriver, page);
            return page;
        }
        throw new EntityCommunicationException("Invalid communication type " + comm.toString());
    }

    public EntityDriver getEntityDriver() {
        return entityDriver;
    }

    public void setWebBrowser(WebBrowser webBrowser) {
        this.webBrowser = webBrowser;
    }

    public void setEntityDriver(EntityDriver entityDriver) {
        this.entityDriver = entityDriver;
    }

    protected File captureScreen() {
        return this.entityDriver.captureScreen();
    }

    public void setSelect(WebElement select, String visibleText) {
        if (null == visibleText) {
            return;
        }
        Select s = new Select(select);
        if (visibleText.isEmpty()) {
            s.selectByIndex(1);
        } else {
            s.selectByVisibleText(visibleText);
        }
    }

    public void setSelect(By by, String visibleText) {
        WebElement select = this.webBrowser.findElement(by);
        this.setSelect(select, visibleText);
    }

    public String getSelect(By by) {
        WebElement select = this.webBrowser.findElement(by);
        Select s = new Select(select);
        return s.getFirstSelectedOption().getText();
    }

    public void highlight(WebElement element) {
        this.webBrowser.executeScript(Void.class, "arguments[0].style.border='3px solid red';", element);
    }
}
