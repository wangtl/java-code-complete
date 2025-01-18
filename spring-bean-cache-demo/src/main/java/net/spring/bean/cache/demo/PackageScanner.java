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
package net.spring.bean.cache.demo;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;

public class PackageScanner {

    public static Set<Class<?>> scanPackage(String packageName) throws Exception{
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');

        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    scanDirectory(new File(resource.getFile()), classes, packageName);
                } else if ("jar".equals(protocol)) {
                    scanJar(((JarURLConnection) resource.openConnection()).getJarFile(), packageName, classes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static void scanDirectory(File directory, Set<Class<?>> classes, String packageName) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, classes, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
            }
        }
    }

    private static void scanJar(java.util.jar.JarFile jarFile, String packageName, Set<Class<?>> classes) throws ClassNotFoundException {
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.charAt(0) != '/') {
                continue;
            }
            name = name.substring(1);
            if (name.startsWith(packageName.replace('.', '/')) && name.endsWith(".class")) {
                String className = name.replace('/', '.'); // Remove slashes and convert to dots.
                classes.add(Thread.currentThread().getContextClassLoader().loadClass(className.substring(0, className.length() - 6)));
            }
        }
    }
}
