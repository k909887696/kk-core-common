package com.kk.common.web.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;
import org.slf4j.MDC;

import javax.annotation.Resource;



public class ApplicationStartedEventListener implements GenericApplicationListener  {



    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    private static Class<?>[] EVENT_TYPES = { ApplicationStartingEvent.class,
            ApplicationEnvironmentPreparedEvent.class, ApplicationPreparedEvent.class,
            ContextClosedEvent.class, ApplicationFailedEvent.class };

    private static Class<?>[] SOURCE_TYPES = { SpringApplication.class,
            ApplicationContext.class };
    @Resource
    private Environment env;
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {

            ConfigurableEnvironment envi = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            MutablePropertySources mps = envi.getPropertySources();

            PropertySource<?> ps = mps.get("Config resource 'class path resource [application.yml]' via location 'optional:classpath:/'");
            //Config resource 'class path resource [application.yml]' via location 'optional:classpath:/'

            if (ps != null && ps.containsProperty("log.home")) {
                String logHome = (String) ps.getProperty("log.home");
                //System.out.println(kafkaUrl);
                MDC.put("logHome", logHome);
            }

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        // TODO Auto-generated method stub
        return DEFAULT_ORDER;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.context.event.GenericApplicationListener#
     * supportsEventType(org.springframework.core.ResolvableType)
     */
    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
