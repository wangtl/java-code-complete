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
package org.example.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;


public class LoggerService {
    private static final LoggerService SERVICE = new LoggerService();

    private final Logger logger;

    private final List<Logger> loggerList;

    private LoggerService(){
        ServiceLoader<Logger> serviceLoader = ServiceLoader.load(Logger.class);
        List<Logger> list = new ArrayList<>();
        for(Logger log : serviceLoader){
            list.add(log);
        }
        // LoggerList 是所有 ServiceProvider
        loggerList = list;
        if (!list.isEmpty()) {
            // Logger 只取一个
            logger = list.get(0);
        } else {
            System.out.println("没有发现 Logger 服务提供者");
            logger = new Logger(){};
        }
    }

    public static Logger getLogger() {
        return SERVICE.logger;
    }

    public static List<Logger> getLoggers() {
        return SERVICE.loggerList;
    }

}
