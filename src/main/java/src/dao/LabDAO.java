package src.dao;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Laboratory;

import java.util.List;

@Repository
public interface LabDAO {

    /** return min */
    Laboratory getLabByLeaderId(String sid);

    Laboratory getLabById(Long id);

    List<Laboratory> getAllLabs();
    List<String> getAllLabNames();

    void insertLab(Laboratory vo);

    void updateLab(Laboratory vo);

    void deleteLabById(Long id);

    Integer containsProject(@Param("lid") Long lid, @Param("pid") Long pid);

}
