/*
 * Copyright 2024 Tingliang Wang All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example.spi.test;

import org.example.spi.Logger;
import org.example.spi.LoggerService;


public class TestJavaSPI {

    public static void main(String[] args) {
        Logger logger = LoggerService.getLogger();
        logger.info("你好");
        logger.debug("测试Java SPI 机制");
    }
}