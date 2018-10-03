package src.controller;

import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.eric.Tools;
import src.model.Laboratory;
import src.model.Modification;
import src.model.Project;
import src.model.assistance.ProjectStatusValue;
import src.service.LabService;
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

    @Autowired
    LabService labService;


    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public Result submitModification(Modification modification, HttpSession session) {
        if (modification == null || modification.getPid() == null|| !modification.check())
            return ResultCache.ARG_ERROR;
        Project p = (Project) modificationService.getProjectForModById(modification.getPid())
                .getData();
        if (p == null)
            return ResultCache.failWithMessage("原项目不存在或读取数据库失败");
        if (!PermissionService.IS_ADMIN(session))
            if (StringUtils.isNullOrEmpty(p.getLeader_id()) || !p.getLeader_id()
                        .equals(PermissionService.SID(session)))
                return ResultCache.PERMISSION_DENIED;
        if (p.status() != ProjectStatusValue.S_PROCESSING)
            return ResultCache.failWithMessage("项目当前状态不可提交修改");

        return modificationService.insert(modification, p);
    }

    // TODO: - 劳资不想写了！！！ 不判断权限、null 什么的了！！

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

    /** 取消/拒绝 修改 */
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public Result recallModification(Long mid, Long pid, HttpSession session) {
        if (mid == null || pid == null)
            return ResultCache.ARG_ERROR;
        Project p = (Project) modificationService.getProjectForModById(pid).getData();
        if (p == null)
            return ResultCache.failWithMessage("原项目不存在或读取数据库失败");
        if (!PermissionService.IS_ADMIN(session))
            if (StringUtils.isNullOrEmpty(p.getLeader_id()) || !p.getLeader_id()
                    .equals(PermissionService.SID(session))) {
                Laboratory lab = PermissionService.getManagedLab(session, labService);
                if (lab == null || !lab.getName().equals(p.getLab_name()))
                    return ResultCache.PERMISSION_DENIED;
            }
        return modificationService.recallModification(mid, p);
    }

}
