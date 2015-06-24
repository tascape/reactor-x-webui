package com.tascape.qa.th.comm;

import com.tascape.qa.th.SystemConfiguration;
import com.tascape.qa.th.Utils;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class Firefox extends WebBrowser {
    private static final Logger LOG = LoggerFactory.getLogger(Firefox.class);

    public static final int FIREBUG_PAGELOADEDTIMEOUT_MILLI = 180000;

    public static final String SYSPROP_FF_BINARY = "qa.comm.FF_BINARY";

    public static final String SYSPROP_FF_PROFILE_NAME = "qa.comm.FF_PROFILE_NAME";

    public static final String DEFAULT_FF_PROFILE_NAME = "default";

    private Firebug firebug = null;

    public Firefox(boolean enableFirebug) throws Exception {
        FirefoxProfile profile;

        ProfilesIni profileIni = new ProfilesIni();
        String profileName = SYSCONFIG.getProperty(SYSPROP_FF_PROFILE_NAME);
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

        profile.setPreference("app.update.enabled", false);
        profile.setEnableNativeEvents(false);
        profile.setAcceptUntrustedCertificates(true);
        profile.setAssumeUntrustedCertificateIssuer(false);
        profile.setPreference("dom.max_chrome_script_run_time", 0);
        profile.setPreference("dom.max_script_run_time", 0);
        if (enableFirebug) {
            this.firebug = new Firebug();
            this.firebug.updateProfile(profile);
        }
        long end = System.currentTimeMillis() + 180000;
        while (System.currentTimeMillis() < end) {
            try {
                this.setWebDriver(new FirefoxDriver(profile));
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

    public Firebug getFirebug() {
        return firebug;
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

        private final Path harPath = SYSCONFIG.getLogPath()
                .resolve(SYSCONFIG.getExecId())
                .resolve(SystemConfiguration.CONSTANT_LOG_KEEP_ALIVE_PREFIX + "har-" + System.currentTimeMillis());

        public void clearHarDir() throws IOException {
            File[] hars = this.harPath.toFile().listFiles((File dir, String name) -> name.endsWith(".har"));
            if (hars != null) {
                for (File f : hars) {
                    f.delete();
                }
            }
        }

        public int getPageLoadTimeMillis(String url)
                throws IOException, JSONException, InterruptedException, ParseException {
            Utils.sleep(2000, "");
            this.clearHarDir();
            Firefox.this.get(url);
            JSONObject json = this.waitForFirebugNetExport();
            return HarLog.parse(json).getOverallLoadTimeMillis();
        }

        public int getAjaxLoadTimeMillis(Ajax ajax) throws Exception {
            this.doNetClear();
            ajax.doRequest();
            Utils.sleep(5000, "Wait for ajax to load");
            try {
                if (ajax.getByDisapper() != null) {
                    Firefox.this.waitForNoElement(ajax.getByDisapper(), AJAX_TIMEOUT_SECONDS);
                }
                if (ajax.getByAppear() != null) {
                    Firefox.this.waitForElement(ajax.getByAppear(), AJAX_TIMEOUT_SECONDS);
                }
                if (ajax.getByDisapper() == null && ajax.getByAppear() == null) {
                    Utils.sleep(5000, "Wait for ajax to load");
                }
            } finally {
                this.clearHarDir();
                this.doNetExport();
            }
            JSONObject json = this.waitForFirebugNetExport();
            return HarLog.parse(json).getOverallLoadTimeMillis();
        }

        private void doNetClear() {
            String js = "window.NetExport.clear(\"" + tokenNetExport + "\")";
            Firefox.this.executeScript(Void.class, js);
        }

        private void doNetExport() {
            String js = "window.NetExport.triggerExport(\"" + tokenNetExport + "\")";
            Firefox.this.executeScript(Void.class, js);
        }

        private JSONObject waitForFirebugNetExport() throws IOException, InterruptedException {
            long end = System.currentTimeMillis() + FIREBUG_PAGELOADEDTIMEOUT_MILLI;
            File[] harFiles = {};
            while (System.currentTimeMillis() < end) {
                Utils.sleep(10000, "Wait for netexport http archive file");
                try {
                    File[] hars = this.harPath.toFile().listFiles((File dir, String name) -> name.endsWith(".har"));
                    if (hars == null || hars.length == 0) {
                        continue;
                    }
                    if (hars.length != harFiles.length) {
                        harFiles = hars;
                        continue;
                    }

                    this.clearHarDir();
                    this.doNetExport();
                    hars = this.harPath.toFile().listFiles((File dir, String name) -> name.endsWith(".har"));
                    File har = hars[0];
                    LOG.debug("Load data from {}", har.getAbsolutePath());
                    JSONObject json = new JSONObject(FileUtils.readFileToString(har));
                    FileUtils.copyFile(har, this.harPath.resolve(har.getName() + ".json").toFile());
                    this.clearHarDir();
                    return json;
                } catch (IOException | JSONException ex) {
                    LOG.warn(ex.getMessage());
                }
            }
            throw new IOException("Cannot load firebug netexport har file");
        }

        @Override
        public void updateProfile(FirefoxProfile profile) {
            String domain = "extensions.firebug.";
            profile.setPreference(domain + "onByDefault", true);
            profile.setPreference(domain + "allPagesActivation", "on");
            profile.setPreference(domain + "defaultPanelName", "net");
            profile.setPreference(domain + "net.enableSites", true);
            profile.setPreference(domain + "netexport.alwaysEnableAutoExport", true);
            profile.setPreference(domain + "netexport.autoExportToFile", true);
            profile.setPreference(domain + "netexport.showPreview", false);
            profile.setPreference(domain + "netexport.timeout", 180000); // default 60000
            profile.setPreference(domain + "netexport.pageLoadedTimeout", 1500); // default 1500
            profile.setPreference(domain + "netexport.secretToken", tokenNetExport);
            profile.setPreference(domain + "netexport.defaultLogDir", harPath.toFile().getAbsolutePath());
        }
    }

    public static interface Extension {
        public void updateProfile(FirefoxProfile profile);
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
                o.blocked = json.getInt("blocked");
                o.dns = json.getInt("dns");
                o.send = json.getInt("send");
                o.wait = json.getInt("wait");
                o.receive = json.getInt("receive");
                return o;
            }
        }

        public static Entry parse(JSONObject json) throws JSONException {
            Entry o = new Entry();
            o.pageref = json.getString("pageref");
            o.startedDateTime = json.getString("startedDateTime");
            o.time = json.getInt("time");
            o.request = Request.parse(json.getJSONObject("request"));
            o.response = Response.parse(json.getJSONObject("response"));
            o.timings = Timings.paser(json.getJSONObject("timings"));
            o.serverIPAddress = json.optString("serverIPAddress");
            o.connection = json.optInt("connection");
            return o;
        }
    }

    int getOverallLoadTimeMillis() throws ParseException {
        final String format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
        long start = Long.MAX_VALUE;
        long end = Long.MIN_VALUE;
        for (Entry entry : this.entries) {
            long s = Utils.getTime(entry.startedDateTime, format);
            start = Math.min(start, s);
            long e = s + entry.time;
            end = Math.max(end, e);
            LOG.debug("{}/{} - {}", entry.request.method, entry.response.status, entry.request.url);
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
