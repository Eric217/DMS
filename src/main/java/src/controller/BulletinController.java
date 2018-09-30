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

    @RequestMapping(value = "/one", method = RequestMethod.GET)
    public Result getOneById(Long id, HttpSession session) {
        if (!PermissionService.IS_LOGIN(session))
            return ResultCache.PERMISSION_DENIED;
        if (id == null || id < 0)
            return ResultCache.ARG_ERROR;
        return bulletinService.getBulletinById(id);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createBulletin(Bulletin vo, HttpSession session) {
        if (vo == null || Tools.isNullOrEmp(vo.getTitle(), vo.getContent()))
            return ResultCache.ARG_ERROR;
        if (PermissionService.IS_ADMIN(session))
            vo.setFrom(Constant.SYSTEM_BULLETIN);
        else {
            Laboratory lab = PermissionService.getManagedLab(session, labService);
            if (lab == null)
                return ResultCache.PERMISSION_DENIED;
            vo.setFrom(lab.getName());
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
        if (size > 1)
            if (!PermissionService.IS_ADMIN(session))
                return ResultCache.PERMISSION_DENIED;
        else {
                if (!PermissionService.IS_ADMIN(session)) { // size == 1
                    Laboratory lab = PermissionService.getManagedLab(session, labService);

                    bulletinService.getBulletinById(set.stream().findAny().orElseThrow(),
                            true).getData()


                    if (lab == null || !lab.getName().equals(bulletinService.getBulletinById(set.stream().findAny().orElseThrow()).get))

                }
            }


        return bulletinService.delete(set);

    }

    /** @param ids 以 @ 分隔 */
    @RequestMapping(value = "/delete/multi", method = RequestMethod.POST)
    public Result deleteBulletinById(String ids, HttpSession session) {
        if (!PermissionService.IS_ADMIN(session)) {
            Laboratory lab = PermissionService.getManagedLab(session, labService);
            if (lab == null || !lab.getName().equals())

        }


        HashSet<Long> set = Tools.split_to_long(ids, "@", null);
        return bulletinService.delete(set);

    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result updateBulletin(Bulletin vo) {
        return bulletinService.update(vo);
    }

}
