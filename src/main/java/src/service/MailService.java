package src.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import src.eric.Tools;

import java.util.Properties;

public class MailService {

    private static JavaMailSenderImpl mailSender;
    private static String username;
    private static SimpleMailMessage mailCache;

    static {
        Properties p = Tools.loadResource("mail.properties");
        String port = p.getProperty("port");
        String u_n = p.getProperty("username");
        expires_seconds = Integer.parseInt(p.getProperty("expires"));
        username = u_n;

        mailSender = new JavaMailSenderImpl();
        mailSender.setHost(p.getProperty("host"));
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(u_n);
        mailSender.setPassword(p.getProperty("password"));

        p = new Properties();
        p.put("mail.smtp.auth", true);
        p.put("mail.smtp.timeout", 4000);
        p.put("mail.smtp.port", port);
        p.put("mail.smtp.socketFactory.port", port);
        p.put("mail.smtp.socketFactory.fallback", "false");
        p.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        mailSender.setJavaMailProperties(p);
    }

    private static void sendMessage(String to, String msg) {
        if (mailCache == null) {
            mailCache = new SimpleMailMessage();
            mailCache.setFrom("SDU LAB <" + username + ">");
            mailCache.setSubject("山东大学软件学院实验室项目组");
        }
        SimpleMailMessage mail = mailCache;
        mail.setTo(to);
        mail.setText(msg);
        mailSender.send(mail);
        mail.setTo((String) null);
        mail.setText(null);
    }


    public static int expires_seconds;

    public static void sendCode(String to, String code) {
        sendMessage(to, "您好！您的验证码为: "+ code + "。30分钟内有效。");
    }

}
