package src.model.assistance;

import src.model.Notification;

import static src.eric.Constant.NotificationType;
import static src.eric.Constant.SYSTEM_NOTIFICATION;

public class NotificationCache {

    private static Notification noti(int type, String from, String msg) {
        Notification n = new Notification();
        n.setFrom(from);
        n.setType(NotificationType[type]);
        n.setContent(msg);
        return n;
    }

    public static Notification CREATE_P_MEM(String p_name) {
        return noti(2,
                SYSTEM_NOTIFICATION,
                "您参与了一个新项目: " + p_name);
    }
    public static Notification CREATE_P_LEA(String p_name) {
        return noti(2, SYSTEM_NOTIFICATION,
                "新项目: " + p_name + " 创建成功");
    }

    public static Notification QUIT_P(String p_name) {
        return noti(2, SYSTEM_NOTIFICATION,
            "您退出了项目: " + p_name);
    }
    public static Notification DEL_P(String p_name) {
        return noti(2, SYSTEM_NOTIFICATION,
                "项目: " + p_name + " 已被删除");
    }


    public static Notification CREATE_P_LAB(String from) {
        return noti(0, from, "实验室收到一条项目申请请求，请尽快审核");
    }

    public static Notification UPDATE_P(String from) {
        return noti(1, from, "实验室收到一条项目修改请求，请尽快查看");
    }

    public static Notification CANCEL_P(String from) {
        return noti(3, from, "实验室收到一条取消项目请求，请尽快审核");
    }

    public static final Notification UPDATED = noti(1, SYSTEM_NOTIFICATION,
            "您申请的项目修改已被同意并生效");
    public static final Notification UPDATE_FAIL = noti(1, SYSTEM_NOTIFICATION,
            "您申请的项目修改没有通过或已取消");



}