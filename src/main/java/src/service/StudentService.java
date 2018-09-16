package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Result getStudentById(String id) {
        Student s = studentDAO.getStudentById(id);
        return ResultCache.getDataOk(s);
    }

    public Result insertStudent(Student vo)   {
        studentDAO.insertStudent(vo);
        return ResultCache.OK;
    }

    public Boolean emailExist(String email) {
        Integer i = studentDAO.checkMailExisted(email);
        if (i != 0)
            return true;
        return false;
    }
     
    public boolean update(Student vo)   {
//        Connection c = null;
//        try {
//            c = ConnectionFactory.shared().makeConnection();
//            return DAOFactory.getStudentDAO(c).doUpdate(vo);
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            if (c != null) c.close();
//        }
        return false;
    }

     
    public boolean delete(Set<String> ids)   {
//        Connection c = null;
//        try {
//            c = ConnectionFactory.shared().makeConnection();
//            return DAOFactory.getStudentDAO(c).doRemoveBatch(ids);
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            if (c != null) c.close();
//        }
        return false;
    }

     


     
    public List<Student> list()   {
//        Connection c = null;
//        try {
//            c = ConnectionFactory.shared().makeConnection();
//            return DAOFactory.getStudentDAO(c).findAll();
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            if (c != null) c.close();
//        }
        return null;
    }

     
    public Map<String, Object> list(int currentPage, int lineSize, String column, String keyWord)
              {
//        Connection c = null;
//        try {
//            c = ConnectionFactory.shared().makeConnection();
//            StudentDAO user_dao = DAOFactory.getStudentDAO(c);
//
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("all_users", user_dao.findAllSplit(currentPage, lineSize, column,
//                    keyWord));
//            map.put("user_count", user_dao.getAllCount(column, keyWord));
//            return map;
//        } catch (SQLException e) {
//            throw e;
//        } catch (Exception e2) {
//            e2.printStackTrace(); return null;
//        } finally {
//            if (c != null) c.close();
//        }
        return null;
    }
}
