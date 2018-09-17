package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.eric.Tools;
import src.model.Admin;
import src.model.Student;
import src.service.AdminService;
import src.service.MailService;
import src.service.StudentService;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Random;

import static src.base.SessionNames.*;
import static src.eric.Tools.createRandomNum;

/** 不需要登陆状态就可以做的请求 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    StudentService studentService;

    @Autowired
    AdminService adminService;

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public Result logout(HttpSession session) {
        session.invalidate();
        return ResultCache.OK;
    }

    @RequestMapping(value = "/register/mail", method = RequestMethod.POST)
    public Result getCode(String email, HttpSession session) {

        Date last_req = (Date)session.getAttribute(S_VERI_LAST), now = new Date();
        if (last_req != null && last_req.getTime() + 1000*60 >= now.getTime()) {
            return ResultCache.getFailureDetail("操作频繁，请稍后再试");
        }
        if (studentService.emailExist(email)) {
            return ResultCache.getFailureDetail("该邮箱已被注册");
        }
        String random = createRandomNum(6);
        MailService.sendMail(email, random);
        session.setAttribute(S_VERI_CODE, random);
        session.setAttribute(S_VERI_LAST, now);
        session.setAttribute(S_VERI_MAIL, email);
        return ResultCache.OK;
    }

    @RequestMapping(value = "/register/code", method = RequestMethod.POST)
    public Result verifyCode(String email, String code, String password,
                             String name, HttpSession session) {

        boolean b1 = email.equals(session.getAttribute(S_VERI_MAIL));
        Date last_req = (Date)session.getAttribute(S_VERI_LAST), now = new Date();
        boolean b2 = last_req != null &&
                        last_req.getTime()+1000*MailService.expires_seconds >= now.getTime();
        boolean b3 = code.equals(session.getAttribute(S_VERI_CODE));
        if (!(b1 && b2 && b3)) {
            return ResultCache.getFailureDetail("验证码已过期，请重新发送");
        }
        if (studentService.emailExist(email)) {
            return ResultCache.getFailureDetail("该邮箱已被注册");
        }
        Student student = new Student();
        String sid = email.split("@")[0];
        student.setId(sid);
        student.setName(name);
        student.setPassword(studentService.encodePassword(password));
        student.setEmail(email);
        studentService.insertStudent(student);
        session.removeAttribute(S_VERI_CODE);
        session.removeAttribute(S_VERI_MAIL);
        return ResultCache.OK;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(String sid, String password, String code, HttpSession session,
                        HttpServletResponse resp) {

        if (!code.equalsIgnoreCase((String)session.getAttribute(S_VERI_IMG))) {
            return ResultCache.getFailureDetail("验证码输入错误");
        }

        if (sid.startsWith("1000")) { // 管理员登陆
            if (adminService.passwordRight(Long.parseLong(sid), password)) {
                session.setAttribute(S_ROLE_ADMIN, true);
            } else
                return ResultCache.getFailureDetail("用户名或密码错误");

        } else { // 学号登陆
            if (studentService.passwordRight(sid, password)) {
                session.setAttribute(S_ROLE_ADMIN, false);
            } else
                return ResultCache.getFailureDetail("用户名或密码错误");
        }

        Cookie c = new Cookie("username", sid);
        resp.addCookie(c);
        session.removeAttribute(S_VERI_IMG);
        session.setAttribute(S_USERNAME, sid);
        return ResultCache.OK;
    }

    @RequestMapping("/login/code")
    public void getImageCode(HttpServletResponse response, HttpSession session) {
        int len = 5, single_w = 16;
        int width = len * (single_w + 2), height = 37;
        Random random = new Random();
        BufferedImage image = new BufferedImage(width, height, 1);

        Graphics2D g = image.createGraphics();
        g.setColor(Tools.getRandColor(200, 250));
        g.setFont(new Font("Times New Roman",Font.ITALIC,28));
        g.fillRect(0, 0, width, height);
        g.setStroke(new BasicStroke(3));

        for(int i = 0; i < 40; i++){ // 绘制干扰线
            g.setColor(Tools.getRandColor(130, 200));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x + x1, y + y1);
        }

        //绘制字符
        String strCode = Tools.createRandomNumWithLetters(len);
        for(int i = 0; i < len; i++){
            g.setColor(new Color(20+random.nextInt(110),
                    20+ random.nextInt(110),20+random.nextInt(110)));
            g.drawString(String.valueOf(strCode.charAt(i)), single_w*i+len, 28);
        }
        g.dispose();

        //设置response头信息
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        session.setAttribute(S_VERI_IMG, strCode);

        try {
            ImageIO.write(image, "JPEG", response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            System.out.println(new Date() + " write verify img failed");
        }
    }

    /** @param admin id 必须是以 1000 开头的 Long 类型
     *  需要添加管理员时，重新部署然后调用添加接口，release 下要注释掉 */
    @RequestMapping(value = "/create_admin", method = RequestMethod.GET)
    public Result create_admin(Admin admin) {
        return adminService.addAdmin(admin);
    }

}
