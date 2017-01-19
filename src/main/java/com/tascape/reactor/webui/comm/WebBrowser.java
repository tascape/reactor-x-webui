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
package com.tascape.reactor.webui.comm;

import com.beust.jcommander.internal.Lists;
import com.tascape.reactor.SystemConfiguration;
import com.tascape.reactor.Utils;
import com.tascape.reactor.comm.EntityCommunication;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public abstract class WebBrowser extends EntityCommunication implements WebDriver {
    private static final Logger LOG = LoggerFactory.getLogger(WebBrowser.class);

    public static final String SYSPROP_WEBBROWSER_TYPE = "reactor.comm.WEBBROWSER_TYPE";

    public static final String SYSPROP_WEBBROWSER_USE_PROXY = "reactor.comm.WEBBROWSER_USE_PROXY";

    public static final String SYSPROP_WEBBROWSER_CLICK_DELAY = "reactor.comm.WEBBROWSER_CLICK_DELAY";

    public static final int AJAX_TIMEOUT_SECONDS = 60;

    public static final int WIDTH = 1920;

    public static final int HEIGHT = 1080;

    private WebDriver webDriver;

    private Actions actions;

    protected final BrowserMobProxy browserProxy;

    private final int clickDelayMillis;

    protected WebBrowser() {
        if (sysConfig.getBooleanProperty(SYSPROP_WEBBROWSER_USE_PROXY, false)) {
            browserProxy = new BrowserMobProxyServer();
            browserProxy.setTrustAllServers(true);
            browserProxy.start();
        } else {
            browserProxy = null;
        }
        clickDelayMillis = sysConfig.getIntProperty(SYSPROP_WEBBROWSER_CLICK_DELAY, 200);
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
        setDefaultTimeouts();
    }

    protected void setProxy(DesiredCapabilities capabilities) {
        if (browserProxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(browserProxy));
        }
    }

    public static WebBrowser newBrowser(boolean devToolsEnabled) throws Exception {
        List<String> sbs = Lists.newArrayList(BrowserType.FIREFOX, BrowserType.CHROME);
        String type = SystemConfiguration.getInstance().getProperty(SYSPROP_WEBBROWSER_TYPE);
        if (type == null) {
            throw new RuntimeException("System property " + SYSPROP_WEBBROWSER_TYPE + " is not specified. "
                + sbs + " are supported.");
        }
        switch (type) {
            case BrowserType.FIREFOX:
                return newFirefox(devToolsEnabled);
            case BrowserType.CHROME:
                return newChrome(devToolsEnabled);
        }
        throw new RuntimeException("System property " + SYSPROP_WEBBROWSER_TYPE + "=" + type
            + " is not supported. Only " + sbs + " are supported currently.");
    }

    public static Chrome newChrome(boolean devToolsEnabled) {
        return new Chrome();
    }

    public static Firefox newFirefox(boolean devToolsEnabled) throws Exception {
        return new Firefox(devToolsEnabled);
    }

    @Override
    public void connect() throws IOException {
    }

    @Override
    public void disconnect() throws IOException {
        this.webDriver.quit();
    }

    @Override
    public void get(String url) {
        LOG.debug("Open url {}", url);
        this.webDriver.get(url);
    }

    public File takeBrowserScreenshot() throws IOException {
        File ss = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        File f = this.getLogPath().resolve(ss.getName()).toFile();
        LOG.debug("Screenshot {}", f.getAbsolutePath());
        FileUtils.moveFile(ss, f);
        return ss;
    }

    public BrowserMobProxy getBrowserProxy() {
        return browserProxy;
    }

    /**
     * Clicks on a web element. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_CLICK_DELAY.
     *
     * @param webElement target web element
     */
    public void click(WebElement webElement) {
        try {
            Thread.sleep(clickDelayMillis);
        } catch (InterruptedException ex) {
            LOG.trace("{}", ex.getMessage());
        }
        webElement.click();
    }

    /**
     * Clears input area with CTRL-A and DELETE.
     *
     * @param textBox text input element
     */
    public void clear(WebElement textBox) {
        textBox.clear();
        textBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        textBox.sendKeys(Keys.DELETE);
    }

    /**
     * Sets text into a text box, clears it first.
     *
     * @param textBox text input element
     * @param text    text to set
     */
    public void setText(WebElement textBox, String text) {
        this.clear(textBox);
        textBox.sendKeys(text);
    }

    /**
     * Sets a check box state.
     *
     * @param checkBox checkBox input element
     * @param checked  checked or not
     */
    public void setChecked(WebElement checkBox, boolean checked) {
        if (checkBox.isSelected() ^ checked) {
            checkBox.click();
        }
    }

    /**
     * Casts a WebElement as a HTML Select object.
     *
     * @param element target element
     *
     * @return HTML Select
     */
    public Select castAsSelect(WebElement element) {
        return new Select(element);
    }

    public abstract int getPageLoadTimeMillis(String url) throws Exception;

    public abstract int getAjaxLoadTimeMillis(Ajax ajax) throws Exception;

    @Override
    public String getCurrentUrl() {
        String url = this.webDriver.getCurrentUrl();
        LOG.debug("Current url is {}", url);
        return url;
    }

    @Override
    public String getTitle() {
        String title = this.webDriver.getTitle();
        LOG.debug("Title is {}", title);
        return title;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return this.webDriver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return this.webDriver.findElement(by);
    }

    @Override
    public String getPageSource() {
        String src = this.webDriver.getPageSource();
        LOG.debug("Page src length {}", src.length());
        return src;
    }

    @Override
    public void close() {
        LOG.debug("Close browser");
        this.webDriver.close();
    }

    @Override
    public void quit() {
        LOG.debug("Quit browser");
        this.webDriver.quit();
        if (browserProxy != null) {
            this.browserProxy.abort();
        }
    }

    @Override
    public Set<String> getWindowHandles() {
        return this.webDriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return this.webDriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return this.webDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return this.webDriver.navigate();
    }

    @Override
    public Options manage() {
        return this.webDriver.manage();
    }

    /**
     * @param <T>
     *               For an HTML element, this method returns a WebElement
     *               For a number, a Long is returned
     *               For a boolean, a Boolean is returned
     *               For all other cases, a String is returned.
     *               For an array, return a List of Object, with each object following the rules above.
     *               Unless the value is null or there is no return value, in which null is returned
     *
     * @param type   return type
     * @param script javascript
     * @param args   Arguments must be a number, a boolean, a String, a WebElement, or a List of any combination of the
     *               above. An exception will be thrown if the arguments do not meet these criteria. The arguments will
     *               be made available to the JavaScript via the "arguments" magic variable, as if the function were
     *               called via "Function.apply"
     *
     * @return primitive type or WebElement
     */
    public <T extends Object> T executeScript(Class<T> type, String script, Object... args) {
        if (this.webDriver instanceof JavascriptExecutor) {
            Object object = ((JavascriptExecutor) webDriver).executeScript(script, args);
            return type.cast(object);
        } else {
            LOG.warn("Cannot execute javascript");
            return null;
        }
    }

    public Object executeScript(String script, Object... args) {
        if (this.webDriver instanceof JavascriptExecutor) {
            return ((JavascriptExecutor) webDriver).executeScript(script, args);
        } else {
            LOG.warn("Cannot execute javascript");
            return null;
        }
    }

    public void landscape() {
        this.manage().window().setPosition(new Point(0, 0));
        this.manage().window().setSize(new Dimension(WIDTH, HEIGHT));
    }

    public void portrait() {
        this.manage().window().setPosition(new Point(0, 0));
        this.manage().window().setSize(new Dimension(HEIGHT, WIDTH));
    }

    public void hide() {
        this.manage().window().setPosition(new Point(WIDTH, WIDTH));
    }

    /**
     * Highlights an element with solid red 3px border.
     *
     * @param we target web element
     */
    public void highlight(WebElement we) {
        executeScript("arguments[0].style.border='3px solid red'", we);
    }

    public void scrollToTop() {
        executeScript("window.scrollTo(0, 0 - document.body.scrollHeight);");
    }

    public void scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public String getHtml(WebElement element) {
        return executeScript(String.class, "return arguments[0].innerHTML;", element);
    }

    public Actions getActions() {
        return actions;
    }

    /**
     * Takes multiple screen shots with different screen resolutions.
     *
     * @see ScreenResolution
     */
    public void takeBrowserScreenshots() {
        Stream.of(ScreenResolution.values()).forEach(sr -> {
            LOG.info("try screen resolution {} x {}", sr.width, sr.height);
            manage().window().setSize(new Dimension(sr.width, sr.height));
            try {
                Utils.sleep(1000, "");
                takeBrowserScreenshot();
            } catch (IOException | InterruptedException ex) {
                LOG.warn(ex.getMessage());
            }
        });
    }

    /**
     * Waits for a specific condition.
     *
     * @param condition expected condition
     * @param seconds   wait timeout
     *
     * @throws org.openqa.selenium.TimeoutException if the timeout expires.
     */
    public void waitFor(ExpectedCondition condition, int seconds) {
        WebDriverWait wait = new WebDriverWait(this, seconds);
        wait.until(condition);
    }

    /**
     * Waits for a specific element to appear.
     *
     * @param by      The locating mechanism
     * @param seconds wait timeout
     *
     * @return The first matching element on the current page
     *
     * @throws org.openqa.selenium.TimeoutException if the timeout expires.
     */
    public WebElement waitForElement(By by, int seconds) {
        LOG.debug("Wait for element {} to appear", by);
        WebDriverWait wait = new WebDriverWait(this, seconds);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitForNoElement(final By by, int seconds) {
        LOG.debug("Wait for element {} to disappear", by);
        WebDriverWait wait = new WebDriverWait(this, seconds);
        wait.until((WebDriver t) -> {
            List<WebElement> es = t.findElements(by);
            if (es.isEmpty()) {
                return true;
            } else {
                return es.stream().noneMatch((e) -> (!e.getCssValue("display").equals("none")));
            }
        });
    }

    public void setDefaultTimeouts() {
        this.actions = new Actions(this.webDriver);
        this.webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        this.webDriver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        this.webDriver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
    }

    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    public static interface Ajax {
        public long doRequest();

        public By getByAppear();

        public By getByDisapper();
    }

    public static abstract class AbstractAjax implements Ajax {
        @Override
        public By getByAppear() {
            return null;
        }

        @Override
        public By getByDisapper() {
            return null;
        }
    }
}
