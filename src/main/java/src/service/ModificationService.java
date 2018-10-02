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
import src.model.Project;
import src.model.Student;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class ModificationService {

    // TODO: - 发通知

    @Autowired
    ModificationDAO modificationDAO;

    @Autowired
    ProjectDAO projectDAO;

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
    public Result insert(Modification mod) {
        try {
            modificationDAO.insertModification(mod);
            // 请求修改、请求取消。请求取消会改变最终状态：
            if (mod.getOpt_status() == 4) { // 4是已取消，是目标状态
                projectDAO.updateOptStatus(mod.getPid(), 5);
            } else { // 请求修改，没有目标状态, 本身状态会变成 3，修改中
                projectDAO.updateOptStatus(mod.getPid(), 3);
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
                    if (oldMemIds.get(i).equals(sid)) {
                        oldMemIds.remove(i);
                        removed = true; break;
                    }
                }
                if (!removed) // new participation
                    projectDAO.addMember(sid, pid);
            }

            for (String oldId: oldMemIds)
                projectDAO.removeMember(oldId, pid);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    @Transactional
    public Result recallModification(Long mid, Long pid) {
        try {
            modificationDAO.deleteModById(mid);
            projectDAO.updateOptStatus(pid, 0);
            return ResultCache.OK;
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

}
