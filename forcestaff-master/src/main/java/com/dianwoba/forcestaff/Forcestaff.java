package com.dianwoba.forcestaff;

import com.dianwoba.forcestaff.core.ContextHolder;
import com.dianwoba.forcestaff.link.NettyServer;
import com.dianwoba.forcestaff.service.zk.ZKException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;
import java.util.Scanner;

/**
 * Push Server的启动器类
 *
 * @author Administrator
 */
public class Forcestaff {
    private static Logger logger = LoggerFactory.getLogger(Forcestaff.class);

    private final Ctx ctx;
    private NettyServer innerServer;

    public Forcestaff(SystemConfig config) throws ZKException {
        ctx = new Ctx(config);
        ContextHolder.setCtx(ctx);
        innerServer = new NettyServer(config.getServerPort());
    }

    public static void main(String[] args) throws ZKException {
        logger.info("Starting push server...");
        long t = System.currentTimeMillis();
        ApplicationContext appContext = new ClassPathXmlApplicationContext("classpath*:spring/app-context.xml");
        ContextHolder.setAppCtx(appContext);
        long t0 = System.currentTimeMillis();
        logger.info("Application context initialized, cost {} ms", t0 - t);
        t = t0;

        Properties prop = (Properties) appContext.getBean("systemProperty");
        if (prop == null) {
            prop = new Properties();
        }
        Forcestaff forcestaff = new Forcestaff(new SystemConfig(prop));
        forcestaff.start();

        // 关闭服务
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if ("shutdown".equals(scanner.nextLine())) {
                forcestaff.shutdown();
                break;
            }
        }
    }

    public void start() {
        innerServer.start();
    }

    public void shutdown() {
        ctx.shutdown();
        innerServer.shutdown();
    }

    public Ctx getCtx() {
        return ctx;
    }
}