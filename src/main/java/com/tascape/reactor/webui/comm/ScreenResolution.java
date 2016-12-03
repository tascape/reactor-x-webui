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

/**
 *
 * @author linsong wang
 */
public enum ScreenResolution {
    SR_768_1024(768, 1024), // iPad mini
    SR_640_1136(640, 1136), // iPhone 5
    SR_750_1334(750, 1334), // iPhone 6
    SR_1080_1920(1080, 1920), // iPhone 6+
    SR_1536_2048(1536, 2048), // iPad 3
    SR_1440_2560(1440, 2560); // galaxy s6

    public final int width;

    public final int height;

    private ScreenResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
