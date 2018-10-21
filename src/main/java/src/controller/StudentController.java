package src.controller;

import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.eric.Tools;
import src.model.Student;
import src.service.PermissionService;
import src.service.StudentService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

/** 插入、改密码 位于 UserController */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    /** 权限：自己，管理员 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateStudent(Student s, HttpSession session) {
        if (s == null || Tools.isNullOrTrimEmp(s.getId(), s.getName()))
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session))
            if (!PermissionService.IS_CURRENT_USER(s.getId(), session))
                return ResultCache.PERMISSION_DENIED;
        return studentService.updateStudent(s);
    }

    /** 权限：管理员
     *  @param sids 以 @ 分隔要删除的 id */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result deleteStudents(String sids, HttpSession session) {
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        if (StringUtils.isNullOrEmpty(sids))
            return ResultCache.ARG_ERROR;
        HashSet<String> ids = Tools.split(sids, "@", "");
        return studentService.deleteStudents(ids);
    }

    /** 权限：自己，管理员 */
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getStudentById(String id, HttpSession session) {
        if (!PermissionService.IS_ADMIN(session))
            if (!PermissionService.IS_CURRENT_USER(id, session))
                return ResultCache.PERMISSION_DENIED;
        if (StringUtils.isNullOrEmpty(id))
            return ResultCache.ARG_ERROR;
        return studentService.getStudentById(id, true);
    }

    /** 权限：管理员 */
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Result getAllStudentSplit(Integer page, Integer rows, HttpSession session) {
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        return studentService.getStudentSplit(page, rows);
    }

    @RequestMapping(value = "/all/count", method = RequestMethod.GET)
    public Result getAllCount(HttpSession session) {
        if (!PermissionService.IS_ADMIN(session))
            return ResultCache.PERMISSION_DENIED;
        return studentService.getAllCount();
    }

}
