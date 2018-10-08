package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import src.base.Result;
import src.base.ResultCache;
import src.dao.BulletinDAO;
import src.model.Bulletin;
import src.model.assistance.PageRowsMap;

import java.util.List;
import java.util.Set;


@Service
public class BulletinService {

    @Autowired
    BulletinDAO bulletinDAO;

    public Result getAllBulletins(Integer page, Integer rows) {
        try {
            return ResultCache.getDataOk(
                    bulletinDAO.getBulletins(new PageRowsMap(rows, rows * (page - 1))));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getBulletinById(Long id, boolean min) {
        try {
            return ResultCache.getDataOk(min? bulletinDAO.getBulletinMinById(id)
                                            : bulletinDAO.getBulletinById(id));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result insertBulletin(Bulletin vo) {
        try {
            bulletinDAO.insert(vo);
            return ResultCache.OK;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result update(Bulletin vo) {
        try {
            bulletinDAO.update(vo);
            return ResultCache.OK;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result delete(Set<Long> ids) {
        try {
            for (Long id: ids)
                bulletinDAO.deleteById(id);
            return ResultCache.OK;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getAllCount() {
        try {
            return ResultCache.getDataOk(bulletinDAO.getAllCount());
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }


}
