package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.service.NotificationService;

@RestController
@RequestMapping(value = "/noti")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getNotificationBySid(String sid) {
        return notificationService.getNotificationsBySid(sid);
    }



}
