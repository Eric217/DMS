package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.model.Laboratory;
import src.service.LabService;
import src.service.PermissionService;

import javax.servlet.http.HttpSession;

import static src.base.ResultCache.*;

@RestController
@RequestMapping(value = "/lab")
public class LabController {

    @Autowired
    LabService labService;

    /** 已登陆权限：查看实验室列表 */
    @RequestMapping(value = "/get/all", method = RequestMethod.GET)
    public Result getAllLabs(HttpSession session) {
        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        return labService.getAllLabs();
    }

    /** 已登陆权限：查看实验室名字列表 */
    @RequestMapping(value = "/get/name", method = RequestMethod.GET)
    public Result getAllLabNames(HttpSession session) {
        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        return labService.getAllLabNames();
    }

    /** 已登陆权限：查看实验室信息 */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getLabById(Long id, HttpSession session) {
        if (id == null)
            return ARG_ERROR;
        if (!PermissionService.IS_LOGIN(session))
            return PERMISSION_DENIED;
        return labService.getLabByLabId(id);
    }

    /** admin 权限*/
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createLab(Laboratory vo, HttpSession session) {
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        if (vo == null || !vo.check())
            return ARG_ERROR;
        return labService.insertLab(vo);
    }

    /** admin 权限*/
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result deleteLabById(Long id, HttpSession session) {
        if (id == null)
            return ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            return PERMISSION_DENIED;
        return labService.deleteLabById(id);
    }

    /** 更新权限：实验室负责人、管理员 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateLab(Laboratory vo, HttpSession session) {
        if (vo == null || !vo.check() || vo.getId() == null)
            return ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            if (!PermissionService.IS_MY_LAB(vo.getId(), session, labService))
                return PERMISSION_DENIED;
        return labService.updateLab(vo);
    }

}
