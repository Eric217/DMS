package src.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import src.eric.Tools;

import java.util.Properties;

public class MailService {

    private static JavaMailSenderImpl mailSender;

    public static int expires_seconds;

    static {
        Properties p = Tools.loadResource("mail.properties");
        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(p.getProperty("host"));
        mailSender.setPort(Integer.parseInt(p.getProperty("port")));
        mailSender.setUsername(p.getProperty("username"));
        mailSender.setPassword(p.getProperty("password"));
        expires_seconds = Integer.parseInt(p.getProperty("expires"));
    }

    public static void sendMail(String to, String code) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("SDU LAB <2431388355@qq.com>");
        mail.setSubject("山东大学软件学院实验室项目组");
        mail.setTo(to);
        mail.setText("您好！您的验证码为: "+ code + "。30分钟内有效。");
        mailSender.send(mail);
    }


}
