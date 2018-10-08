package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import src.base.Result;
import src.base.ResultCache;
import src.dao.StudentDAO;
import src.model.Student;
import src.model.assistance.PageRowsMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentService {

    @Autowired
    StudentDAO studentDAO;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Encode
    public String encodePassword(String origin_password) {
        return passwordEncoder.encode(origin_password);
    }

    // Boolean. handle Exception outside
    public Boolean emailExist(String email) {
        return studentDAO.checkMailExisted(email) != 0;
    }

    public Boolean passwordRight(String sid, String input_password) {
        String encoded = studentDAO.getEncodedPassword(sid);
        if (encoded == null || encoded.isEmpty())
            return false;
        return passwordEncoder.matches(input_password, encoded);
    }

    // Business
    public Result updatePassword(String email, String newPass) {
        try {
            studentDAO.updatePassword(email, encodePassword(newPass));
            return ResultCache.OK;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result insertStudent(Student vo)   {
        try {
            studentDAO.insertStudent(vo);
            return ResultCache.OK;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result deleteStudents(Set<String> ids) {
        try {
            for (String id : ids)
                studentDAO.deleteStudent(id);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
        return ResultCache.OK;
    }

    public Result updateStudent(Student vo) {
        try {
            studentDAO.updateStudent(vo);
            return ResultCache.OK;
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getStudentById(String id, Boolean full) {
        try {
            if (full)
                return ResultCache.getDataOk(studentDAO.getStudentById(id));
            String[] arr = {id};
            return ResultCache.getDataOk(
                    studentDAO.getMinStudentByIds(arr).stream().findAny().orElseThrow());
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getStudentByIds(String[] ids) {
        try {
            List<Student> list = studentDAO.getMinStudentByIds(ids);
            return ResultCache.getDataOk(list);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getStudentSplit(Integer page, Integer rows) {
        try {
            return ResultCache.getDataOk(
                    studentDAO.getAllSplit(new PageRowsMap(rows, (page-1)*rows)));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getAllCount() {
        try {
            return ResultCache.getDataOk(studentDAO.getCount());
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

}
