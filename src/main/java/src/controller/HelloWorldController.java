package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import src.base.Result;
import org.springframework.web.bind.annotation.RestController;
import src.base.ResultCache;
import src.model.Admin;
import src.model.Project;
import src.model.Student;
import src.service.ProjectService;
import src.service.StudentService;

import javax.servlet.http.HttpSession;

@RestController
public class HelloWorldController {

    @Autowired
    StudentService studentService;

    @Autowired
    ProjectService projectService;



    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public Result hello(String id) {

        return studentService.getStudentById(id);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.GET)
    public Result hi(Project admin, Student test, HttpSession session) {
        session.setAttribute("test", "yes");
        Admin a = new Admin();
        a.setId(admin.getId());
        a.setPassword(test.getPassword());
        return ResultCache.getDataOk(a);
    }

    @RequestMapping(value = "/student/project/create", method = RequestMethod.GET)
    public Result createProject(Project vo, String id1, String id2, String id3,
                                String id4, String id5, String id6) {

        return projectService.insert(vo, id1, id2, id3, id4, id5, id6);
    }



}
