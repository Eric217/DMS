package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Bulletin;

import java.util.List;

@Repository
public interface BulletinDAO {

    List<Bulletin> getBulletins();

    Bulletin getBulletinById(Long id);

    void insert(Bulletin vo);

    void update(Bulletin vo);

    void deleteById(Long id);


}
