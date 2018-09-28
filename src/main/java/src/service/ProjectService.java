package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.ProjectDAO;
import src.model.Project;
import src.model.assistance.PageRowsMap;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import static src.model.assistance.ProjectStatusValue.*;

@Service
public class ProjectService {

    @Autowired
    ProjectDAO projectDAO;

    @Transactional
    public Result insert(Project vo, String[] sids) {
        try {
            // 确保sid没有其他活跃的项目
            Long pid = projectDAO.getActiveProjectIdByLeaderId(vo.getLeader_id());
            if (pid != null)
                return ResultCache.failWithMessage("同一时间只能管理一个进行中的项目");
            pid = projectDAO.getMaxProjectId();
            if (pid == null) pid = 0L;
            pid ++;
            vo.setId(pid);
            vo.setSubmit_time(new Timestamp(System.currentTimeMillis()));
            projectDAO.insertProject(vo);

            for (String sid : sids)
                projectDAO.addMember(sid, pid);
            projectDAO.addMember(vo.getLeader_id(), pid);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getProjectById(Long id) {
        try {
            Project p = projectDAO.getProjectById(id);
            if (p == null)
                return ResultCache.failWithMessage("项目不存在");
            return ResultCache.getDataOk(p);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getProjectsByStudentId(String sid, Boolean viewDelete) {
        try {
            List<Project> list = viewDelete
                    ? projectDAO.getProjectsAllByStudentId(sid)
                    : projectDAO.getProjectsDelByStudentId(sid);
            return ResultCache.getDataOk(list);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getAllProjectsByLabId(Integer page, Integer rows, Long lab_id) {
        try {
            return ResultCache.getDataOk(projectDAO.getAllSplitOfLabId(rows,
                    rows*(page-1), lab_id));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getProjectsWithStatusByLabId(Integer status, Long lab_id) {
        try {
            List<Project> list;
            if (status == S_REQUESTING)
                list = projectDAO.getRequestingOfLabId(lab_id);
            else if (status == S_PROCESSING)
                list = projectDAO.getProcessingOfLabId(lab_id);
            else if (status == S_CREATING)
                list = projectDAO.getCreatingOfLabId(lab_id);
            else
                list = null;
            return ResultCache.getDataOk(list);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getProjectsAdmin(Integer page, Integer rows, Integer status) {
        try {
            List<Project> list = null; int s = status;

            PageRowsMap map = new PageRowsMap(rows, rows * (page - 1));

            if (s == S_ALL) {
                list = projectDAO.getAllSplit(map);
            } else if (s == S_CREATING || s == S_REQUESTING) {
                list = projectDAO.getCreating(map);
            } else if (s == S_REJECTED) {
                list = projectDAO.getRejected(map);
            } else if (s == S_PROCESSING) {
                list = projectDAO.getProcessing(map);
            } else if (s == S_CANCELED) {
                list = projectDAO.getCanceled(map);
            } else if (s == S_COMPLETE) {
                list = projectDAO.getComplete(map);
            } else if (s == S_OVERTIME) {
                list = projectDAO.getOvertime(map);
            }

            return ResultCache.getDataOk(list);

        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }


//    public Result getCount(String prop, String like, Boolean viewDeleted) {
//        Integer count = viewDeleted
//                ? projectDAO.getCount(prop, like)
//                : projectDAO.getCount_Fake(prop, like);
//        return ResultCache.getDataOk(count);
//    }


    public Result updateProject(Project vo) {
        projectDAO.updateProject(vo);
        return ResultCache.OK;
    }

    @Transactional
    public Result updateDeleted(Set<Long> ids, Integer newValue) {
        if (newValue > 1) newValue = 1;
        if (newValue < 0) newValue = 0;
        for (Long id: ids) {
            projectDAO.updateDeleted(id, newValue);
        }
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
