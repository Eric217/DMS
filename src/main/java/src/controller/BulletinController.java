package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.eric.Constant;
import src.eric.Tools;
import src.model.Bulletin;
import src.model.Laboratory;
import src.service.BulletinService;
import src.service.LabService;
import src.service.PermissionService;

import javax.servlet.http.HttpSession;
import java.util.HashSet;

@RestController
@RequestMapping (value = "/bulletin")
public class BulletinController {

    @Autowired
    BulletinService bulletinService;

    @Autowired
    LabService labService;

    @RequestMapping(value = "/one", method = RequestMethod.GET)
    public Result getOneById(Long id, HttpSession session) {
        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        if (id == null || id < 0)
            return ResultCache.ARG_ERROR;

        return bulletinService.getBulletinById(id);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Result getAllCount(HttpSession session) {
        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        return bulletinService.getAllCount();
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public Result getAllBulletins(Integer page, Integer rows, HttpSession session) {
        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        if (page == null)
            page = 1;
        if (rows == null)
            rows = 10;
        return bulletinService.getAllBulletins(page, rows);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createBulletin(Bulletin vo, HttpSession session) {
        if (vo == null || Tools.isNullOrTrimEmp(vo.getTitle(), vo.getContent()))
            return ResultCache.ARG_ERROR;
        vo.setTitle(vo.getTitle().trim());
        if (PermissionService.IS_ADMIN(session))
            vo.setFrom(Constant.SYSTEM_BULLETIN);
        else {
            Laboratory lab = PermissionService.getManagedLab(session, labService);
            if (lab == null)
                return ResultCache.PERMISSION_DENIED;
            vo.setFrom(lab.getName() + "实验室");
        }
        return bulletinService.insertBulletin(vo);
    }

    /** 多选删除只支持 admin，删除一个支持其实验室负责人、admin
     *  @param ids 以 @ 分隔 */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Result deleteBulletinByIds(String ids, HttpSession session) {

        HashSet<Long> set = Tools.split_to_long(ids, "@", null);
        var size = set.size();
        if (size == 0)
            return ResultCache.ARG_ERROR;
        boolean admin = PermissionService.IS_ADMIN(session);
        if (admin)
            return bulletinService.delete(set);
        if (size > 1)
            return ResultCache.PERMISSION_DENIED;
        // size == 1
        Laboratory lab = PermissionService.getManagedLab(session, labService);
        if (lab == null)
            return ResultCache.PERMISSION_DENIED;
        Bulletin b = (Bulletin) bulletinService.getMinBulletinById(
                set.stream().findAny().orElseThrow()).getData();
        if (b == null)
            return ResultCache.failWithMessage("id 对应的公告不存在，或数据库读写失败");
        if (!lab.getName().equals(b.getFrom()))
            return ResultCache.PERMISSION_DENIED;
        return bulletinService.delete(set);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateBulletin(Bulletin vo, HttpSession session) {

        if (vo == null || vo.getId() == null || Tools.isNullOrTrimEmp(vo.getTitle(), vo.getContent()))
            return ResultCache.ARG_ERROR;
        if (!PermissionService.IS_ADMIN(session)) {
            Laboratory lab = PermissionService.getManagedLab(session, labService);
            if (lab == null || !lab.getName().equals(vo.getFrom()))
                return ResultCache.PERMISSION_DENIED;
        }
        return bulletinService.update(vo);
    }

}
