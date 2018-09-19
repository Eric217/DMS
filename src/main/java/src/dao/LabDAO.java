package src.dao;

import org.springframework.stereotype.Repository;
import src.model.Laboratory;

@Repository
public interface LabDAO {

    Laboratory getLabByLeaderId(String sid);
//    Laboratory getSimplifiedLabByLeaderId

}
