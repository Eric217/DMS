package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Modification;

@Repository
public interface ModificationDAO {


    Modification getModifiByLeaderId(String sid);

    Modification getModifiById(Long id);

    void insertModifi(Modification vo);

    void updateModifi(Modification vo);

    void deleteModifiById(Long id);
    
    
}
