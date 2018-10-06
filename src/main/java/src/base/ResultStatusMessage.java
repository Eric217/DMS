package src.base;

public interface ResultStatusMessage {
    String M_OK = "操作成功";
    String M_FAILURE = "操作失败";
    String M_DATABASE_ERROR = "数据库错误";
    String M_ARG_ERROR = "数据库错误";
    String M_PERMISSION_DENIED = "会话已过期或没有权限，即将刷新页面刷新";
}

