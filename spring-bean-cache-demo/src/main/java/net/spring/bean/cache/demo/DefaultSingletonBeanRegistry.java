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

import net.spring.bean.cache.demo.factory.ObjectFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry {

    // 从上至下 分表代表这“三级缓存”
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256); //一级缓存
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16); // 二级缓存
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16); // 三级缓存

    /** Names of beans that are currently in creation. */
    // 这个缓存也十分重要：它表示bean创建过程中都会在里面呆着~
    // 它在Bean开始创建时放值，创建完成时会将其移出~
    private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

    /** Names of beans that have already been created at least once. */
    // 当这个Bean被创建完成后，会标记为这个 注意：这里是set集合 不会重复
    // 至少被创建了一次的  都会放进这里~~~~
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    private Object getSingleton(String beanName, boolean allowEarlyReference) {
        //1.先从一级缓存中获取，获取到直接返回
        Object singletonObject = this.singletonObjects.get(beanName);
        //2.如果获取不到或对象正在创建，就到二级缓存中去获取，获取到直接返回
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                //3.如果仍获取不到，且允许 singletonFactories(allowEarlyCurrentlyInCreation()）通过 getObject()获取。
                //就到三级缓存中用 getObject() 获取。
                //获取到就从 singletonFactories中移出，且放进 earlySingletonObjects。
                //（即从三级缓存移动到二级缓存）
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }

    public boolean isSingletonCurrentlyInCreation(String beanName){
        return this.singletonsCurrentlyInCreation.contains(beanName);
    }

    public void addSingletonCurrentlyInCreation(String beanName){
        this.singletonsCurrentlyInCreation.add(beanName);
    }


    public void addSingletonFactory(String beanName,ObjectFactory<?> singletonFactory){
        synchronized (this.singletonObjects){
            if(!this.singletonObjects.containsKey(beanName)){
                this.singletonFactories.put(beanName,singletonFactory);
                this.earlySingletonObjects.remove(beanName);
            }
        }
    }

    public void addSingleton(String beanName,Object instance){
        synchronized (this.singletonObjects){
            if(!this.singletonObjects.containsKey(beanName)){
                this.singletonObjects.put(beanName,instance);
                this.earlySingletonObjects.remove(beanName);
                this.singletonFactories.remove(beanName);
                //当这个Bean被创建完成后，会标记为被创建了
                this.alreadyCreated.add(beanName);
                //单例bean创建完成，移除正在创建的标识
                this.singletonsCurrentlyInCreation.remove(beanName);
            }
        }
    }

}
