package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import src.base.Result;
import src.base.ResultCache;
import src.dao.LabDAO;
import src.model.Laboratory;

@Service
public class LabService {

    @Autowired
    LabDAO labDAO;

    public boolean containsProject(Long lid, Long pid) {
        return (labDAO.containsProject(lid, pid) != 0);
    }

    Result getLabByLeaderId(String sid) {
        try {
            return ResultCache.getDataOk(labDAO.getLabByLeaderId(sid));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getLabByLabId(Long id) {
        try {
            return ResultCache.getDataOk(labDAO.getLabById(id));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getAllLabs() {
        try {
            return ResultCache.getDataOk(labDAO.getAllLabs());
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getAllLabNames() {
        try {
            return ResultCache.getDataOk(labDAO.getAllLabNames());
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result insertLab(Laboratory vo) {
        try {
            labDAO.insertLab(vo);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.failWithMessage("操作失败，可能的原因是：负责人学号不存在或" +
                    "已负责其他实验室；实验室名称已被占用；数据库异常");
        }
        return ResultCache.OK;
    }

    public Result updateLab(Laboratory vo) {
        try {
            labDAO.updateLab(vo);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.failWithMessage("操作失败，可能的原因是：负责人学号不存在或" +
                    "已负责其他实验室；实验室名称已被占用；数据库异常");
        }
        return ResultCache.OK;
    }

    public Result deleteLabById(Long id) {
        try {
            labDAO.deleteLabById(id);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
        return ResultCache.OK;
    }

}
