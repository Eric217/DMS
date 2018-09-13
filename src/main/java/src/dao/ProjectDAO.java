package src.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import src.model.Project;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
public interface ProjectDAO {

    public Integer getCount(String property, String like);

    public Project getProjectById(Long id);

    public Long getActiveProjectIdByLeaderId(String sid);

    public List<Project> getAllProjects();

    public List<Project> getAllSplit(Integer page, Integer rows, String property, String like);

    public void insertProject(Project vo);

    public void updateProject(Project vo);

    public void deleteProjects(List<Long> ids);

    public void addMember(@Param("sid") String sid, @Param("pid") Long pid);

}
