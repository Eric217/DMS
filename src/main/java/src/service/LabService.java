package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.base.Result;
import src.base.ResultCache;
import src.dao.LabDAO;

@Service
public class LabService {

    @Autowired
    LabDAO labDAO;

    public Result getLabByLeaderId(String sid) {
        if (sid == null)
            return ResultCache.OK;
        return ResultCache.getDataOk(labDAO.getLabByLeaderId(sid));
    }

}
