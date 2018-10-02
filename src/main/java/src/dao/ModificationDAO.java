package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Modification;

@Repository
public interface ModificationDAO {

    Modification getModByProjectId(Long id);

    void insertModification(Modification vo);

    void deleteModById(Long id);

}
