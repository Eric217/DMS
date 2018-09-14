package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import src.model.Project;
import src.model.Student;
import src.model.assistance.PageRowsMap;
import src.model.assistance.StuProMap;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectDAO {

    Project getProjectById(Long id);

    Long getActiveProjectIdByLeaderId(String sid);

    void insertProject(Project vo);

    void updateProject(Project vo);

    void updateDeleted(@Param("id") Long id, @Param("newValue") Integer newValue);

    void deleteProjectById(Long id);

    void addMember(@Param("sid") String sid, @Param("pid") Long pid);


    /** Simplified Student Object */
    List<Student> getMembersByProjectId(Long id);

    Integer getCount(@Param("property") String property, @Param("like") String like);

    List<Project> getAllSplit (PageRowsMap map);
    List<Project> getChecking(PageRowsMap map);
    List<Project> getProcessing(PageRowsMap map);
    List<Project> getRejected(PageRowsMap map);
    List<Project> getCanceled(PageRowsMap map);
    List<Project> getComplete(PageRowsMap map);
    List<Project> getOvertime(PageRowsMap map);

    // 凡是带 fake 后缀的，返回的 project deleted 属性为 0；上面的 皆为获取所有 project
    Integer getCount_Fake(@Param("property") String property, @Param("like") String like);

    List<Project> getAllSplit_Fake(PageRowsMap map);
    List<Project> getProcessing_Fake(PageRowsMap map);
    List<Project> getChecking_Fake(PageRowsMap map);
    List<Project> getRejected_Fake(PageRowsMap map);
    List<Project> getCanceled_Fake(PageRowsMap map);
    List<Project> getComplete_Fake(PageRowsMap map);
    List<Project> getOvertime_Fake(PageRowsMap map);

    List<Project> getAllSplitForStu(StuProMap map);
    List<Project> getProcessingForStu(StuProMap map);
    List<Project> getCheckingForStu(StuProMap map);
    List<Project> getRejectedForStu(StuProMap map);
    List<Project> getCanceledForStu(StuProMap map);
    List<Project> getCompleteForStu(StuProMap map);
    List<Project> getOvertimeForStu(StuProMap map);

}
