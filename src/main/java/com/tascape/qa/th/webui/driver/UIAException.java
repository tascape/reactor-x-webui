/*
 * Copyright 2015 - 2016 tascape.
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

/**
 *
 * @author linsong wang
 */
public class UIAException extends RuntimeException {
    private static final long serialVersionUID = 8493532L;

    public UIAException(String message) {
        super(message);
    }

    public UIAException(String message, Throwable ex) {
        super(message, ex);
    }
}
