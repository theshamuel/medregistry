package com.theshamuel.medreg.config;

import com.theshamuel.medreg.module.invirto.OrderListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty(
        value = "invitro.module.enabled",
        havingValue = "true",
        matchIfMissing = false)
public class InvitroConfig {

    private static Logger logger = LoggerFactory.getLogger(InvitroConfig.class);

    private final Environment env;

    public InvitroConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public Thread invitroModuleBean() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
            return null;
        }
        StringBuffer url = new StringBuffer("jdbc:postgresql://");
        if (env.getProperty("INVITRO_DB_SERVER") != null &&
                env.getProperty("INVITRO_DB_PORT") != null &&
                env.getProperty("INVITRO_DB") != null &&
                env.getProperty("INVITRO_DB_USER") != null &&
                env.getProperty("INVITRO_DB_PASSWORD") != null) {

            url.append(env.getProperty("INVITRO_DB_SERVER"));
            url.append(":");
            url.append(env.getProperty("INVITRO_DB_PORT"));
            url.append("/");
            url.append(env.getProperty("INVITRO_DB"));
        } else {
            url.append("127.0.0.1");
            url.append(":");
            url.append("5432");
            url.append("/");
            url.append("postgres");
            logger.warn("THE INVITRO MODULE is starting with DEFAULT DB params");
        }
        Connection conn;
        String user = "postgres";
        String password = "postgres";
        if (env.getProperty("INVITRO_DB_USER") != null) {
            user = env.getProperty("INVITRO_DB_USER");
        }
        if (env.getProperty("INVITRO_DB_PASSWORD") != null) {
            password = env.getProperty("INVITRO_DB_USER");
        }

        try {
            conn = DriverManager.getConnection(url.toString(), user, password);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.warn("INVITRO module has not started");
            return null;
        }

        Thread listener;
        try {
            listener = new Thread(new OrderListener(conn, null));
            listener.start();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            return null;
        }
        logger.info("INVITRO module has started");
        return listener;
    }
}
