package com.wildtigerrr.StoryOfCamelot.bin.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private static final String ERR_MSG = "Spring utility class not initialized";

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T bean(Class<T> clazz) {
        if (context == null) {
            throw new IllegalStateException(ERR_MSG);
        }
        return context.getBean(clazz);
    }

    public static <T> T bean(String name) {
        if (context == null) {
            throw new IllegalStateException(ERR_MSG);
        }
        return (T) context.getBean(name);
    }

    private static boolean IS_TEST = false; /* Can be changed by ReflectionUtils from ServiceBaseTest */
    public static boolean isRunningTest() {
        return IS_TEST;
    }

}
