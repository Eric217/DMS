package src.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.base.Result;
import src.base.ResultCache;
import src.dao.NotificationDAO;
import src.model.Notification;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    NotificationDAO notificationDAO;

    public Result getNotificationsBySid(String sid) {
        List<Notification> list = notificationDAO.getNotificationsBySid(sid);
        return ResultCache.getDataOk(list);
    }


}
