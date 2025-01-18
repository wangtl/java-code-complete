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
package net.spring.bean.cache.demo.context;

import net.spring.bean.cache.demo.BeanDefinition;
import net.spring.bean.cache.demo.PackageScanner;
import net.spring.bean.cache.demo.annotation.Service;
import net.spring.bean.cache.demo.factory.DefaultListableBeanFactory;

import java.util.Map;
import java.util.Set;

public class DefaultApplicationContext {
    //简化Spring加载上下文的入口AbstractApplicationContext的refresh方法的视线。
    // Spring容器的初始化始于AbstractApplicationContext的refresh()方法的调用，该方法触发了容器的刷新操作。
    public void refresh(String packageName) throws Exception{

        DefaultListableBeanFactory defaultListableBeanFactory = DefaultListableBeanFactory.getInstance();

        //扫描包下的所有类
        Set<Class<?>> classes = PackageScanner.scanPackage(packageName);
        //通过service注解，获取所有需要创建实例的bean
        classes.forEach(classz ->{
            Service service = classz.getDeclaredAnnotation(Service.class);
            if(service != null){
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setBeanClass(classz);
                defaultListableBeanFactory.registerBeanDefinition(classz.getSimpleName(),beanDefinition);
            }
        });
        Map<String, BeanDefinition> beanDefinitionMap = defaultListableBeanFactory.getAllBeanDefinitions();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String key = entry.getKey();
            defaultListableBeanFactory.getBean(key);
        }
    }

}
