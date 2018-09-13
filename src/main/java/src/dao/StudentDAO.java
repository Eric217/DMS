package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Student;

import java.util.List;
import java.util.Set;

@Repository
public interface StudentDAO {

    public Integer getCount(String property, String like);

    public Student getStudentById(String id);

    public List<Student> getAllStudents();

    public List<Student> getAllSplit(Integer page, Integer rows, String property, String like);

    public void insertStudent(Student vo);

    public void updateStudent(Student vo);

    public void deleteStudents(List<String> ids);

    public void participateProject(Long pid);


}
