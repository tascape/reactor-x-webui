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
package com.tascape.reactor.webui.comm;

import com.tascape.reactor.comm.EntityCommunication;
import com.thoughtworks.selenium.DefaultSelenium;
import org.openqa.selenium.server.SeleniumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web browser via Selenium remote server.
 *
 * @author wlinsong
 */
@SuppressWarnings("deprecation")
public abstract class WebBrowserRc extends EntityCommunication implements com.thoughtworks.selenium.Selenium {
    private static final Logger LOG = LoggerFactory.getLogger(WebBrowserRc.class);

    protected int port = 4444;

    private SeleniumServer seleniumServer;

    private DefaultSelenium ds;

    @Override
    public void connect() throws Exception {
        this.seleniumServer = this.initServer();
        this.seleniumServer.getServer().setStopAtShutdown(true);
    }

    public abstract SeleniumServer initServer();

    public void startBrowser(String url) {
        this.ds = this.initBrowser(url);
        this.start();
    }

    @Override
    public void start() {
        this.ds.start();
    }

    public abstract DefaultSelenium initBrowser(String url);

    @Override
    public void disconnect() throws Exception {
        if (this.ds != null) {
            this.ds.shutDownSeleniumServer();
            this.ds.stop();
        }
        if (this.seleniumServer != null) {
            this.seleniumServer.getServer().stop();
        }
    }

    @Override
    public void setExtensionJs(String extensionJs) {
        this.ds.setExtensionJs(extensionJs);
    }

    @Override
    public void start(String optionsString) {
        this.ds.start(optionsString);
    }

    @Override
    public void start(Object o) {
        this.ds.start(o);
    }

    @Override
    public void stop() {
        this.ds.stop();
    }

    @Override
    public void showContextualBanner() {
        this.ds.showContextualBanner();
    }

    @Override
    public void showContextualBanner(String className, String methodName) {
        this.ds.showContextualBanner(className, methodName);
    }

    @Override
    public void click(String locator) {
        this.ds.click(locator);
    }

    @Override
    public void doubleClick(String locator) {
        this.ds.doubleClick(locator);
    }

    @Override
    public void contextMenu(String locator) {
        this.ds.contextMenu(locator);
    }

    @Override
    public void clickAt(String locator, String coordString) {
        this.ds.clickAt(locator, coordString);
    }

    @Override
    public void doubleClickAt(String locator, String coordString) {
        this.ds.doubleClickAt(locator, coordString);
    }

    @Override
    public void contextMenuAt(String locator, String coordString) {
        this.ds.contextMenuAt(locator, coordString);
    }

    @Override
    public void fireEvent(String locator, String eventName) {
        this.ds.fireEvent(locator, eventName);
    }

    @Override
    public void focus(String locator) {
        this.ds.focus(locator);
    }

    @Override
    public void keyPress(String locator, String keySequence) {
        this.ds.keyPress(locator, keySequence);
    }

    @Override
    public void shiftKeyDown() {
        this.ds.shiftKeyDown();
    }

    @Override
    public void shiftKeyUp() {
        this.ds.shiftKeyUp();
    }

    @Override
    public void metaKeyDown() {
        this.ds.metaKeyDown();
    }

    @Override
    public void metaKeyUp() {
        this.ds.metaKeyUp();
    }

    @Override
    public void altKeyDown() {
        this.ds.altKeyDown();
    }

    @Override
    public void altKeyUp() {
        this.ds.altKeyUp();
    }

    @Override
    public void controlKeyDown() {
        this.ds.controlKeyDown();
    }

    @Override
    public void controlKeyUp() {
        this.ds.controlKeyUp();
    }

    @Override
    public void keyDown(String locator, String keySequence) {
        this.ds.keyDown(locator, keySequence);
    }

    @Override
    public void keyUp(String locator, String keySequence) {
        this.ds.keyUp(locator, keySequence);
    }

    @Override
    public void mouseOver(String locator) {
        this.ds.mouseOver(locator);
    }

    @Override
    public void mouseOut(String locator) {
        this.ds.mouseOut(locator);
    }

    @Override
    public void mouseDown(String locator) {
        this.ds.mouseDown(locator);
    }

    @Override
    public void mouseDownRight(String locator) {
        this.ds.mouseDownRight(locator);
    }

    @Override
    public void mouseDownAt(String locator, String coordString) {
        this.ds.mouseDownAt(locator, coordString);
    }

    @Override
    public void mouseDownRightAt(String locator, String coordString) {
        this.ds.mouseDownRightAt(locator, coordString);
    }

    @Override
    public void mouseUp(String locator) {
        this.ds.mouseUp(locator);
    }

    @Override
    public void mouseUpRight(String locator) {
        this.ds.mouseUpRight(locator);
    }

    @Override
    public void mouseUpAt(String locator, String locator1) {
        this.ds.mouseUpAt(locator, locator1);
    }

    @Override
    public void mouseUpRightAt(String locator, String locator1) {
        this.ds.mouseUpRightAt(locator, locator1);
    }

    @Override
    public void mouseMove(String locator) {
        this.ds.mouseMove(locator);
    }

    @Override
    public void mouseMoveAt(String locator, String coordString) {
        this.ds.mouseMoveAt(locator, coordString);
    }

    @Override
    public void type(String locator, String coordString) {
        this.ds.type(locator, locator);
    }

    @Override
    public void typeKeys(String locator, String value) {
        this.ds.typeKeys(locator, value);
    }

    @Override
    public void setSpeed(String value) {
        this.ds.setSpeed(value);
    }

    @Override
    public String getSpeed() {
        return this.ds.getSpeed();
    }

    @Override
    public String getLog() {
        return this.ds.getLog();
    }

    @Override
    public void check(String locator) {
        this.ds.check(locator);
    }

    @Override
    public void uncheck(String locator) {
        this.ds.uncheck(locator);
    }

    @Override
    public void select(String selectLocator, String optionLocator) {
        this.ds.select(selectLocator, optionLocator);
    }

    @Override
    public void addSelection(String locator, String optionLocator) {
        this.ds.addSelection(locator, optionLocator);
    }

    @Override
    public void removeSelection(String locator, String optionLocator) {
        this.ds.removeSelection(locator, optionLocator);
    }

    @Override
    public void removeAllSelections(String locator) {
        this.ds.removeAllSelections(locator);
    }

    @Override
    public void submit(String formLocator) {
        this.ds.submit(formLocator);
    }

    @Override
    public void open(String url, String ignoreResponseCode) {
        this.ds.open(url, ignoreResponseCode);
    }

    @Override
    public void open(String url) {
        this.ds.open(url);
    }

    @Override
    public void openWindow(String url, String windowID) {
        this.ds.openWindow(url, windowID);
    }

    @Override
    public void selectWindow(String windowID) {
        this.ds.selectWindow(windowID);
    }

    @Override
    public void selectPopUp(String windowID) {
        this.ds.selectPopUp(windowID);
    }

    @Override
    public void deselectPopUp() {
        this.ds.deselectPopUp();
    }

    @Override
    public void selectFrame(String locator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getWhetherThisFrameMatchFrameExpression(String currentFrameString, String target) {
        return this.ds.getWhetherThisFrameMatchFrameExpression(currentFrameString, target);
    }

    @Override
    public boolean getWhetherThisWindowMatchWindowExpression(String currentFrameString, String target) {
        return this.ds.getWhetherThisWindowMatchWindowExpression(currentFrameString, target);
    }

    @Override
    public void waitForPopUp(String windowID, String tineout) {
        this.ds.waitForPopUp(windowID, tineout);
    }

    @Override
    public void chooseCancelOnNextConfirmation() {
        this.ds.chooseCancelOnNextConfirmation();
    }

    @Override
    public void chooseOkOnNextConfirmation() {
        this.ds.chooseOkOnNextConfirmation();
    }

    @Override
    public void answerOnNextPrompt(String answer) {
        this.ds.answerOnNextPrompt(answer);
    }

    @Override
    public void goBack() {
        this.ds.goBack();
    }

    @Override
    public void refresh() {
        this.ds.refresh();
    }

    @Override
    public void close() {
        this.ds.close();
    }

    @Override
    public boolean isAlertPresent() {
        return this.ds.isAlertPresent();
    }

    @Override
    public boolean isPromptPresent() {
        return this.ds.isPromptPresent();
    }

    @Override
    public boolean isConfirmationPresent() {
        return this.ds.isConfirmationPresent();
    }

    @Override
    public String getAlert() {
        return this.ds.getAlert();
    }

    @Override
    public String getConfirmation() {
        return this.ds.getConfirmation();
    }

    @Override
    public String getPrompt() {
        return this.ds.getPrompt();
    }

    @Override
    public String getLocation() {
        return this.ds.getLocation();
    }

    @Override
    public String getTitle() {
        return this.ds.getTitle();
    }

    @Override
    public String getBodyText() {
        return this.ds.getBodyText();
    }

    @Override
    public String getValue(String locator) {
        return this.ds.getValue(locator);
    }

    @Override
    public String getText(String locator) {
        return this.ds.getText(locator);
    }

    @Override
    public void highlight(String locator) {
        this.ds.highlight(locator);
    }

    @Override
    public String getEval(String script) {
        return this.ds.getEval(script);
    }

    @Override
    public boolean isChecked(String locator) {
        return this.ds.isChecked(locator);
    }

    @Override
    public String getTable(String tableCellAddress) {
        return this.ds.getTable(tableCellAddress);
    }

    @Override
    public String[] getSelectedLabels(String selectLocator) {
        return this.ds.getSelectedLabels(selectLocator);
    }

    @Override
    public String getSelectedLabel(String selectLocator) {
        return this.ds.getSelectedLabel(selectLocator);
    }

    @Override
    public String[] getSelectedValues(String selectLocator) {
        return this.ds.getSelectedValues(selectLocator);
    }

    @Override
    public String getSelectedValue(String selectLocator) {
        return this.ds.getSelectedValue(selectLocator);
    }

    @Override
    public String[] getSelectedIndexes(String selectLocator) {
        return this.ds.getSelectedIndexes(selectLocator);
    }

    @Override
    public String getSelectedIndex(String selectLocator) {
        return this.ds.getSelectedIndex(selectLocator);
    }

    @Override
    public String[] getSelectedIds(String selectLocator) {
        return null;
    }

    @Override
    public String getSelectedId(String selectLocator) {
        return this.ds.getSelectedId(selectLocator);
    }

    @Override
    public boolean isSomethingSelected(String selectLocator) {
        return this.ds.isSomethingSelected(selectLocator);
    }

    @Override
    public String[] getSelectOptions(String selectLocator) {
        return this.ds.getSelectOptions(selectLocator);
    }

    @Override
    public String getAttribute(String attributeLocator) {
        return this.ds.getAttribute(attributeLocator);
    }

    @Override
    public boolean isTextPresent(String pattern) {
        return this.ds.isTextPresent(pattern);
    }

    @Override
    public boolean isElementPresent(String locator) {
        return this.ds.isElementPresent(locator);
    }

    @Override
    public boolean isVisible(String locator) {
        return this.ds.isVisible(locator);
    }

    @Override
    public boolean isEditable(String locator) {
        return this.ds.isEditable(locator);
    }

    @Override
    public String[] getAllButtons() {
        return this.ds.getAllButtons();
    }

    @Override
    public String[] getAllLinks() {
        return this.ds.getAllLinks();
    }

    @Override
    public String[] getAllFields() {
        return this.ds.getAllFields();
    }

    @Override
    public String[] getAttributeFromAllWindows(String attributeName) {
        return this.ds.getAttributeFromAllWindows(attributeName);
    }

    @Override
    public void dragdrop(String locator, String movementsString) {
        this.ds.dragAndDrop(locator, movementsString);
    }

    @Override
    public void setMouseSpeed(String pixels) {
        this.ds.setMouseSpeed(pixels);
    }

    @Override
    public Number getMouseSpeed() {
        return this.ds.getMouseSpeed();
    }

    @Override
    public void dragAndDrop(String locator, String movementsString) {
        this.ds.dragAndDrop(locator, movementsString);
    }

    @Override
    public void dragAndDropToObject(String locatorOfObjectToBeDragged, String locatorOfDragDestinationObject) {
        this.ds.dragAndDropToObject(locatorOfObjectToBeDragged, locatorOfDragDestinationObject);
    }

    @Override
    public void windowFocus() {
        this.ds.windowFocus();
    }

    @Override
    public void windowMaximize() {
        this.ds.windowMaximize();
    }

    @Override
    public String[] getAllWindowIds() {
        return this.ds.getAllWindowIds();
    }

    @Override
    public String[] getAllWindowNames() {
        return this.ds.getAllWindowNames();
    }

    @Override
    public String[] getAllWindowTitles() {
        return this.ds.getAllWindowTitles();
    }

    @Override
    public String getHtmlSource() {
        return this.ds.getHtmlSource();
    }

    @Override
    public void setCursorPosition(String locator, String position) {
        this.ds.setCursorPosition(locator, position);
    }

    @Override
    public Number getElementIndex(String locator) {
        return this.ds.getElementIndex(locator);
    }

    @Override
    public boolean isOrdered(String locator1, String locator2) {
        return this.ds.isOrdered(locator1, locator2);
    }

    @Override
    public Number getElementPositionLeft(String locator) {
        return this.ds.getElementPositionLeft(locator);
    }

    @Override
    public Number getElementPositionTop(String locator) {
        return this.ds.getElementPositionTop(locator);
    }

    @Override
    public Number getElementWidth(String locator) {
        return this.ds.getElementWidth(locator);
    }

    @Override
    public Number getElementHeight(String locator) {
        return this.ds.getElementHeight(locator);
    }

    @Override
    public Number getCursorPosition(String locator) {
        return this.ds.getCursorPosition(locator);
    }

    @Override
    public String getExpression(String expression) {
        return this.ds.getExpression(expression);
    }

    @Override
    public Number getXpathCount(String xpath) {
        return this.ds.getXpathCount(xpath);
    }

    @Override
    public Number getCssCount(String css) {
        return this.ds.getCssCount(css);
    }

    @Override
    public void assignId(String locator, String identifier) {
        this.ds.assignId(locator, identifier);
    }

    @Override
    public void allowNativeXpath(String allow) {
        this.ds.allowNativeXpath(allow);
    }

    @Override
    public void ignoreAttributesWithoutValue(String ignore) {
        this.ds.ignoreAttributesWithoutValue(ignore);
    }

    @Override
    public void waitForCondition(String script, String timeout) {
        this.ds.waitForCondition(script, timeout);
    }

    @Override
    public void setTimeout(String timeout) {
        this.ds.setTimeout(timeout);
    }

    @Override
    public void waitForPageToLoad(String timeout) {
        this.ds.waitForPageToLoad(timeout);
    }

    @Override
    public void waitForFrameToLoad(String frameAddress, String timeout) {
        this.ds.waitForFrameToLoad(frameAddress, timeout);
    }

    @Override
    public String getCookie() {
        return this.ds.getCookie();
    }

    @Override
    public String getCookieByName(String name) {
        return this.ds.getCookieByName(name);
    }

    @Override
    public boolean isCookiePresent(String name) {
        return this.ds.isCookiePresent(name);
    }

    @Override
    public void createCookie(String nameValuePair, String optionsString) {
        this.ds.createCookie(nameValuePair, optionsString);
    }

    @Override
    public void deleteCookie(String name, String optionsString) {
        this.ds.deleteCookie(name, optionsString);
    }

    @Override
    public void deleteAllVisibleCookies() {
        this.ds.deleteAllVisibleCookies();
    }

    @Override
    public void setBrowserLogLevel(String logLevel) {
        this.ds.setBrowserLogLevel(logLevel);
    }

    @Override
    public void runScript(String script) {
        this.ds.runScript(script);
    }

    @Override
    public void addLocationStrategy(String strategyName, String functionDefinition) {
        this.ds.addLocationStrategy(strategyName, functionDefinition);
    }

    @Override
    public void captureEntirePageScreenshot(String filename, String kwargs) {
        this.ds.captureEntirePageScreenshot(filename, kwargs);
    }

    @Override
    public void rollup(String rollupName, String kwargs) {
        this.ds.rollup(rollupName, kwargs);
    }

    @Override
    public void addScript(String scriptContent, String scriptTagId) {
        this.ds.addScript(scriptContent, scriptTagId);
    }

    @Override
    public void removeScript(String scriptTagId) {
        this.ds.removeScript(scriptTagId);
    }

    @Override
    public void useXpathLibrary(String libraryName) {
        this.ds.useXpathLibrary(libraryName);
    }

    @Override
    public void setContext(String context) {
        this.ds.setContext(context);
    }

    @Override
    public void attachFile(String fieldLocator, String fileLocator) {
        this.ds.attachFile(fieldLocator, fileLocator);
    }

    @Override
    public void captureScreenshot(String filename) {
        this.ds.captureScreenshot(filename);
    }

    @Override
    public String captureScreenshotToString() {
        return this.ds.captureScreenshotToString();
    }

    @Override
    public String captureNetworkTraffic(String type) {
        return this.ds.captureNetworkTraffic(type);
    }

    @Override
    public void addCustomRequestHeader(String key, String value) {
        this.ds.addCustomRequestHeader(key, value);
    }

    @Override
    public String captureEntirePageScreenshotToString(String kwargs) {
        return this.ds.captureEntirePageScreenshotToString(kwargs);
    }

    @Override
    public void shutDownSeleniumServer() {
        this.ds.shutDownSeleniumServer();
    }

    @Override
    public String retrieveLastRemoteControlLogs() {
        return this.ds.retrieveLastRemoteControlLogs();
    }

    @Override
    public void keyDownNative(String keycode) {
        this.ds.keyDownNative(keycode);
    }

    @Override
    public void keyUpNative(String keycode) {
        this.ds.keyUpNative(keycode);
    }

    @Override
    public void keyPressNative(String keycode) {
        this.ds.keyPressNative(keycode);
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        FirefoxRc browser = new FirefoxRc();
        try {
            browser.connect();
            browser.startBrowser("http://www.yahoo.com");
            browser.windowMaximize();
            browser.waitForPageToLoad("60000000");
            browser.click("link=Learn HTML");
            Thread.sleep(5000);
        } finally {
            browser.disconnect();
        }
    }
}
