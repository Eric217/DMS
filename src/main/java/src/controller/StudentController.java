package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.model.Student;
import src.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentService studentService;

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getStudentById(String id) {
        return studentService.getStudentById(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateStudent(Student s) {

        return ResultCache.OK;
    }


}
