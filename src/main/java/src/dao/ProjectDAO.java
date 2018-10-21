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

    /** For mapper to create collection type.
     *  @return Simplified Student Object. */
    List<Student> getMembersByProjectId(Long id); // TODO: - 测试删掉可不可以

    /** 为 modification 写的一个接口*/
    List<Student> getMembersByIds(String[] ids);

    String getLeaderIdByPid(Long id);

    /** 获取一个学生管理的活跃状态的项目 id（一个学生只允许管理一个活跃的项目） */
    Long getActiveProjectIdByLeaderId(String sid);

    /** 获取最大 pid */
//    Long getMaxProjectId();

    /** 插入一条 participation, sid 参与 pid */
    void addMember(@Param("sid") String sid, @Param("pid") Long pid);

    void removeMember(@Param("sid") String sid, @Param("pid") Long pid);

    void updateOptStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 这里更新的是一些普通属性，不包含开始、结束等状态标志，包含 opt-status */
    void updateProject(Project vo);

    void deleteProjectById(Long id);

    Integer updateDeleted(@Param("id") Long id, @Param("newValue") Integer newValue);

    Integer getCountOfLab(Long lid);

    /** 获取项目的详细信息 */
    Project getProjectById(Long id);

    /** 仅查看一个学生未被删除的项目 */
    List<Project> getProjectsDelByStudentId(String sid);

    /** 查看一个学生的所有项目 */
    List<Project> getProjectsAllByStudentId(String sid);

    // 管理员权限：获取某种类型的项目
    List<Project> getAllSplit(PageRowsMap map);
    List<Project> getCreating(PageRowsMap map);
    List<Project> getProcessing(PageRowsMap map);
    List<Project> getRejected(PageRowsMap map);
    List<Project> getCanceled(PageRowsMap map);
    List<Project> getComplete(PageRowsMap map);
    List<Project> getOvertime(PageRowsMap map);

    Integer getAllCount();
    Integer getCreatingCount();
    Integer getProcessingCount();
    Integer getRejectedCount();
    Integer getCanceledCount();
    Integer getCompleteCount();
    Integer getOvertimeCount();

    // 实验室负责人权限：获取 进行中的项目，待审核的新项目，待处理的请求，实验室所有项目
    List<Project> getProcessingOfLabId(Long lab_id);
    List<Project> getCreatingOfLabId  (Long lab_id);
    List<Project> getRequestingOfLabId(Long lab_id);
    List<Project> getAllSplitOfLabId(@Param("num") Integer num, @Param("offset") Integer ofs,
                                     @Param("lid") Long lab_id);





}
