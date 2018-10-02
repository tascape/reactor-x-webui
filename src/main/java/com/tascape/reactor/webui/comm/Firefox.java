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

import com.tascape.reactor.SystemConfiguration;
import com.tascape.reactor.Utils;
import com.tascape.reactor.driver.EntityDriver;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class Firefox extends WebBrowser {
    private static final Logger LOG = LoggerFactory.getLogger(Firefox.class);

    public static final String SYSPROP_DRIVER = "webdriver.gecko.driver";

    public static final int FIREBUG_PAGELOADEDTIMEOUT_MILLI = 60000;

    public static final String SYSPROP_FF_BINARY = "reactor.comm.FF_BINARY";

    public static final String SYSPROP_FF_PROFILE_NAME = "reactor.comm.FF_PROFILE_NAME";

    public static final String SYSPROP_FF_ABOUT_CONFIG = "reactor.comm.FF_ABOUT_CONFIG";

    public static final String DEFAULT_FF_PROFILE_NAME = "default";

    static {
        String driver = System.getProperty(SYSPROP_DRIVER);
        if (driver == null) {
            String driverFile = SystemUtils.IS_OS_WINDOWS ? "geckodriver.exe" : "geckodriver";
            File d = SystemConfiguration.HOME_PATH.resolve(DRIVER_DIRECTORY).resolve(driverFile).toFile();
            if (d.exists() && d.isFile()) {
                LOG.info("Use geckodriver at {}", d.getAbsolutePath());
                System.setProperty(SYSPROP_DRIVER, d.getAbsolutePath());
            } else {
                throw new RuntimeException("Cannot find geckodriver. Please set system property "
                        + SYSPROP_DRIVER + ", or download geckodriver into directory " + d.getParent()
                        + ". Check download page http://https://github.com/mozilla/geckodriver/releases");
            }
        } else {
            LOG.info("Use driver specified by system property {}={}", SYSPROP_DRIVER, driver);
        }
    }

    public Firebug getFirebug() {
        return firebug;
    }

    public static interface Extension {
        public void updateProfile(FirefoxProfile profile);
    }

    private final Path downloadDir = Files.createTempDirectory("ff-download");

    private final Firebug firebug;

    /**
     *
     * @param devToolsEnabled Firebug is working as DevTools on firefox
     *
     * @throws Exception any error
     */
    public Firefox(boolean devToolsEnabled) throws Exception {
        FirefoxProfile profile;

        ProfilesIni profileIni = new ProfilesIni();
        String profileName = sysConfig.getProperty(SYSPROP_FF_PROFILE_NAME);
        if (profileName != null) {
            LOG.debug("Load Firefox profile named as {}", profileName);
            profile = profileIni.getProfile(profileName);
        } else {
            LOG.debug("Load Firefox profile named as {}", DEFAULT_FF_PROFILE_NAME);
            profile = profileIni.getProfile(DEFAULT_FF_PROFILE_NAME);
        }
        if (profile == null) {
            throw new Exception("Cannot find Firefox profile");
        }

        profile.setAcceptUntrustedCertificates(true);
        profile.setAssumeUntrustedCertificateIssuer(false);
        profile.setPreference("services.sync.prefs.sync.signon.rememberSignons", false);
        profile.setPreference("app.update.enabled", false);
        profile.setPreference("browser.cache.disk.enable ", false);
        profile.setPreference("browser.cache.memory.enable", false);
        profile.setPreference("dom.max_chrome_script_run_time", 0);
        profile.setPreference("dom.max_script_run_time", 0);

        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", downloadDir.toFile().getAbsolutePath());
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf,application/x-pdf");
        profile.setPreference("pdfjs.disabled", true);

//        try {
//            String propFile = sysConfig.getProperty(SYSPROP_FF_ABOUT_CONFIG, "");
//            if (StringUtils.isNotBlank(propFile)) {
//                File f = new File(propFile);
//                if (f.exists()) {
//                    profile.updateUserPrefs(f);
//                }
//            }
//        } catch (Exception ex) {
//            LOG.warn((ex.getMessage()));
//        }
//
        this.firebug = new Firebug();
        if (devToolsEnabled) {
            this.firebug.updateProfile(profile);
        }

        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        super.initDesiredCapabilities(capabilities);
        capabilities.setCapability("marionette", true);
        super.setProxy(capabilities);
        super.setLogging(capabilities);

        long end = System.currentTimeMillis() + 180000;
        while (System.currentTimeMillis() < end) {
            try {
                FirefoxOptions options = new FirefoxOptions()
                    .setLogLevel(FirefoxDriverLogLevel.FATAL)
                    .merge(capabilities)
                    .setProfile(profile);
                super.setWebDriver(new FirefoxDriver(options));
                break;
            } catch (org.openqa.selenium.WebDriverException ex) {
                String msg = ex.getMessage();
                LOG.warn(msg);
                if (!msg.contains("Unable to bind to locking port 7054 within 45000 ms")) {
                    throw ex;
                }
            }
        }
    }

    @Override
    public int getPageLoadTimeMillis(String url) throws Exception {
        return this.firebug.getPageLoadTimeMillis(url);
    }

    @Override
    public int getAjaxLoadTimeMillis(Ajax ajax) throws Exception {
        return this.firebug.getAjaxLoadTimeMillis(ajax);
    }

    public class Firebug implements Extension {
        private final String tokenNetExport = UUID.randomUUID().toString();

        private final Path harPath = Firefox.this.getLogPath();

        public int getPageLoadTimeMillis(String url) throws IOException, ParseException, InterruptedException {
            this.doNetClear();
            Firefox.this.get(url);
            return this.getLastLoadTimeMillis(0);
        }

        public int getAjaxLoadTimeMillis(Ajax ajax) throws Exception {
            this.doNetClear();
            long start = ajax.doRequest();
            Utils.sleep(5000, "Wait for ajax to load");
            if (ajax.getByDisapper() != null) {
                Firefox.this.waitForNoElement(ajax.getByDisapper(), AJAX_TIMEOUT_SECONDS);
            }
            if (ajax.getByAppear() != null) {
                Firefox.this.waitForElement(ajax.getByAppear(), AJAX_TIMEOUT_SECONDS);
            }
            return this.getLastLoadTimeMillis(start);
        }

        private int getLastLoadTimeMillis(long startMillis) throws IOException, ParseException, InterruptedException {
            JSONObject json = this.waitForFirebugNetExport();
            EntityDriver driver = Firefox.this.getDriver();
            if (driver != null) {
                driver.captureScreen();
            }
            return HarLog.parse(json).getOverallLoadTimeMillis(startMillis);
        }

        /*
         * this seems not working.
         */
        private void doNetClear() throws InterruptedException {
            String js = "HAR.clear({token: \"" + tokenNetExport + "\"});";
            Firefox.this.executeScript(Void.class, js);
            LOG.debug("clear Net detail");
        }

        private String doNetExport() {
            String har = UUID.randomUUID().toString();
            String var = "var options = {token: \"" + tokenNetExport + "\", fileName: \"" + har + "\"};";
            String js = var + "HAR.triggerExport(options).then(result => {});";
            Firefox.this.executeScript(Void.class, js);
            return har + ".har";
        }

        private JSONObject waitForFirebugNetExport() throws IOException, InterruptedException {
            long end = System.currentTimeMillis() + FIREBUG_PAGELOADEDTIMEOUT_MILLI;
            long size = -1;
            File har = null;
            while (System.currentTimeMillis() < end) {
                Thread.sleep(5000);
                LOG.trace("Wait for http archive file");
                if (har != null && har.exists()) {
                    long sizeCurrent = har.length();
                    if (size > 0 && size == sizeCurrent) {
                        JSONObject json = new JSONObject(FileUtils.readFileToString(har, Charset.defaultCharset()));
                        File harTxt = Firefox.this.getLogPath().resolve("net-export.txt").toFile();
                        FileUtils.copyFile(har, harTxt);
                        LOG.debug("net export data {}", harTxt.getAbsolutePath());
                        return json;
                    } else {
                        LOG.debug("har file size {} bytes", sizeCurrent);
                        size = sizeCurrent;
                    }
                } else {
                    har = harPath.resolve(this.doNetExport()).toFile();
                    LOG.debug("har file {}", har.getAbsolutePath());
                }
            }
            throw new IOException("Cannot load firebug netexport har file");
        }

        // https://github.com/firebug/har-export-trigger
        // https://addons.mozilla.org/en-US/firefox/addon/har-export-trigger/
        @Override
        public void updateProfile(FirefoxProfile profile) {
            profile.setPreference("extensions.firebug.onByDefault", true);
            profile.setPreference("extensions.firebug.allPagesActivation", "on");
            profile.setPreference("extensions.firebug.defaultPanelName", "net");
            profile.setPreference("extensions.firebug.net.enableSites", true);
            profile.setPreference("extensions.netmonitor.har.autoConnect", true);
            profile.setPreference("extensions.netmonitor.har.contentAPIToken", tokenNetExport);
            profile.setPreference("devtools.netmonitor.har.enableAutoExportToFile", true);
            profile.setPreference("devtools.netmonitor.har.defaultLogDir", harPath.toFile().getAbsolutePath());
            profile.setPreference("devtools.netmonitor.har.pageLoadedTimeout", 1500); // default 1500
        }
    }

    public Path getDownloadDir() {
        return downloadDir;
    }
}

class HarLog {
    private static final Logger LOG = LoggerFactory.getLogger(HarLog.class);

    public String version;

    public Creator creator;

    public Browser browser;

    public List<Page> pages;

    public List<Entry> entries;

    public static HarLog parse(String harJson) throws JSONException {
        JSONObject json = new JSONObject(harJson);
        return HarLog.parse(json);
    }

    public static HarLog parse(JSONObject harJson) throws JSONException {
        JSONObject json = harJson.getJSONObject("log");
        HarLog har = new HarLog();

        har.version = json.getString("version");
        har.creator = Creator.parse(json.getJSONObject("creator"));
        har.browser = Browser.parse(json.getJSONObject("browser"));
        har.pages = new LinkedList<>();
        for (int i = 0; i < json.getJSONArray("pages").length(); i++) {
            har.pages.add(Page.parse(json.getJSONArray("pages").getJSONObject(i)));
        }
        har.entries = new LinkedList<>();
        for (int i = 0; i < json.getJSONArray("entries").length(); i++) {
            har.entries.add(Entry.parse(json.getJSONArray("entries").getJSONObject(i)));
        }
        return har;
    }

    public static class Creator {
        public String name;

        public String version;

        public static Creator parse(JSONObject json) throws JSONException {
            Creator o = new Creator();
            o.name = json.getString("name");
            o.version = json.getString("version");
            return o;
        }
    }

    public static class Browser {
        public String name;

        public String version;

        public static Browser parse(JSONObject json) throws JSONException {
            Browser o = new Browser();
            o.name = json.getString("name");
            o.version = json.getString("version");
            return o;
        }
    }

    public static class Page {

        public String startedDateTime;

        public String id;

        public String title;

        public PageTimings pageTimings;

        public static class PageTimings {

            public int onContentLoad;

            public int onLoad;

            public String toString;

            public static PageTimings parse(JSONObject json) throws JSONException {
                PageTimings o = new PageTimings();
                o.toString = json.toString();
                o.onContentLoad = json.getInt("onContentLoad");
                o.onLoad = json.getInt("onLoad");
                return o;
            }
        }

        public static Page parse(JSONObject json) throws JSONException {
            Page o = new Page();
            o.startedDateTime = json.getString("startedDateTime");
            o.id = json.getString("id");
            o.title = json.getString("title");
            o.pageTimings = PageTimings.parse(json.getJSONObject("pageTimings"));
            return o;
        }
    }

    public static class Entry {

        public String pageref;

        public String startedDateTime;

        public int time;

        public Request request;

        public Response response;

        public Timings timings;

        public String serverIPAddress;

        public int connection;

        public static class Request {
            public String method;

            public String url;

            public String httpVersion;

            public static Request parse(JSONObject json) throws JSONException {
                Request o = new Request();
                o.method = json.getString("method");
                o.url = json.getString("url");
                o.httpVersion = json.getString("httpVersion");
                return o;
            }
        }

        public static class Response {
            public int status;

            public String statusText;

            public String httpVersion;

            public String redirectURL;

            public static Response parse(JSONObject json) throws JSONException {
                Response o = new Response();
                o.status = json.getInt("status");
                o.statusText = json.getString("statusText");
                o.httpVersion = json.getString("httpVersion");
                o.redirectURL = json.getString("redirectURL");
                return o;
            }
        }

        public static class Timings {
            public int blocked;

            public int dns;

            public int connect;

            public int send;

            public int wait;

            public int receive;

            public static Timings paser(JSONObject json) throws JSONException {
                Timings o = new Timings();
                o.blocked = json.optInt("blocked");
                o.dns = json.optInt("dns");
                o.send = json.optInt("send");
                o.wait = json.optInt("wait");
                o.receive = json.optInt("receive");
                return o;
            }
        }

        public static Entry parse(JSONObject json) throws JSONException {
            Entry o = new Entry();
            o.pageref = json.getString("pageref");
            o.startedDateTime = json.getString("startedDateTime");
            try {
                o.time = json.getInt("time");
            } catch (JSONException ex) {
                LOG.trace("{} has no load time, use 0 - {}", o.pageref, ex.getMessage());
                o.time = 0;
            }
            o.request = Request.parse(json.getJSONObject("request"));
            o.response = Response.parse(json.getJSONObject("response"));
            o.timings = Timings.paser(json.getJSONObject("timings"));
            o.serverIPAddress = json.optString("serverIPAddress");
            o.connection = json.optInt("connection");
            return o;
        }
    }

    int getOverallLoadTimeMillis(long startMillis) throws ParseException {
        final String format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
        long start = Long.MAX_VALUE;
        long end = Long.MIN_VALUE;
        for (Entry entry : this.entries) {
            long s = Utils.getTime(entry.startedDateTime, format);
            if (s > startMillis) {
                start = Math.min(start, s);
                long e = s + entry.time;
                end = Math.max(end, e);
                LOG.debug("{}/{} - {}", entry.request.method, entry.response.status, entry.request.url);
            }
        }
        if (end <= start) {
            return -1;
        }
        long time = end - start;
        LOG.debug("Overall load time {} ms", time);
        return (int) (time);
    }

    int getLatestPageLoadTimeMillis() {
        Page p = this.pages.get(pages.size() - 1);
        LOG.debug("{}", p.pageTimings.toString);
        String id = p.id;
        for (Entry e : entries) {
            if (e.pageref.equals(p.id)) {
                LOG.debug("Page URL {}", e.request.url);
                break;
            }
        }
        return Math.max(p.pageTimings.onContentLoad, p.pageTimings.onLoad);
    }

    int getLatestEntryLoadTimeMillis(String urlRegex) {
        for (int i = this.entries.size() - 1; i >= 0; i--) {
            Entry e = this.entries.get(i);
            if (e.request.url.matches(urlRegex)) {
                LOG.debug("Request URL {}", e.request.url);
                return e.time;
            }
        }
        return -1;
    }
}
