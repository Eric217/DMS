package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Notification;

import java.util.List;

@Repository
public interface NotificationDAO {

    List<Notification> getNotificationsBySid(String sid);

    List<Long> getNotificationIdsBySid(String sid);

    void deleteNotify(@Param("nid") Long nid, @Param("sid") String sid);

    void deleteNotiById(Long nid);

    Integer getUnreadCountOfNoti(Long nid);

    void insertNotification(Notification vo);

    void notify(@Param("sid") String sid, @Param("nid") Long nid);

    // assist
    String getLabLeaderIdByLabName(String labName);

}
