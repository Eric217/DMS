package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Student;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentDAO {

//    Integer getCount(String property, String like);

    Student getStudentById(String id);

//    List<Student> getAllStudents();

//    List<Student> getAllSplit(Integer page, Integer rows, String property, String like);

    void insertStudent(Student vo);

    void updateStudent(Student vo);

    void updatePassword(@Param("email") String email, @Param("pass") String pass);

    void deleteStudent(String id);

    Integer checkMailExisted(String mail);

    String getEncodedPassword(String id);



}
