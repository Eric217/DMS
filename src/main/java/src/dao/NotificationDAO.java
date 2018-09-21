package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Notification;

import java.util.List;

@Repository
public interface NotificationDAO {


    List<Notification> getNotificationsBySid(String sid);





}
