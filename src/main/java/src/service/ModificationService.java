package src.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.base.Result;
import src.base.ResultCache;
import src.dao.ModificationDAO;
import src.dao.ProjectDAO;
import src.eric.Tools;
import src.model.Modification;
import src.model.Notification;
import src.model.Project;
import src.model.Student;
import src.model.assistance.NotificationCache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ModificationService {

    // TODO: - 发通知

    @Autowired
    ModificationDAO modificationDAO;

    @Autowired
    ProjectDAO projectDAO;

    @Autowired
    NotificationService notificationService;

    public Result getProjectForModById(Long pid) {
        try {
            return ResultCache.getDataOk(modificationDAO.getProjectForModificationById(pid));
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    public Result getModificationByPid(Long pid) {
        try {
            Modification modification = modificationDAO.getModByProjectId(pid);
            HashSet<String> set = Tools.split(modification.getMembers(), "@",
                    modification.getLeader_id());
            modification.setMember_list(projectDAO.getMembersByIds(Tools.toArray(set)));
            modification.setMembers(null);
            return ResultCache.getDataOk(modification);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }


    @Transactional
    public Result insert(Modification mod, Project project) {
        try {
            modificationDAO.insertModification(mod);
            // 请求修改、请求取消。请求取消会改变最终状态：
            if (mod.getOpt_status() == 4) { // 4是已取消，是目标状态
                projectDAO.updateOptStatus(mod.getPid(), 5);
                notificationService._notifyLabLeaderOfLabName(project.getLab_name(),
                        NotificationCache.UPDATE_P(project.getLeader_id()));
            } else { // 请求修改，没有目标状态, 本身状态会变成 3，修改中
                projectDAO.updateOptStatus(mod.getPid(), 3);
                notificationService._notifyLabLeaderOfLabName(project.getLab_name(),
                        NotificationCache.CANCEL_P(project.getLeader_id()));
            }
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result execModification(Long pid) {
        try {

            Modification mod = modificationDAO.getModByProjectId(pid);

            modificationDAO.deleteModById(mod.getId());

            Project p = projectDAO.getProjectById(mod.getPid());
            p.setName(mod.getName());
            p.setDescription(mod.getDescription());
            p.setDuration(mod.getDuration());
            p.setCoach_id(mod.getCoach_id());
            p.setAim(mod.getAim());
            p.setType(mod.getType());
            p.setOpt_status(mod.getOpt_status() == 4 ? 4 : 0);
            projectDAO.updateProject(p);

            Set<String> updated_noti = Tools.toSet(p.getLeader_id());
            Set<String> added_noti = new HashSet<>();
            Set<String> remove_noti = new HashSet<>();

            List<Student> oldMems = p.getMembers();
            List<String> oldMemIds = new ArrayList<>();
            for (Student s: oldMems) {
                if (!s.getId().equals(p.getLeader_id()))
                    oldMemIds.add(s.getId());
            }
            HashSet<String> newMemIds = Tools.split(mod.getMembers(), "@",
                    mod.getLeader_id());
            for (String sid: newMemIds) {
                boolean removed = false;
                for (int i = 0; i < oldMemIds.size(); i++) {
                    if (oldMemIds.get(i).equals(sid)) { // old preserved mem
                        updated_noti.add(sid);
                        oldMemIds.remove(i);
                        removed = true; break;
                    }
                }
                if (!removed) { // new participation
                    projectDAO.addMember(sid, pid);
                    added_noti.add(sid);
                }
            }

            for (String oldId: oldMemIds) {
                projectDAO.removeMember(oldId, pid);
                remove_noti.add(oldId);
            }
            // 给 组长、old preserved mem 通知更新完成
            notificationService._notifyMembersByIds(updated_noti, NotificationCache.UPDATED);
            notificationService._notifyMembersByIds(added_noti,
                    NotificationCache.CREATE_P_MEM);
            notificationService._notifyMembersByIds(remove_noti, NotificationCache.QUIT_P);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result recallModification(Long mid, Project p) {
        try {
            modificationDAO.deleteModById(mid);
            projectDAO.updateOptStatus(p.getId(), 0);
            notificationService._notifyMembersByIds(Tools.toSet(p.getLeader_id()),
                    NotificationCache.UPDATE_FAIL);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

}
