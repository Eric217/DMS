package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Modification;
import src.model.Project;

@Repository
public interface ModificationDAO {

    Modification getModByProjectId(Long id);

    void insertModification(Modification vo);

    void deleteModById(Long id);

    Project getProjectForModificationById(Long id);


}
