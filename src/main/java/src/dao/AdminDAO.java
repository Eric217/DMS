package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Admin;

@Repository
public interface AdminDAO {

    String getEncodedPassword(Long id);

    void addAdministrator(Admin admin);

}
