package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.ProjectDAO;
import src.eric.Tools;
import src.model.Project;
import src.model.Student;
import src.model.assistance.NotificationCache;
import src.model.assistance.PageRowsMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static src.model.assistance.ProjectStatusValue.*;

@Service
public class ProjectService {

    @Autowired
    ProjectDAO projectDAO;

    /** 通知服务应该耦合在这里吗？？？ 还是在控制器层写。。。 */
    @Autowired
    NotificationService notificationService;

    public String getLeaderIdByPid(Long pid) {
        try {
            return projectDAO.getLeaderIdByPid(pid);
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public Result insert(Project vo, Set<String> sids) {
        try {
            // 确保sid没有其他活跃的项目
            Long pid = projectDAO.getActiveProjectIdByLeaderId(vo.getLeader_id());
            if (pid != null)
                return ResultCache.failWithMessage("同一时间只能管理一个进行中的项目");
            // TODO: - 检查 sid、lid 是否在 punish 期内
            projectDAO.insertProject(vo); // 如果 lab_name 不合法这里会捕获异常
            pid = vo.getId();
            for (String sid : sids)
                projectDAO.addMember(sid, pid);
            projectDAO.addMember(vo.getLeader_id(), pid);

            // 通知成员已加入项目
            notificationService._notifyMembersByIds(sids, NotificationCache.CREATE_P_MEM);
            // 通知实验室负责人审核
            notificationService._notifyLabLeaderOfLabName(vo.getLab_name(),
                    NotificationCache.CREATE_P_LAB(vo.getLeader_id()));
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
            } else if (s == S_CREATING) {
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

    public Result getCountOfLab(Long lab_id) {
        try {
            return ResultCache.getDataOk(projectDAO.getCountOfLab(lab_id));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getAllCountOfStatus(Integer status) {
        try {
            int s = status, r = 0;
            if (s == S_ALL) {
                r = projectDAO.getAllCount();
            } else if (s == S_CREATING) {
                r = projectDAO.getCreatingCount();
            } else if (s == S_REJECTED) {
                r = projectDAO.getRejectedCount();
            } else if (s == S_PROCESSING) {
                r = projectDAO.getProcessingCount();
            } else if (s == S_CANCELED) {
                r = projectDAO.getCanceledCount();
            } else if (s == S_COMPLETE) {
                r = projectDAO.getCompleteCount();
            } else if (s == S_OVERTIME) {
                r = projectDAO.getOvertimeCount();
            }
            return ResultCache.getDataOk(r);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result updateProject(Project p, Set<String> newMemIds, Boolean notify) {
        try {
            projectDAO.updateProject(p);

            List<Student> oldMems = projectDAO.getMembersByProjectId(p.getId());
            List<String> oldMemIds = new ArrayList<>();
            for (Student s: oldMems) {
                if (!s.getId().equals(p.getLeader_id()))
                    oldMemIds.add(s.getId());
            }

            Set<String> updated_noti = notify ? Tools.toSet(p.getLeader_id()) : null;
            Set<String> added_noti = notify ? new HashSet<>() : null;
            Set<String> remove_noti = notify ? new HashSet<>() : null;

            for (String sid: newMemIds) {
                boolean removed = false;
                for (int i = 0; i < oldMemIds.size(); i++) {
                    if (oldMemIds.get(i).equals(sid)) { // old preserved mem
                        if (notify)
                           updated_noti.add(sid);
                        oldMemIds.remove(i);
                        removed = true; break;
                    }
                }
                if (!removed) { // new participation
                    projectDAO.addMember(sid, p.getId());
                    if (notify)
                        added_noti.add(sid);
                }
            }

            for (String oldId: oldMemIds) {
                projectDAO.removeMember(oldId, p.getId());
                if (notify)
                   remove_noti.add(oldId);
            }
            if (notify) {
                notificationService._notifyMembersByIds(updated_noti, NotificationCache.UPDATED);
                notificationService._notifyMembersByIds(added_noti,
                        NotificationCache.CREATE_P_MEM);
                notificationService._notifyMembersByIds(remove_noti, NotificationCache.QUIT_P);
            }
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result updateDeleted(Set<Long> ids, Integer newValue) {
        if (newValue > 1) newValue = 1;
        if (newValue < 0) newValue = 0;
        try {
            for (Long id: ids) {
                int u = projectDAO.updateDeleted(id, newValue);
                if (u == 1) {
                    // TODO: - 通知所有成员已删除。还要考虑真删时候的通知
                }
            }
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result deleteProjects(Set<Long> ids) {
        try {
            for (Long id: ids) {
                projectDAO.deleteProjectById(id);
            }
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

}
