package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.model.Student;
import src.service.StudentService;

/** 部分控制位于 UserController */
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
        studentService.updateStudent(s);
        return ResultCache.OK;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Result getAllSplit(Integer page, Integer rows) {
        return studentService.getStudentSplit(page, rows);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Result getCount() {
        return studentService.getAllCount();
    }



}
