package src.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.base.Result;
import src.base.ResultCache;
import src.model.Student;
import src.service.MailService;
import src.service.StudentService;

import javax.servlet.http.HttpSession;

import java.util.Date;

import static src.base.SessionNames.S_VERI_CODE;
import static src.base.SessionNames.S_VERI_LAST;
import static src.base.SessionNames.S_VERI_MAIL;
import static src.eric.Tools.createRandomNum;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/register/mail", method = RequestMethod.GET)
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

    @RequestMapping(value = "/register/code", method = RequestMethod.GET)
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
        student.setPassword(passwordEncoder.encode(password));
        student.setEmail(email);
        studentService.insertStudent(student);
        session.removeAttribute(S_VERI_CODE);
        session.removeAttribute(S_VERI_MAIL);
        return ResultCache.OK;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(String sid, String password, HttpSession session) {




        return null;

    }



}
