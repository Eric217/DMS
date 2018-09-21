package src.dao;


import org.springframework.stereotype.Repository;
import src.model.Laboratory;

import java.util.List;

@Repository
public interface LabDAO {

    Laboratory getLabByLeaderId(String sid);

    Laboratory getLabById(Long id);

    List<Laboratory> getAllLabs();

    void insertLab(Laboratory vo);

    void updateLab(Laboratory vo);

    void deleteLabById(Long id);

}
