package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.eric.Constant;
import src.eric.Tools;
import src.model.Notification;
import src.service.NotificationService;
import src.service.PermissionService;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/noti")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    /** 直接按照 sid 查询收到的通知（没有给管理员发通知的需求） */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getNotificationById(HttpSession session) {
        String id = PermissionService.SID(session);
        if (id == null)
            return ResultCache.PERMISSION_DENIED;
        return notificationService.getNotificationsBySid(id);
    }

    /*  通知的发送一般是某种操作被动产生的。其他情况作为本系列接口：
     *  实验室负责人给实验室内所有人发通知；
     *  实验室负责人给实验室内某个项目（组长）通知；
     *  管理员 除上述权限，可以给 任意几个人 发通知，所有人就等于公告了 */
    /** 仅限管理员：
     *  @param to 以 @ 分隔 */
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public Result postNotiToAny(String to, Notification vo, HttpSession session) {
        if (vo == null || Tools.isNullOrEmp(to, vo.getContent()))
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        vo.setFrom(Constant.SYSTEM_ADMIN);
        vo.setType(Constant.SYSTEM_NOTIFICATION);
        return notificationService.notifyStudentsByIds(
                Tools.split(to, "@", null), vo);
    }

    @RequestMapping(value = "/post2", method = RequestMethod.POST)
    public Result postNotiToProject(Long pid, Notification vo, HttpSession session) {
//        if (vo == null || Tools.isNullOrEmp(to, vo.getContent()))
//            return ResultCache.ARG_ERROR;
//        if (!PermissionService.IS_ADMIN(session))
//            return ResultCache.PERMISSION_DENIED;
//        vo.setFrom(Constant.SYSTEM_ADMIN);
//        vo.setType(Constant.SYSTEM_NOTIFICATION);
//        return notificationService.notifyStudentsByIds(
//                Tools.split(to, "@", null), vo);
        return null;
    }


    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public Result readNotification(Long nid, HttpSession session) {
        if (nid == null)
            return ResultCache.ARG_ERROR;
        String id = PermissionService.SID(session);
        if (id == null)
            return ResultCache.PERMISSION_DENIED;
        return notificationService.markAsReadNoti(nid, id);
    }

    @RequestMapping(value = "/read/all", method = RequestMethod.POST)
    public Result readAllNotification(HttpSession session) {
        String id = PermissionService.SID(session);
        if (id == null)
            return ResultCache.PERMISSION_DENIED;
        return notificationService.markAllAsRead(id);
    }


}
