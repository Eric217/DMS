package src.service;

import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.ProjectDAO;
import src.model.Project;
import src.model.assistance.PageRowsMap;
import src.model.assistance.StuProMap;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import static src.model.assistance.ProjectStatusValue.*;

@Service
public class ProjectService {

    @Autowired
    ProjectDAO projectDAO;

    public Result getCount(String prop, String like, Boolean viewDeleted) {
        Integer count = viewDeleted
                ? projectDAO.getCount(prop, like)
                : projectDAO.getCount_Fake(prop, like);
        return ResultCache.getDataOk(count);
    }

    ///
    @Transactional
    public Result getProjectById(Long id) {
        Project p = projectDAO.getProjectById(id);
        return ResultCache.getDataOk(p);
    }


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

    /** 对于普通学生  */
    public Result getParticipatedProjects(Integer page, Integer rows, Integer status,
                                          String sid) {
        List<Project> list = null;
        int s = status;

        StuProMap map = new StuProMap(rows, rows*(page-1), sid);

        if (s == S_ALL) {
            list = projectDAO.getAllSplitForStu(map);
        } else if (s == S_CHECKING) {
            list = projectDAO.getCheckingForStu(map);
        } else if (s == S_REJECTED) {
            list = projectDAO.getRejectedForStu(map);
        } else if (s == S_PROCESSING) {
            list = projectDAO.getProcessingForStu(map);
        } else if (s == S_CANCELED) {
            list = projectDAO.getCanceledForStu(map);
        } else if (s == S_COMPLETE) {
            list = projectDAO.getCompleteForStu(map);
        } else if (s == S_OVERTIME) {
            list = projectDAO.getOvertimeForStu(map);
        }
        return ResultCache.getDataOk(list);
    }

    /** 对于管理人员： */
    public Result getProjects(Integer page, Integer rows, Integer status,
                              Boolean viewDelete, String property, String like) {
        int s = status;
        List<Project> list = null;

        if (property == null || property.trim().isEmpty() || like == null || like.trim().isEmpty()) {
            property = ""; like = "";
        }

        PageRowsMap map = new PageRowsMap(rows, rows*(page-1), property, like);

        if (s == S_ALL) {
            list = viewDelete ? projectDAO.getAllSplit(map)
                    :projectDAO.getAllSplit_Fake(map);
        } else if (s == S_CHECKING) {
            list = viewDelete ? projectDAO.getChecking(map)
                    :projectDAO.getChecking_Fake(map);
        } else if (s == S_REJECTED) {
            list = viewDelete ? projectDAO.getRejected(map)
                    :projectDAO.getRejected_Fake(map);
        } else if (s == S_PROCESSING) {
            list = viewDelete ? projectDAO.getProcessing(map)
                    :projectDAO.getProcessing_Fake(map);
        } else if (s == S_CANCELED) {
            list = viewDelete ? projectDAO.getCanceled(map)
                    :projectDAO.getCanceled_Fake(map);
        } else if (s == S_COMPLETE) {
            list = viewDelete ? projectDAO.getComplete(map)
                    :projectDAO.getComplete_Fake(map);
        } else if (s == S_OVERTIME) {
            list = viewDelete ? projectDAO.getOvertime(map)
                    :projectDAO.getOvertime_Fake(map);
        }
        return ResultCache.getDataOk(list);
    }


    public Result updateProject(Project vo) {
        projectDAO.updateProject(vo);
        return ResultCache.OK;
    }

    public Result updateDeleted(Long id, Integer newValue) {
        if (newValue > 1) newValue = 1;
        if (newValue < 0) newValue = 0;
        projectDAO.updateDeleted(id, newValue);
        return ResultCache.OK;
    }

    @Transactional
    public Result deleteProjects(Set<Long> ids) {
        for (Long id: ids) {
            projectDAO.deleteProjectById(id);
        }
        return ResultCache.OK;
    }


}
