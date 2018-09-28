package src.service;

import com.mysql.cj.util.StringUtils;
import src.base.Result;
import src.base.ResultCache;
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

    public static String SID(HttpSession session) {
        return (String) session.getAttribute(S_USERNAME);
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

    public static boolean IS_CURRENT_USER(String id, HttpSession session) {
        String s = (String) session.getAttribute(S_USERNAME);
        if (s == null)
            return false;
        return s.equals(id);
    }

    public static boolean IS_MY_LAB(Long lab_id, HttpSession session, LabService service) {
        Laboratory lab = getManagedLab(session, service);
        return lab != null && lab.getId().equals(lab_id);
    }

    /** 已登陆用户当前管理的实验室 */
    public static Laboratory getManagedLab(HttpSession session, LabService labService) {
        var sid = SID(session);
        if (StringUtils.isNullOrEmpty(sid))
            return null;
        Object result = labService.getLabByLeaderId(sid).getData();
        return result == null ? null : (Laboratory)result;
    }

    /** @return sid */
    public static String RequireStudentOnly(HttpSession session) {
        if (IS_ADMIN(session))
            return null;
        return (String) session.getAttribute(S_USERNAME);
    }









    /** 该学生当前正在进行的项目ID */
    public static Long S_WORKING_P() {
        return 0l;
    }


    /** 烦啊！ 直接返回 true 了！！！ */
    public static boolean hasPermission(Long pid, String sid) {
        return true;
    }

    public static boolean RequireAdmin(HttpSession session) {


        return false;
    }

    public static boolean RequireNone() {


        return true;
    }

    public static boolean RequireProjectLeader(HttpSession session) {


        return false;
    }

    public static boolean RequireParticipation(HttpSession session) {


        return false;
    }

    public static Result RequireLabLeader(HttpSession session) {

        return ResultCache.PERMISSION_DENIED;
    }


}

