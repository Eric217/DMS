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

}
