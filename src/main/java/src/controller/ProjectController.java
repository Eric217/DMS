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

//    @RequestMapping(value = "/student/project/count", method = RequestMethod.GET)
//    public Result getCount_s(String property, String like) {
//        return projectService.getCount(property, like, false);
//    }
//
//    @RequestMapping(value = "/admin/project/count", method = RequestMethod.GET)
//    public Result getCount_a(String property, String like) {
//        return projectService.getCount(property, like, true);
//    }

    /** @param vo 具体需要一些 not null 的参数
     *  @param memberIds 以 @ 分割，除 leader 外至少一个人 */
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
        if (!vo.check() || StringUtils.isNullOrEmpty(memberIds))
            return ResultCache.failWithMessage("必要信息不能为空或格式错误");

        String[] sids = memberIds.split("@");
        HashSet<String> set = new HashSet<>();
        for (String id: sids) {
            if (!id.isEmpty() && !id.equals(sid))
                set.add(id);
        }
        sids = (String[]) set.toArray();
        if (sids.length == 0)
            return ResultCache.failWithMessage("至少需要一个组员");
        return projectService.insert(vo, sids);
    }

    /** 查看某个学生的所有项目，权限：admin，或自己的 */
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

    /** 查看某一个很详细的 Project */
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
        for (Student s: p.getMembers()) {
            if (s.getId().equals(sid))
                return r;
        }
        return ResultCache.PERMISSION_DENIED;
    }

    /** 查看某个实验室的 Projects
     *  @param status 取 进行中，待审核，请求中，所有，只有在-所有-情况下才需要 page rows 参数 */
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

    /** 对于管理人员, 获取任意状态的项目 */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Result getProjectsAdmin(Integer page, Integer rows, Integer status,
                                   HttpSession session) {
        if (status < 0 || status > 7)
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        return projectService.getProjectsAdmin(page==null ?1:page, rows==null ?10:rows, status);
    }

    // TODO: - 权限
    // TODO: - 所有的参数的 null 判断

    /* 更新分两种，一种是主动更新，一种是从 modification 中同意
     * 权限：管理员、该项目所在实验室负责人 */
    /** 主动更新 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateProject(Project vo, HttpSession session) {
        if (vo == null || vo.getId() == null || Tools.isNullOrEmp(vo.getName(), vo.getAim(),
                vo.getLab_name()))
            return ResultCache.ARG_ERROR;

        return projectService.updateProject(vo);
    }

    /** 同意修改 */
    @RequestMapping(value = "/modify", method = RequestMethod.POST)
    public Result updateProject(Modification modification) {
//        if (vo == null || vo.getId() == null || Tools.isNullOrEmp(vo.getName(), vo.getAim(),
//                vo.getLab_name()))
//            return ResultCache.ARG_ERROR;
//
//        return projectService.updateProject(vo);
        return null;
    }

    /** 普通用户删除，实际没有删除，而是更新了一个属性 */
    @RequestMapping(value = "/delete/student", method = RequestMethod.POST)
    public Result updateDeleted(Long id, Integer newValue) {
        Set<Long> s = new HashSet<>();
        s.add(id);
        return projectService.updateDeleted(s, newValue);
    }
    /** 删除多个，参数样式：233@445@663 的字符串 */
    @RequestMapping(value = "/student/project/multi_delete", method = RequestMethod.POST)
    public Result updateDeleted_m(String ids, Integer newValue) {
        Set<Long> s; s = new HashSet<>();

        String[] arr = ids.split("@");
        for (String id: arr) {
            if (!id.isEmpty())
                s.add(Long.parseLong(id));
        }
        return projectService.updateDeleted(s, newValue);
    }

    /** 按照 id 删除一个项目 */
    @RequestMapping(value = "/admin/project/delete", method = RequestMethod.POST)
    public Result delete(Long id) {
        Set<Long> s = new HashSet<>();
        s.add(id);
        return projectService.deleteProjects(s);
    }

    /** 删除多个，参数样式：233|445|663 的字符串 */
    @RequestMapping(value = "/admin/project/multi_delete", method = RequestMethod.POST)
    public Result delete_m(String ids) {
        Set<Long> s; s = new HashSet<>();
        String[] arr = ids.split("|");
        for (String id: arr) {
            if (!id.isEmpty())
                s.add(Long.parseLong(id));
        }
        return projectService.deleteProjects(s);
    }

}
