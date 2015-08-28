/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.core.handler.impl;

import io.gravitee.gateway.core.handler.ContextHandler;
import io.gravitee.gateway.core.handler.Handler;
import io.gravitee.gateway.core.model.Api;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Properties;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class ApiHandlerFactory extends AbstractHandlerFactory {

    @Override
    public Handler create(Api api) {
        AbstractApplicationContext internalApplicationContext = createApplicationContext(api);
        return internalApplicationContext.getBean(ContextHandler.class);
    }

    private AbstractApplicationContext createApplicationContext(Api api) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(applicationContext);

        PropertyPlaceholderConfigurer configurer=new PropertyPlaceholderConfigurer();
        final Properties properties = applicationContext.getBean("graviteeProperties", Properties.class);
        configurer.setProperties(properties);
        configurer.setIgnoreUnresolvablePlaceholders(true);
        context.addBeanFactoryPostProcessor(configurer);

        context.getBeanFactory().registerSingleton("api", api);
        context.register(ApiHandlerConfiguration.class);
        context.setId("context-api-" + api.getName());
        context.refresh();

        return context;
    }
}