package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import src.base.Result;
import org.springframework.web.bind.annotation.RestController;
import src.base.ResultCache;
import src.model.Project;
import src.service.ProjectService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

import static src.base.SessionNames.S_USERNAME;

@RestController
public class ProjectController {

    // 后台稳定性（防小人而已）
    // TODO: - 权限
    // TODO: - 所有的参数的 null 判断

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping(value = "/student/project/create", method = RequestMethod.POST)
    public Result createProject(Project vo, String id1, String id2, String id3,
                                String id4, String id5, String id6) {
        try {
            return projectService.insert(vo, id1, id2, id3, id4, id5, id6);
        } catch (Exception e) {
            return ResultCache.FAILURE;
        }
    }

    @RequestMapping(value = "/student/project/count", method = RequestMethod.GET)
    public Result getCount_s(String property, String like) {
        return projectService.getCount(property, like, false);
    }



    @RequestMapping(value = "/admin/project/count", method = RequestMethod.GET)
    public Result getCount_a(String property, String like) {
        return projectService.getCount(property, like, true);
    }

    //
    /** 普通学生 查看一种状态的 项目列表, 我们对 学生（普通+实验室负责人）不开放检索功能 */
    @RequestMapping(value = "/student/project/all", method = RequestMethod.GET)
    public Result getProject_s(Integer page, Integer rows, Integer status, HttpSession session) {
        if (page == null || rows == null) {
            page = 1; rows = 10;
        }
        String uid = (String)session.getAttribute(S_USERNAME);
        // TODO: - check permission
        return projectService.getParticipatedProjects(page, rows, status, uid);
    }

    @RequestMapping(value = "/student/project/managed", method = RequestMethod.GET)
    public Result getProject_m(Integer page, Integer rows, HttpSession session) {
        if (page == null || rows == null) {
            page = 1; rows = 10;
        }
        String uid = (String)session.getAttribute(S_USERNAME);
        System.out.println("uid = " + uid);
        return projectService.getProjectsManagedBySid(page, rows, uid, false);
    }

    @RequestMapping(value = "/admin/project/managed", method = RequestMethod.GET)
    public Result getProject_m(Integer page, Integer rows, String sid, HttpSession session) {
        if (page == null || rows == null) {
            page = 1; rows = 10;
        }
        String uid = (String)session.getAttribute(S_USERNAME);
        // TODO: - check permission
        return projectService.getProjectsManagedBySid(page, rows, sid, true);
    }


    /** 有管理职能的 查看一种状态的 项目列表
     *  有了 property 和 like，可以支持检索功能，按照 name，coach_id，lab_name，leader_id，type 之一进行 */
    @RequestMapping(value = "/admin/project/all", method = RequestMethod.GET)
    public Result getProject_a(Integer page, Integer rows, Integer status, String property,
                               String like) {
        if (page == null || rows == null) {
            page = 1; rows = 10;
        }
        return projectService.getProjects(page, rows, status, true, property, like);
    }

    /** @return 具体某一个很详细的 Project。既然有 id，肯定是看得见的，不用再分 viewDelete 情况 */
    @RequestMapping(value = "/student/project/get", method = RequestMethod.GET)
    public Result getProjectById(Long id) {
        return projectService.getProjectById(id);
    }

    /** 普通用户删除，实际没有删除，而是更新了一个属性 */
    @RequestMapping(value = "/admin/project/update", method = RequestMethod.POST)
    public Result updateProject(Project vo) {
        return projectService.updateProject(vo);
    }

    /** 普通用户删除，实际没有删除，而是更新了一个属性 */
    @RequestMapping(value = "/student/project/delete", method = RequestMethod.POST)
    public Result updateDeleted(Long id, Integer newValue) {
        Set<Long> s = new HashSet<>();
        s.add(id);
        return projectService.updateDeleted(s, newValue);
    }
    /** 删除多个，参数样式：233|445|663 的字符串 */
    @RequestMapping(value = "/student/project/multi_delete", method = RequestMethod.POST)
    public Result updateDeleted_m(String ids, Integer newValue) {
        Set<Long> s; s = new HashSet<>();
        String[] arr = ids.split("|");
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
