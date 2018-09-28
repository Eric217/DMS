package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Project;
import src.model.Student;
import src.model.assistance.PageRowsMap;

import java.util.List;

@Repository
public interface ProjectDAO {

    void insertProject(Project vo);

    void updateProject(Project vo);

    void deleteProjectById(Long id);

    void updateDeleted(@Param("id") Long id, @Param("newValue") Integer newValue);


//    // 凡是带 fake 后缀的，返回的 project deleted 属性为 0；上面的 皆为获取所有 project
//    Integer getCount_Fake(@Param("property") String property, @Param("like") String like);
//
//    Integer getCount(@Param("property") String property, @Param("like") String like);


    // 管理员权限：获取某种类型的项目
    List<Project> getAllSplit(PageRowsMap map);
    List<Project> getCreating(PageRowsMap map);
    List<Project> getProcessing(PageRowsMap map);
    List<Project> getRejected(PageRowsMap map);
    List<Project> getCanceled(PageRowsMap map);
    List<Project> getComplete(PageRowsMap map);
    List<Project> getOvertime(PageRowsMap map);

    // 实验室负责人权限：获取 进行中的项目，待审核的新项目，待处理的请求，实验室所有项目
    List<Project> getProcessingOfLabId(Long lab_id);
    List<Project> getCreatingOfLabId  (Long lab_id);
    List<Project> getRequestingOfLabId(Long lab_id);
    List<Project> getAllSplitOfLabId(@Param("num") Integer num, @Param("offset") Integer ofs,
                                     @Param("lid") Long lab_id);

    /** 获取项目的详细信息 */
    Project getProjectById(Long id);

    /** For mapper to create collection type.
     *  @return Simplified Student Object. */
    @SuppressWarnings("unused")
    List<Student> getMembersByProjectId(Long id);

    /** 获取一个学生管理的活跃状态的项目 id（一个学生只允许管理一个活跃的项目） */
    Long getActiveProjectIdByLeaderId(String sid);

    /** 获取最大 pid */
    Long getMaxProjectId();

    /** 插入一条 participation, sid 参与 pid */
    void addMember(@Param("sid") String sid, @Param("pid") Long pid);

    /** 仅查看一个学生未被删除的项目 */
    List<Project> getProjectsDelByStudentId(String sid);
    /** 查看一个学生的所有项目 */
    List<Project> getProjectsAllByStudentId(String sid);


}
