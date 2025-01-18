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
package net.spring.bean.cache.demo.factory;

import net.spring.bean.cache.demo.BeanDefinition;
import net.spring.bean.cache.demo.DefaultSingletonBeanRegistry;
import net.spring.bean.cache.demo.annotation.AutoWired;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DefaultListableBeanFactory {

    // 私有构造函数，防止外部直接创建实例
    private DefaultListableBeanFactory() {
    }

    private final static DefaultListableBeanFactory defaultListableBeanFactory = new DefaultListableBeanFactory();

    private final DefaultSingletonBeanRegistry defaultSingletonBeanRegistry = new DefaultSingletonBeanRegistry();

    public static DefaultListableBeanFactory getInstance(){
        return defaultListableBeanFactory;
    }
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition){
        this.beanDefinitionMap.put(beanName,beanDefinition);
    }

    public Map<String, BeanDefinition> getAllBeanDefinitions(){
        return beanDefinitionMap;
    }

    public Object getBean(String beanName) throws Exception{
        // 检查一级缓存是否存在
        Object sharedInstance = defaultSingletonBeanRegistry.getSingleton(beanName);
        if (sharedInstance != null) {
            // 缓存存在,直接返回
            return sharedInstance;
        }
        // 缓存不存在,则创建bean
        return createBean(beanName);
    }

    private Object createBean(String beanName) throws Exception{
        //调用对象的构造方法实例化对象
        Object beanInstance = this.createBeanInstance(beanName);
        //填充属性，主要对 bean 的依赖属性注入（@Autowired）
        this.populateBean(beanInstance);
        //initializeBean：回到一些如initMethod，InitalizingBean等方法
        this.initializeBean(beanInstance);
        defaultSingletonBeanRegistry.addSingleton(beanName,beanInstance);
        return beanInstance;
    }

    private void initializeBean(Object beanInstance) {
        //回到一些如initMethod，InitalizingBean等方法，这里省略
    }

    private void populateBean(Object beanInstance) throws Exception{
        Field[] fields = beanInstance.getClass().getDeclaredFields();
        if(fields != null){
            for (Field field : fields) {
                AutoWired autoWired = field.getDeclaredAnnotation(AutoWired.class);
                if (autoWired != null) {
                    //简单处理，获取属性类型的名称
                    String beanName = field.getType().getSimpleName();
                    //获取依赖Bean的实例
                    Object object = this.getBean(beanName);
                    field.setAccessible(true);
                    field.set(beanInstance,object);
                }
            }
        }

    }

    private Object createBeanInstance(String beanName) throws Exception {
        defaultSingletonBeanRegistry.addSingletonCurrentlyInCreation(beanName);
        Class classz = Class.forName(this.beanDefinitionMap.get(beanName).getBeanClass().getName());
        Object bean = classz.newInstance();
        defaultSingletonBeanRegistry.addSingletonFactory(beanName,()->getEarlyBeanReference(beanName, bean));
        return bean;
    }

    private Object getEarlyBeanReference(String beanName, Object bean) {
        //简单处理，直接返回bean，这里可以通过AOP创建一些bean的代理对象，方法增强
        return bean;
    }


}
