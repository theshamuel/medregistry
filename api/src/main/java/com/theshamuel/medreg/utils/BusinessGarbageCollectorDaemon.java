package com.theshamuel.medreg.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public final class BusinessGarbageCollectorDaemon {

    private static final Logger logger = LoggerFactory
            .getLogger(BusinessGarbageCollectorDaemon.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void start() {

        logger.info("Started business GC");
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("Call clearOutdatedAppointments in business GC");
                clearOutdatedAppointments();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private static void clearOutdatedAppointments() {
        final String uri =  "http://localhost:" +
                        System.getenv("SERVER_PORT") != null ? System.getenv("SERVER_PORT") : "9000" +
                            "/api/appointments/outdated";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(uri);
    }

}
