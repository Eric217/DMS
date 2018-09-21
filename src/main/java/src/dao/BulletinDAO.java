package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Bulletin;

import java.util.List;

@Repository
public interface BulletinDAO {

    List<Bulletin> getBulletins();




}
