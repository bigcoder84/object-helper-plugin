package cn.bigcoder.plugin.objecthelper.common.util;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

/**
 * @author: Jindong.Tian
 * @date: 2021-02-11
 **/
public class NotificationUtils {

    /**
     * 从2020.3版本方式，通知组改由Plugin.xml注册。详见：https://plugins.jetbrains.com/docs/intellij/notifications.html#top-level-notifications
     */
    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("ObjectHelper Notification Group", NotificationDisplayType.BALLOON, true);

    public static void notifyInfo(Project project, String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION).notify(project);
    }

    public static void notifyWarning(Project project, String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING).notify(project);
    }

    public static void notifyError(Project project, String content) {
        NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR).notify(project);
    }
}
