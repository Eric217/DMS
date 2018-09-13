package src.service;

import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.ProjectDAO;
import src.model.Project;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ProjectService {


    @Autowired
    ProjectDAO projectDAO;



    @Transactional
    public Result insert(Project vo, String... ids) {

        vo.setSubmit_time(new Timestamp(System.currentTimeMillis()));
        projectDAO.insertProject(vo);
        Long p_id = projectDAO.getActiveProjectIdByLeaderId(vo.getLeader_id());

        System.out.println("ids: " + ids.length);
        for (String id: ids) {
            if (!StringUtils.isNullOrEmpty(id) && !id.equals(vo.getLeader_id()))
                projectDAO.addMember(id, p_id);
        }
        projectDAO.addMember(vo.getLeader_id(), p_id);
        return ResultCache.OK;
    }

     
    public boolean update(Project vo) {
        return false;
    }

     
    public boolean delete(Set<Long> ids) {
        return false;
    }

     
    public Project get(Long id) {
        return null;
    }

     
    public List<Project> list() {
        return null;
    }

     
    public Map<String, Object> list(int currentPage, int lineSize, String column, String keyWord) {
        return null;
    }



}
