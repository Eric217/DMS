package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Bulletin;
import src.model.assistance.PageRowsMap;

import java.util.List;

@Repository
public interface BulletinDAO {

    Integer getAllCount();

    List<Bulletin> getBulletins(PageRowsMap map);

    Bulletin getBulletinById(Long id);
    Bulletin getBulletinMinById(Long id);

    void insert(Bulletin vo);

    void update(Bulletin vo);

    void updateReadCount(Long id);

    void deleteById(Long id);

    Long getReadCount(Long id);
}
