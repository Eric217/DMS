package src.controller;

import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import src.base.Result;
import org.springframework.web.bind.annotation.RestController;
import src.base.ResultCache;
import src.eric.Tools;
import src.model.Laboratory;
import src.model.Modification;
import src.model.Project;
import src.model.Student;
import src.service.LabService;
import src.service.PermissionService;
import src.service.ProjectService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

import static src.model.assistance.ProjectStatusValue.*;

@RestController
@RequestMapping(value = "/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @Autowired
    LabService labService;

    /**
     * 权限：admin，或自己的实验室
     */
    @RequestMapping(value = "/count/lab", method = RequestMethod.GET)
    public Result getCountLab(Long lab_id, HttpSession session) {
        if (lab_id == null)
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            if (!PermissionService.IS_MY_LAB(lab_id, session, labService))
                return ResultCache.PERMISSION_DENIED;

        return projectService.getCountOfLab(lab_id);
    }

    @RequestMapping(value = "/count/status", method = RequestMethod.GET)
    public Result getCountAdmin(Integer status, HttpSession session) {
        if (status == null || status < 0 || status > 6)
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        return projectService.getAllCountOfStatus(status);
    }

    /**
     * @param vo        具体需要一些 not null 的参数
     * @param memberIds 以 @ 分割，除 leader 外至少一个人
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createProject(Project vo, String memberIds, HttpSession session) {
        String sid = PermissionService.SID(session);
        if (StringUtils.isNullOrEmpty(sid))
            return ResultCache.PERMISSION_DENIED;
        if (PermissionService.IS_ADMIN(session))
            return ResultCache.failWithMessage("管理员不参与项目");
        if (vo == null)
            return ResultCache.ARG_ERROR;
        vo.setLeader_id(sid);
        if (!vo.check() || vo.getLab_name() == null)
            return ResultCache.failWithMessage("必要信息不能为空或格式错误");
        HashSet<String> sids = Tools.split(memberIds, "@", sid);
        if (sids.size() == 0)
            return ResultCache.failWithMessage("至少需要一个组员");
        return projectService.insert(vo, sids);
    }

    /**
     * 查看某个学生的所有项目，权限：admin，或自己的
     */
    @RequestMapping(value = "/student", method = RequestMethod.GET)
    public Result getProjectsByStudentId(String sid, HttpSession session) {
        if (sid == null || sid.isEmpty())
            return ResultCache.ARG_ERROR;
        if (PermissionService.IS_ADMIN(session))
            return projectService.getProjectsByStudentId(sid, true);
        if (PermissionService.IS_CURRENT_USER(sid, session))
            return projectService.getProjectsByStudentId(sid, false);
        return ResultCache.PERMISSION_DENIED;
    }

    /**
     * 查看某一个很详细的 Project
     */
    @RequestMapping(value = "/one", method = RequestMethod.GET)
    public Result getProjectById(Long id, HttpSession session) {
        String sid = PermissionService.SID(session);
        if (StringUtils.isNullOrEmpty(sid))
            return ResultCache.PERMISSION_DENIED;
        if (id == null)
            return ResultCache.ARG_ERROR;
        Result r = projectService.getProjectById(id);
        if (PermissionService.IS_ADMIN(session))
            return r;
        Project p = (Project) r.getData();
        if (p == null)
            return r;
        Laboratory lab = PermissionService.getManagedLab(session, labService);
        if (lab != null && lab.getName().equals(p.getLab_name()))
            return r;
        if (p.getDeleted() == 1)
            return ResultCache.failWithMessage("项目不存在");
        for (Student s : p.getMembers()) {
            if (s.getId().equals(sid))
                return r;
        }
        return ResultCache.PERMISSION_DENIED;
    }

    /**
     * 查看某个实验室的 Projects
     *
     * @param status 取 进行中，待审核，请求中，所有，只有在-所有-情况下才需要 page rows 参数
     */
    @RequestMapping(value = "/lab", method = RequestMethod.GET)
    public Result getProjectsByLabId(Integer page, Integer rows, Integer status, Long lab_id,
                                     HttpSession session) {
        if (lab_id == null || status == null || (status != S_PROCESSING &&
                status != S_CREATING && status != S_REQUESTING && status != S_ALL))
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            if (!PermissionService.IS_MY_LAB(lab_id, session, labService))
                return ResultCache.PERMISSION_DENIED;
        if (status == S_ALL)
            return projectService.getAllProjectsByLabId(page == null ? 1 : page,
                    rows == null ? 10 : rows, lab_id);
        return projectService.getProjectsWithStatusByLabId(status, lab_id);
    }

    /**
     * 对于管理人员, 获取任意状态的项目
     */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Result getProjectsAdmin(Integer page, Integer rows, Integer status,
                                   HttpSession session) {
        if (status == null || status < 0 || status > 6)
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        return projectService.getProjectsAdmin(page == null ? 1 : page, rows == null ? 10 : rows, status);
    }

    /* 更新分两种，一种是主动更新，一种是从 modification 中同意
     * 权限：管理员、该项目所在实验室负责人 */

    /**
     * 主动更新，分好几种，有空再改；不允许更改所在实验室、项目组长
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateProject(Project vo, String memberIds, Boolean notify,
                                HttpSession session) {
        if (vo == null || vo.getId() == null || !vo.check())
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session)) {
            Laboratory lab = PermissionService.getManagedLab(session, labService);
            try {
                if (lab == null || !labService.containsProject(lab.getId(), vo.getId()))
                    return ResultCache.PERMISSION_DENIED;
            } catch (Exception e) {
                return ResultCache.DATABASE_ERROR;
            }
        }
        if (notify == null)
            notify = false;
        Set<String> set = Tools.split(memberIds, "@",
                projectService.getLeaderIdByPid(vo.getId()));
        if (set.size() == 0)
            return ResultCache.ARG_ERROR;
        return projectService.updateProject(vo, set, notify);
    }

    /**
     * 普通用户删除，每次只允许删除一个，实际没有删除，而是更新了一个属性
     */
    @RequestMapping(value = "/delete/student", method = RequestMethod.POST)
    public Result updateDeleted(Long pid, Integer newValue, HttpSession session) {
        if (pid == null)
            return ResultCache.ARG_ERROR;
        if (newValue == null)
            newValue = 1;
        if (!PermissionService.IS_ADMIN(session)) {
            if (newValue == 0)
                return ResultCache.PERMISSION_DENIED;
            String sid = PermissionService.SID(session);
            String lid = projectService.getLeaderIdByPid(pid);
            if (StringUtils.isNullOrEmpty(lid))
                return ResultCache.ARG_ERROR;
            if (sid == null || !sid.equals(lid))
                return ResultCache.PERMISSION_DENIED;
            // TODO: - 判断项目状态，只有非活跃状态的才可以"删除"
        }
        return projectService.updateDeleted(Tools.toSet(pid), newValue);
    }

    /**
     * 实验室负责人、管理员删除多个，ids 以 @ 分隔
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result delete_multi(String ids, HttpSession session) {
        if (StringUtils.isNullOrEmpty(ids))
            return ResultCache.ARG_ERROR;
        Set<Long> s = Tools.split_to_long(ids, "@", null);

        if (!PermissionService.IS_ADMIN(session)) {
            Laboratory lab = PermissionService.getManagedLab(session, labService);
            if (lab == null)
                return ResultCache.PERMISSION_DENIED;
            try {
                for (Long pid : s)
                    if (!labService.containsProject(lab.getId(), pid))
                        return ResultCache.PERMISSION_DENIED;
            } catch (Exception e) {
                return ResultCache.DATABASE_ERROR;
            }
        }
        return projectService.deleteProjects(s);
    }

}
