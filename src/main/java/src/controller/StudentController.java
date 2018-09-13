package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.service.StudentService;

@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @RequestMapping(value = "/student/get", method = RequestMethod.GET)
    public Result getStudentById(String id) {
        return studentService.getStudentById(id);
    }

}
