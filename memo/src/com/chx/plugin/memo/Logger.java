package com.chx.plugin.memo;

import com.intellij.notification.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * notifications
 *
 * @author chenxi
 * @date 2019-11-01
 */
public class Logger {

    public final static String DISPLAY_GROUP = "Memo";

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    public static void init() {
        if (initialized.compareAndSet(false, true)) {
            NotificationsConfiguration.getNotificationsConfiguration().register(DISPLAY_GROUP, NotificationDisplayType.NONE);
        }
    }

    public static void info(String message) {
        Notifications.Bus.notify(new Notification(DISPLAY_GROUP, DISPLAY_GROUP + " [INFO]", message, NotificationType.INFORMATION));
    }

    public static void warn(String message) {
        Notifications.Bus.notify(new Notification(DISPLAY_GROUP, DISPLAY_GROUP + " [WARN]", message, NotificationType.WARNING));
    }

    public static void error(String message) {
        Notifications.Bus.notify(new Notification(DISPLAY_GROUP, DISPLAY_GROUP + " [ERROR]", message, NotificationType.ERROR));
    }

}
