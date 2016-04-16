package com.dianwoba.forcestaff.core;

import com.dianwoba.forcestaff.Ctx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public final class ContextHolder {

    private static Logger logger = LoggerFactory.getLogger(ContextHolder.class);
    private static ApplicationContext appCtx;
    private static Ctx ctx;

    private ContextHolder() {
    }

    public static ApplicationContext getAppCtx() {
        while (appCtx == null) {
            logger.warn("Application context is not setted.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.warn("Thread sleep interrupted.");
            }
        }
        return appCtx;
    }

    public static void setAppCtx(ApplicationContext appCtx) {
        ContextHolder.appCtx = appCtx;
    }

    public static Ctx getCtx() {
        return ctx;
    }

    public static void setCtx(Ctx ctx) {
        ContextHolder.ctx = ctx;
    }
}