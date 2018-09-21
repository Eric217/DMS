package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.base.Result;
import src.base.ResultCache;
import src.dao.LabDAO;
import src.model.Laboratory;

@Service
public class LabService {

    @Autowired
    LabDAO labDAO;

    public Result getLabByLeaderId(String sid) {
        if (sid == null)
            return ResultCache.OK;
        return ResultCache.getDataOk(labDAO.getLabByLeaderId(sid));
    }

    public Result getLabById(Long id) {
        return ResultCache.getDataOk(labDAO.getLabById(id));
    }

    public Result getAllLabs() {
        return ResultCache.getDataOk(labDAO.getAllLabs());
    }


    public Result insertLab(Laboratory vo) {
        labDAO.insertLab(vo);
        return ResultCache.OK;
    }

    public Result updateLab(Laboratory vo) {
        labDAO.updateLab(vo);
        return ResultCache.OK;
    }

    public Result deleteLabById(Long id) {
        labDAO.deleteLabById(id);
        return ResultCache.OK;
    }

}
