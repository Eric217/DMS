package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.model.Bulletin;
import src.service.BulletinService;

@RestController
@RequestMapping (value = "/bulletin")
public class BulletinController {

    @Autowired
    BulletinService bulletinService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Result getBulletins() {
        return bulletinService.getBulletins();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createBulletin(Bulletin vo) {
        return bulletinService.insertBulletin(vo);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getOneById(Long id) {
        return bulletinService.getBulletinById(id);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result deleteBulletinById(Long id) {
        return bulletinService.delete(id);

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateBulletin(Bulletin vo) {
        return bulletinService.update(vo);
    }

}
