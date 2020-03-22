package com.theshamuel.medreg.module.invirto;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderListener implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(OrderListener.class);
    private Connection conn;

    public OrderListener(Connection conn, String queue) throws SQLException {
        this.conn = conn;
        Statement stmt = conn.createStatement();
        if (queue != null) {
            stmt.execute("LISTEN ".concat(queue));
        } else {
            stmt.execute("LISTEN invitro_events");
        }
        stmt.close();
    }

    public void run() {
        while (true) {
            try {
                PGNotification[] notifications = ((PGConnection) conn).getNotifications();
                if (notifications != null) {
                    for (int i = 0; i < notifications.length; i++) {
                        logger.info("New INVITRO order: " + notifications[i].getParameter());
                    }
                }

                TimeUnit.SECONDS.sleep(3);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
