package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.StudentDAO;
import src.model.Student;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentService {

    @Autowired
    StudentDAO studentDAO;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Result getStudentById(String id) {
        Student s = studentDAO.getStudentById(id);
        return ResultCache.getDataOk(s);
    }

    public Result insertStudent(Student vo)   {
        studentDAO.insertStudent(vo);
        return ResultCache.OK;
    }

    public Boolean emailExist(String email) {
        if (studentDAO.checkMailExisted(email) != 0)
            return true;
        return false;
    }

    public String encodePassword(String origin_password) {
        return passwordEncoder.encode(origin_password);
    }

    public Boolean passwordRight(String sid, String input_password) {
        String encoded = studentDAO.getEncodedPassword(sid);
        if (encoded == null || encoded.isEmpty()) {
            return false;
        }
        return passwordEncoder.matches(input_password, encoded);
    }
     
    public Result updateStudent(Student vo)   {
        studentDAO.updateStudent(vo);
        return ResultCache.OK;
    }

    @Transactional
    public Result deleteStudents(Set<String> ids)   {
        for (String id: ids) {
            studentDAO.deleteStudent(id);
        }
        return ResultCache.OK;
    }

}
