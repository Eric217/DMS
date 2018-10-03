package src.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.NotificationDAO;
import src.eric.Tools;
import src.model.Notification;

import java.util.List;
import java.util.Set;

@Service
public class NotificationService {

    @Autowired
    NotificationDAO notificationDAO;

//    @Transactional
//    public Result notifyLabLeaderByLabName(String lab_name, Notification vo) {
//        try {
//            String l_sid = notificationDAO.getLabLeaderIdByLabName(lab_name);
//            if (l_sid == null)
//                return;
//            HashSet<String> set = new HashSet<>();
//            set.add(l_sid);
//            return notifyStudentsByIds(set, vo);
//        } catch (Exception e) {
//            return ResultCache.DATABASE_ERROR;
//        }
//    }

    @Transactional
    public Result notifyStudentsByIds(Set<String> sids, Notification vo) {
        try {
            _notifyMembersByIds(sids, vo);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    // in package:
    void _notifyMembersByIds(Set<String> sids, Notification vo) {
        if (sids.size() > 0) {
            notificationDAO.insertNotification(vo);
            Long nid = vo.getId();
            for (String sid : sids)
                notificationDAO.notify(sid, nid);
        }
    }
    void _notifyLabLeaderOfLabName(String lab_name, Notification vo) {
        String l_sid = notificationDAO.getLabLeaderIdByLabName(lab_name);
        if (l_sid == null)
            return;
        _notifyMembersByIds(Tools.toSet(l_sid), vo);
    }

    public Result getNotificationsBySid(String sid) {
        try {
            return ResultCache.getDataOk(notificationDAO.getNotificationsBySid(sid));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result markAsReadNoti(Long nid, String sid) {
        try {
            removeOne(nid, sid);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    private void removeOne(Long nid, String sid) {
        notificationDAO.deleteNotify(nid, sid);
        Integer unread = notificationDAO.getUnreadCountOfNoti(nid);
        if (unread == 0)
            notificationDAO.deleteNotiById(nid);
    }

    @Transactional
    public Result markAllAsRead(String sid) {
        try {
            List<Long> notiIds = notificationDAO.getNotificationIdsBySid(sid);
            for (Long nid: notiIds)
                removeOne(nid, sid);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }


}
