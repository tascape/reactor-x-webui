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

import com.google.common.collect.Lists;
import com.tascape.reactor.SystemConfiguration;
import com.tascape.reactor.Utils;
import com.tascape.reactor.comm.EntityCommunication;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
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
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.logging.Logs;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

/**
 *
 * @author linsong wang
 */
public abstract class WebBrowser extends EntityCommunication implements WebDriver {
    private static final Logger LOG = LoggerFactory.getLogger(WebBrowser.class);

    public static final List<String> SUPPORTED_BROWSERS = Lists.newArrayList(
        BrowserType.FIREFOX,
        BrowserType.CHROME,
        BrowserType.SAFARI,
        BrowserType.IE,
        BrowserType.EDGE);

    public static final String DRIVER_DIRECTORY = "webui";

    public static final String SYSPROP_WEBBROWSER_TYPE = "reactor.comm.WEBBROWSER_TYPE";

    public static final String SYSPROP_WEBBROWSER_USE_PROXY = "reactor.comm.WEBBROWSER_USE_PROXY";

    public static final String SYSPROP_WEBBROWSER_INTERACTION_DELAY_MILLIS
        = "reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS";

    public static final int AJAX_TIMEOUT_SECONDS = 60;

    public static final int WIDTH = 1920;

    public static final int HEIGHT = 1080;

    public static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("HH.mm.ss.SSS");

    private WebDriver webDriver;

    private Actions actions;

    protected final BrowserMobProxy browserProxy;

    private final int interactionDelayMillis;

    protected WebBrowser() {
        if (sysConfig.getBooleanProperty(SYSPROP_WEBBROWSER_USE_PROXY, false)) {
            browserProxy = new BrowserMobProxyServer();
            browserProxy.setTrustAllServers(true);
            browserProxy.start();
        } else {
            browserProxy = null;
        }
        interactionDelayMillis = sysConfig.getIntProperty(SYSPROP_WEBBROWSER_INTERACTION_DELAY_MILLIS, 0);
    }

    public WebBrowser setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.actions = new Actions(this.webDriver);
        return this;
    }

    protected WebBrowser setProxy(DesiredCapabilities capabilities) {
        if (browserProxy != null) {
            capabilities.setCapability(CapabilityType.PROXY, ClientUtil.createSeleniumProxy(browserProxy));
        }
        return this;
    }

    protected WebBrowser initDesiredCapabilities(DesiredCapabilities capabilities) {
        capabilities.setJavascriptEnabled(true);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setAcceptInsecureCerts(true);
        return this;
    }

    protected WebBrowser setLogging(DesiredCapabilities capabilities) {
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.CLIENT, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        logs.enable(LogType.PERFORMANCE, Level.ALL);
        logs.enable(LogType.PROFILER, Level.ALL);
        logs.enable(LogType.SERVER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);
        return this;
    }

    public static WebBrowser newBrowser(boolean devToolsEnabled) throws Exception {
        String type = SystemConfiguration.getInstance().getProperty(SYSPROP_WEBBROWSER_TYPE);
        if (type == null) {
            throw new RuntimeException("System property " + SYSPROP_WEBBROWSER_TYPE + " is not specified. "
                + SUPPORTED_BROWSERS + " are supported.");
        }
        String[] types = type.split("\\|");
        switch (types[RandomUtils.nextInt() % types.length]) {
            case BrowserType.FIREFOX:
                return newFirefox(devToolsEnabled);
            case BrowserType.CHROME:
                return newChrome(devToolsEnabled);
            case BrowserType.IE:
                return newIE(devToolsEnabled);
            case BrowserType.EDGE:
                return newEdge(devToolsEnabled);
            case BrowserType.SAFARI:
                throw new UnsupportedOperationException(
                    "Safari webdriver is having issues, please check https://github.com/SeleniumHQ/selenium/issues/3796");
//                return newSafari(devToolsEnabled);
        }
        throw new RuntimeException("System property " + SYSPROP_WEBBROWSER_TYPE + "=" + type
            + " is not supported. Only " + SUPPORTED_BROWSERS + " are supported currently.");
    }

    public static Chrome newChrome(boolean devToolsEnabled) throws Exception {
        try {
            return new Chrome();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            Thread.sleep(1000);
            return new Chrome();
        }
    }

    public static Firefox newFirefox(boolean devToolsEnabled) throws Exception {
        try {
            return new Firefox(devToolsEnabled);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            Thread.sleep(new Random().nextInt(3000));
            return new Firefox(devToolsEnabled);
        }
    }

    public static Safari newSafari(boolean devToolsEnabled) throws Exception {
        try {
            return new Safari();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            Thread.sleep(new Random().nextInt(3000));
            return new Safari();
        }
    }

    public static IE newIE(boolean devToolsEnabled) throws Exception {
        try {
            return new IE();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            Thread.sleep(new Random().nextInt(3000));
            return new IE();
        }
    }

    public static Edge newEdge(boolean devToolsEnabled) throws Exception {
        try {
            return new Edge();
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            Thread.sleep(new Random().nextInt(3000));
            return new Edge();
        }
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
        LOG.trace("Open url {}", url);
        this.webDriver.get(url);
    }

    public void saveWebDriverLogs() throws IOException {
        Map<String, File> logMap = new HashMap<>();
        Logs logs = this.webDriver.manage().logs();
        try {
            logs.getAvailableLogTypes().forEach(type -> {
                try {
                    LogEntries les = logs.get(type);
                    File f = saveAsTextFile(this.getClass().getSimpleName().toLowerCase() + "-" + type, "log");
                    try (OutputStream out = FileUtils.openOutputStream(f)) {
                        IOUtils.writeLines(les.getAll(), IOUtils.LINE_SEPARATOR, out, Charset.defaultCharset());
                        logMap.put(type, f);
                    }
                } catch (IOException ex) {
                    LOG.trace(ex.getLocalizedMessage());
                }
            });
        } catch (Exception ex) {
            LOG.warn(ex.getLocalizedMessage());
        }

        File browserLog = logMap.get("browser");
        if (browserLog != null) {
            FileUtils.readLines(browserLog, Charset.defaultCharset()).forEach(line -> {
                if (line.contains("[SEVERE]")) {
                    LOG.warn(line);
                }
            });
        }
    }

    /**
     * Takes a screen shot of current browserLog window.
     *
     * @return image file
     *
     * @throws IOException if error
     */
    public File takeBrowserScreenshot() throws IOException {
        File ss = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        File f = this.getLogPath().resolve("screenshot-" + LocalDateTime.now().format(DT_FORMATTER) + ".png").toFile();
        LOG.debug("Screenshot {}", f.getAbsolutePath());
        FileUtils.moveFile(ss, f);
        return f;
    }

    /**
     * Takes a screen shot of specified web element.
     *
     * @param webElement specified web element
     *
     * @return image file
     *
     * @throws IOException if error
     */
    public File takeBrowserScreenshot(WebElement webElement) throws IOException {
        File f = this.getLogPath().resolve("screenshot-" + LocalDateTime.now().format(DT_FORMATTER) + ".png").toFile();
        Screenshot screenshot = new AShot()
            .coordsProvider(new WebDriverCoordsProvider()) //find coordinates with WebDriver API
            .shootingStrategy(ShootingStrategies.viewportPasting(100))
            .takeScreenshot(webDriver, webElement);
        ImageIO.write(screenshot.getImage(), "PNG", f);
        LOG.debug("Screenshot {}", f.getAbsolutePath());
        return f;
    }

    public BrowserMobProxy getBrowserProxy() {
        return browserProxy;
    }

    public WebElement parentOf(WebElement element) {
        return element.findElement(By.xpath(".."));
    }

    /**
     * Clicks on a web element. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS.
     *
     * @param webElement target web element
     *
     * @return itself
     */
    public WebBrowser click(WebElement webElement) {
        this.delay();
        webElement.click();
        return this;
    }

    /**
     * Clicks on a web element. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS.
     *
     * @param by target web element locator
     *
     * @return itself
     */
    public WebBrowser click(By by) {
        return this.click(findElement(by));
    }

    /**
     * Hovers mouse on a web element. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS.
     *
     * @param webElement target web element
     *
     * @return itself
     */
    public WebBrowser hover(WebElement webElement) {
        scrollIntoView(webElement);
        Actions builder = new Actions(this.webDriver);
        builder.moveToElement(webElement)
            .moveByOffset(1, 0)
            .moveByOffset(-1, 0)
            .build().perform();
        this.delay();
        return this;
    }

    /**
     * Clears input area with CTRL-A and DELETE.
     *
     * @param textBox text input element
     *
     * @return itself
     */
    public WebBrowser clear(WebElement textBox) {
        textBox.clear();
        textBox.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        textBox.sendKeys(Keys.DELETE);
        return this;
    }

    /**
     * Sets text into a text box, clears it first. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS.
     *
     * @param textBox text input element
     * @param text    text to set
     *
     * @return itself
     */
    public WebBrowser setText(WebElement textBox, String text) {
        this.delay();
        this.clear(textBox);
        textBox.sendKeys(text);
        return this;
    }

    /**
     * Sets a check box state. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS.
     *
     * @param checkBox checkBox input element
     * @param checked  checked or not
     *
     * @return this
     */
    public WebBrowser setChecked(WebElement checkBox, boolean checked) {
        if (checkBox.isSelected() ^ checked) {
            this.delay();
            checkBox.click();
        }
        return this;
    }

    /**
     * Casts a WebElement as a HTML Select object. A delay of milliseconds can be set via integer system property
     * reactor.comm.WEBBROWSER_INTERACTION_DELAY_MILLIS.
     *
     * @param element target element
     *
     * @return HTML Select
     */
    public Select castAsSelect(WebElement element) {
        this.delay();
        return new Select(element);
    }

    public WebBrowser selectByVisibleText(WebElement select, String visibleText) {
        if (null == visibleText) {
            return this;
        }
        Select s = castAsSelect(select);
        s.selectByVisibleText(visibleText);
        return this;
    }

    public WebBrowser select(By by, String visibleText) {
        selectByVisibleText(findElement(by), visibleText);
        return this;
    }

    public String getFirstSelectedOption(By by) {
        Select s = castAsSelect(findElement(by));
        return s.getFirstSelectedOption().getText();
    }

    /**
     * Gets the first selected option display text of a WebElement as a HTML Select object.
     *
     * @param element target element
     *
     * @return display text
     */
    public String getFirstSelectedOption(WebElement element) {
        Select select = this.castAsSelect(element);
        return select.getFirstSelectedOption().getText();
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

    public WebBrowser setWindowSize(int width, int height) {
        this.delay();
        this.manage().window().setPosition(new Point(0, 0));
        this.manage().window().setSize(new Dimension(width, height));
        return this;
    }

    public WebBrowser landscape() {
        this.delay();
        this.manage().window().setPosition(new Point(0, 0));
        this.manage().window().setSize(new Dimension(WIDTH, HEIGHT));
        return this;
    }

    public WebBrowser portrait() {
        this.delay();
        this.manage().window().setPosition(new Point(0, 0));
        this.manage().window().setSize(new Dimension(HEIGHT, WIDTH));
        return this;
    }

    public WebBrowser hide() {
        this.manage().window().setPosition(new Point(WIDTH, WIDTH));
        return this;
    }

    /**
     * Highlights an element with solid red 3px border.
     *
     * @param we target web element
     *
     * @return self
     */
    public WebBrowser highlight(WebElement we) {
        executeScript("arguments[0].style.border='3px solid red'", we);
        return this;
    }

    public WebBrowser scrollToTop() {
        executeScript("window.scrollTo(0, 0 - document.body.scrollHeight);");
        return this;
    }

    public WebBrowser scrollToBottom() {
        executeScript("window.scrollTo(0, document.body.scrollHeight);");
        return this;
    }

    public WebBrowser scrollIntoView(WebElement element) {
        executeScript("arguments[0].scrollIntoView();", element);
        return this;
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
     * @return self
     *
     * @see ScreenResolution
     */
    public WebBrowser takeBrowserScreenshots() {
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
        return this;
    }

    /**
     * Waits for a specific condition.
     *
     * @param condition expected condition
     * @param seconds   wait timeout
     *
     * @return self
     */
    public WebBrowser waitFor(ExpectedCondition condition, int seconds) {
        WebDriverWait wait = new WebDriverWait(this, seconds);
        wait.until(condition);
        return this;
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

    public WebBrowser waitForNoElement(final By by, int seconds) {
        LOG.debug("Wait for element {} to disappear", by);
        WebDriverWait wait = new WebDriverWait(this, seconds);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
        return this;
    }

    /**
     * Waits for a specific element to be clickable.
     *
     * @param element the web element
     * @param seconds wait timeout
     *
     * @return The element to wait
     *
     * @throws org.openqa.selenium.TimeoutException if the timeout expires.
     */
    public WebElement waitForClickable(WebElement element, int seconds) {
        LOG.debug("Wait for element {} to be clickable", element);
        WebDriverWait wait = new WebDriverWait(this, seconds);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        return element;
    }

    /**
     * Waits for the element specified by locator to be clickable.
     *
     * @param by      the web element locator
     * @param seconds wait timeout
     *
     * @return The element to wait
     *
     * @throws org.openqa.selenium.TimeoutException if the timeout expires.
     */
    public WebElement waitForClickable(By by, int seconds) {
        LOG.debug("Wait for element {} to be clickable", by);
        WebDriverWait wait = new WebDriverWait(this, seconds);
        wait.until(ExpectedConditions.elementToBeClickable(by));
        return this.findElement(by);
    }

    public void continueToThisWebsite() {
    }

    /**
     * Sets default timeouts/waits (in second).
     * implicitlyWait = 0
     * pageLoadTimeout = 10
     * setScriptTimeout = 10
     *
     * @return
     */
    public WebBrowser setDefaultTimeouts() {
        this.webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        this.webDriver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        this.webDriver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
        return this;
    }

    public WebBrowser delay() {
        super.delay(interactionDelayMillis);
        return this;
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
