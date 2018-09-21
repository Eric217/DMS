package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.service.BulletinService;

@RestController
@RequestMapping (value = "/bull")
public class BulletinController {

    @Autowired
    BulletinService bulletinService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getBulletins() {
        return bulletinService.getBulletins();
    }

}
