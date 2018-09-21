package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.model.Laboratory;
import src.service.LabService;

@RestController
@RequestMapping(value = "/lab")
public class LabController {

    @Autowired
    LabService labService;

    @RequestMapping(value = "/get/leader", method = RequestMethod.GET)
    public Result getLabByLeaderId(String sid) {
        return labService.getLabByLeaderId(sid);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getLabById(Long id) {
        return labService.getLabById(id);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createLab(Laboratory vo) {
        return labService.insertLab(vo);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result deleteLabById(Long id) {
        return labService.deleteLabById(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateLab(Laboratory vo) {
        return labService.updateLab(vo);
    }

    @RequestMapping(value = "/get/all", method = RequestMethod.GET)
    public Result getAllLabs() {
        return labService.getAllLabs();
    }

}
