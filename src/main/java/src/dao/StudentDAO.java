package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Student;
import src.model.assistance.PageRowsMap;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentDAO {

    void insertStudent(Student vo);

    void updateStudent(Student vo);

    void deleteStudent(String id);


    Integer getCount();

    Student getStudentById(String id);

    /** 这个接口只返回 id name。需要更多信息直接改 mapper
     *  我是真的不想用动态 SQL 。。。  */
    List<Student> getMinStudentByIds(String[] ids);

    List<Student> getAllSplit(PageRowsMap map);


    void updatePassword(@Param("email") String email, @Param("pass") String pass);

    Integer checkMailExisted(String mail);

    String getEncodedPassword(String id);

}
