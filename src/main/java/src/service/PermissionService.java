package src.service;

import src.base.Result;
import src.model.Laboratory;

import javax.servlet.http.HttpSession;
import static src.base.SessionNames.S_IS_ADMIN;
import static src.base.SessionNames.S_USERNAME;

/** 主要为学生权限声明的一个类。（其实管理员的权限永远不会改变，直接用 session 可以解决） */

public class PermissionService {


    public static void GRANT_ADMIN(HttpSession session) {
        session.setAttribute(S_IS_ADMIN, true);
    }
    public static void GRANT_USER(HttpSession session, String sid) {
        session.setAttribute(S_USERNAME, sid);
    }

    public static boolean IS_ADMIN(HttpSession session) {
        Boolean isAdmin = (Boolean) session.getAttribute(S_IS_ADMIN);
        if (isAdmin == null)
            return false;
        return isAdmin;
    }

    public static boolean IS_LOGIN(HttpSession session) {
        return session.getAttribute(S_USERNAME) != null;
    }

    /** 已登陆用户当前管理的实验室 */
    public static Laboratory getManagedLab(HttpSession session, LabService labService) {
        Object result =
                labService.getLabByLeaderId((String)session.getAttribute(S_USERNAME)).getData();
        return result == null ? null : (Laboratory)result;
    }

    /** 该学生当前正在进行的项目ID */
    public static Long S_WORKING_P() {
        return 0l;
    }


    /** 烦啊！ 直接返回 true 了！！！ */
    public static boolean hasPermission(Long pid, String sid) {
        return true;
    }

}

