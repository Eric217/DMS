package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.model.Modification;
import src.service.ModificationService;
import src.service.PermissionService;
import src.service.ProjectService;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/modify")
public class ModificationController {

    @Autowired
    ModificationService modificationService;

    @Autowired
    ProjectService projectService;

    // TODO: - 劳资不想写了！！！ 不判断权限、null 什么的了！！

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public Result submitModification(Modification modification, HttpSession session) {

        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        // 判断 1、是不是已经在请求状态，2、是不是进行中的项目 3、 等等等等，不符合情理的就拒绝

        return modificationService.insert(modification);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getModificationByProjectId(Long pid, HttpSession session) {

        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;

        return modificationService.getModificationByPid(pid);
    }

    /** 管理员有可能修改了 modification 后点击同意，因此 */
    @RequestMapping(value = "/agree", method = RequestMethod.POST)
    public Result execModification(Long pid, HttpSession session) {

        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;

        return modificationService.execModification(pid);
    }

    /** 取消修改 */
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public Result recallModification(Long mid, Long pid, HttpSession session) {

        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        // 一系列判断
        return modificationService.recallModification(mid, pid);
    }

}
