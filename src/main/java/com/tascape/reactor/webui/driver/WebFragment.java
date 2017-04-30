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
import org.openqa.selenium.support.ui.LoadableComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public abstract class WebFragment extends LoadableComponent<WebFragment> {
    private static final Logger LOG = LoggerFactory.getLogger(WebFragment.class);

    protected WebPage page;

    protected WebApp app;

    protected WebBrowser webBrowser;

    @Override
    protected void load() {
        this.isLoaded();
    }

    public void setPage(WebPage page) {
        this.page = page;
        this.app = this.page.getApp();
        this.webBrowser = this.app.getWebBrowser();
    }
}
