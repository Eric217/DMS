package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import src.base.Result;
import src.base.ResultCache;
import src.dao.BulletinDAO;
import src.model.Bulletin;

import java.util.List;


@Service
public class BulletinService {

    @Autowired
    BulletinDAO bulletinDAO;

    public Result getBulletins() {
        List<Bulletin> list = bulletinDAO.getBulletins();
        return ResultCache.getDataOk(list);
    }

    public Result getBulletinById(Long id) {
        return ResultCache.getDataOk(bulletinDAO.getBulletinById(id));
    }

    public Result insertBulletin(Bulletin vo) {
        bulletinDAO.insert(vo);
        return ResultCache.OK;
    }

    public Result update(Bulletin vo) {
        bulletinDAO.update(vo);
        return ResultCache.OK;
    }

    public Result delete(Long id) {
        bulletinDAO.deleteById(id);
        return ResultCache.OK;
    }


}
