package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.eric.Constant;
import src.eric.Tools;
import src.model.Admin;
import src.model.Laboratory;
import src.model.Student;
import src.model.assistance.UserTypeModel;
import src.service.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Random;

import static src.base.SessionNames.*;

/** 不需要登陆状态就可以做的请求 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    StudentService studentService;

    @Autowired
    AdminService adminService;

    @Autowired
    LabService labService;

    /** 退出登陆 */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result logout(HttpSession session) {
        session.invalidate();
        return ResultCache.OK;
    }

    /** 发送注册验证码 */
    @RequestMapping(value = "/register/mail", method = RequestMethod.POST)
    public Result getCode(String email, HttpSession session) {
        try {
            if (!Tools.isRightMailFormat(email))
                return ResultCache.failWithMessage("邮箱格式不符合要求");

            Date last_req = (Date) session.getAttribute(S_VERI_LAST), now = new Date();
            long rest = now.getTime() - 60 * 1000;
            if (last_req != null && last_req.getTime() >= rest) {
                long last_t = last_req.getTime();
                rest = (long) ((last_t - rest) / 1000.0);
                return ResultCache.failWithMessage("操作频繁，请 " + rest + " 秒后再试");
            }
            if (studentService.emailExist(email))
                return ResultCache.failWithMessage("该邮箱已被注册");

            String random = Tools.createRandomNum(6);
            MailService.sendCode(email, random);
            session.setAttribute(S_VERI_CODE, random);
            session.setAttribute(S_VERI_LAST, now);
            session.setAttribute(S_VERI_MAIL, email);
            session.setAttribute(S_VERI_TYPE, Constant.REGISTER);
            session.setMaxInactiveInterval(Integer.parseInt(
                    Tools.loadResource("mail.properties").getProperty("expires")));
            return ResultCache.OK;
        } catch (MailException e) {
            return ResultCache.failWithMessage("邮件系统异常");
        } catch (Exception e1) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    /** 提交注册 */
    @RequestMapping(value = "/register/code", method = RequestMethod.POST)
    public Result verifyCode(String email, String code, String password,
                             String name, HttpSession session) {
        try {
            if (Tools.isNullOrTrimEmp(email, code, name) || !Tools.isRightPass(password))
                return ResultCache.ARG_ERROR;
            name = name.trim();
            boolean b1 = email.equals(session.getAttribute(S_VERI_MAIL));
            Date last_req = (Date) session.getAttribute(S_VERI_LAST), now = new Date();
            boolean b2 = last_req != null &&
                    last_req.getTime() + 1000 * MailService.expires_seconds >= now.getTime();
            boolean b3 = code.equals(session.getAttribute(S_VERI_CODE));
            boolean b4 = Constant.REGISTER.equals(session.getAttribute(S_VERI_TYPE));
            if (!(b1 && b2 && b4))
                return ResultCache.failWithMessage("验证码已过期，请重新发送");
            if (!b3)
                return ResultCache.failWithMessage("验证码输入错误");
            if (studentService.emailExist(email))
                return ResultCache.failWithMessage("该邮箱已被注册");
            b3 = Tools.validateRealStudent(email, name);
            if (!b3)
                return ResultCache.failWithMessage("邮箱与姓名不匹配");
            Student student = new Student();
            String sid = email.split("@")[0];
            student.setId(sid);
            student.setName(name);
            student.setPassword(studentService.encodePassword(password));
            student.setEmail(email);
            session.removeAttribute(S_VERI_CODE);
            session.removeAttribute(S_VERI_MAIL);
            session.removeAttribute(S_VERI_TYPE);
            return studentService.insertStudent(student);
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    /** 发送重置密码验证码 */
    @RequestMapping(value = "/resetPassword/mail", method = RequestMethod.POST)
    public Result getCode_2(String email, HttpSession session) {
        try {
            if (!Tools.isRightMailFormat(email))
                return ResultCache.failWithMessage("邮箱格式不符合要求");

            Date last_req = (Date) session.getAttribute(S_VERI_LAST), now = new Date();
            long rest = now.getTime() - 60 * 1000;
            if (last_req != null && last_req.getTime() >= rest) {
                long last_t = last_req.getTime();
                rest = (long) ((last_t - rest) / 1000.0);
                return ResultCache.failWithMessage("操作频繁，请 " + rest + " 秒后再试");
            }
            if (!studentService.emailExist(email))
                return ResultCache.failWithMessage("该邮箱尚未注册");

            String random = Tools.createRandomNum(6);
            MailService.sendCode(email, random);
            session.setAttribute(S_VERI_CODE, random);
            session.setAttribute(S_VERI_LAST, now);
            session.setAttribute(S_VERI_MAIL, email);
            session.setAttribute(S_VERI_TYPE, Constant.RESET_PASSWORD);
            session.setMaxInactiveInterval(Integer.parseInt(
                    Tools.loadResource("mail.properties").getProperty("expires")));
            return ResultCache.OK;
        } catch (MailException e) {
            return ResultCache.failWithMessage("邮件系统异常");
        } catch (Exception e1) {
            return ResultCache.DATABASE_ERROR;
        }
    }

    /** 提交重置密码 */
    @RequestMapping(value = "/resetPassword/code", method = RequestMethod.POST)
    public Result verifyCode_2(String email, String code, String password, HttpSession session) {
        if (Tools.isNullOrTrimEmp(email, code))
            return ResultCache.failWithMessage("不能为空");
        if (!Tools.isRightPass(password))
            return ResultCache.failWithMessage("密码格式错误");
        boolean b1 = email.equals(session.getAttribute(S_VERI_MAIL));
        Date last_req = (Date)session.getAttribute(S_VERI_LAST), now = new Date();
        boolean b2 = last_req != null &&
                last_req.getTime()+1000*MailService.expires_seconds >= now.getTime();
        boolean b3 = code.equals(session.getAttribute(S_VERI_CODE));
        boolean b4 = Constant.RESET_PASSWORD.equals(session.getAttribute(S_VERI_TYPE));
        if (!(b1 && b2 && b4))
            return ResultCache.failWithMessage("验证码已过期，请重新发送");
        if (!b3)
            return ResultCache.failWithMessage("验证码输入错误");
        session.removeAttribute(S_VERI_CODE);
        session.removeAttribute(S_VERI_MAIL);
        session.removeAttribute(S_VERI_TYPE);
        return studentService.updatePassword(email, password);
    }

    /** 登陆 */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(String sid, String password, String code,
                        Boolean remember, HttpSession session, HttpServletResponse resp) {

        if (code == null || !code.equalsIgnoreCase((String)session.getAttribute(S_VERI_IMG)))
            return ResultCache.failWithMessage("验证码输入错误");
        if (sid == null)
            return ResultCache.failWithMessage("用户名或密码错误");
        try {
            if (sid.startsWith("1000")) { // 管理员登陆
                if (adminService.passwordRight(Long.parseLong(sid), password)) {
                    PermissionService.GRANT_ADMIN(session);
                } else
                    return ResultCache.failWithMessage("用户名或密码错误");
            } else { // 学号登陆
                if (!studentService.passwordRight(sid, password)) {
                    return ResultCache.failWithMessage("用户名或密码错误");
                }
            }
        } catch (Exception e) {
            return ResultCache.DATABASE_ERROR;
        }
        // 账号密码没问题，存 session
        PermissionService.GRANT_USER(session, sid);
        session.removeAttribute(S_VERI_IMG);

        Cookie c = Tools.makeCookie(S_USERNAME, sid);
        resp.addCookie(c);
        // TODO：- 加密 cookie，对称加密或者走 https. 配合前端加载时自动填充上 Cookie 的账号密码
        // 这块我其实不会做。。。
        Cookie c1 = Tools.makeCookie(S_PASSWORD, remember ? password : "");
        resp.addCookie(c1);

        session.setMaxInactiveInterval(40 * 60);
        return userType(session);
    }

    /** role: 1 normal, 2 lab , 3 admin, 0 no user,
     *  data: */
    @RequestMapping(value = "/type", method = RequestMethod.GET)
    public Result userType(HttpSession session) {
        int role = 0;
        UserTypeModel model = new UserTypeModel();
        String uid = (String)session.getAttribute(S_USERNAME);
        if (PermissionService.IS_LOGIN(session)) {
            if (PermissionService.IS_ADMIN(session)) {
                Admin admin = new Admin();
                admin.setId(Long.parseLong(uid));
                model.setAdmin(admin);
                role = 3;
            } else {
                Laboratory l = PermissionService.getManagedLab(session, labService);
                model.setLab(l);
                role = l == null ? 1 : 2;
                Student s = (Student)(studentService.getStudentById(uid, false).getData());
                model.setStudent(s);
            }
        }
        model.setRole(role);
        return ResultCache.getDataOk(model);
    }

    /** 验证码图片绘制 */
    @RequestMapping("/login/code")
    public void getImageCode(HttpServletResponse response, HttpSession session) {
        int len = 5, single_w = 16;
        int width = len * (single_w + 2), height = 34;
        Random random = new Random();
        BufferedImage image = new BufferedImage(width, height, 1);

        Graphics2D g = image.createGraphics();
        g.setColor(Tools.getRandColor(170, 250));
        g.fillRect(0, 0, width, height);
        g.setStroke(new BasicStroke(3f));

        for(int i = 0; i < 40; i++){ // 绘制干扰线
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.setColor(Tools.getRandColor(110, 190));
            g.drawLine(x, y, x + x1, y + y1);
        }

        //绘制字符
        String strCode = Tools.createRandomNumWithLetters(len);
        Font[] fs = Tools.getRandFonts(21, 16, len);
        for(int i = 0; i < len; i++){
            g.setFont(fs[i]);
            g.setColor(Tools.getRandColor(0, 140));
            g.drawString(String.valueOf(strCode.charAt(i)), single_w*i+len, 28);
        }
        g.dispose();

        session.setAttribute(S_VERI_IMG, strCode);

        try {
            ImageIO.write(image, "JPEG", response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            System.err.println(new Date() + " write verify img failed");
        }
    }

    /** @param admin id 必须是以 1000 开头的 Long 类型
     *  需要添加管理员时，重新部署然后调用添加接口，release 下要注释掉 */
    @RequestMapping(value = "/create_admin", method = RequestMethod.GET)
    public Result create_admin(Admin admin) {
        return adminService.addAdmin(admin);
    }

}
