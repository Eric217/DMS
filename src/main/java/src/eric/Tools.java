package src.eric;

import javax.servlet.http.Cookie;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.Properties;
import java.util.Random;

public class Tools {

    private static Random random = new Random();

    public static boolean isRightMail(String email) {
        return true;
    }

    public static boolean isRightPass(String pass) {
        if (pass == null)
            return false;
        if (pass.length() > 16 || pass.length() < 8)
            return false;
        return true;
    }

    public static boolean isNullOrEmp(String... strings) {
        for (String str: strings) {
            if (str == null || str.isEmpty())
                return true;
        }
        return false;
    }

    public static String createRandomNum(int bits){
        String randomNumStr = "";
        for(int i = 0; i < bits; i++){
            int randomNum = (int)(Math.random() * 10);
            randomNumStr += randomNum;
        }
        return randomNumStr;
    }

    public static String createRandomNumWithLetters(int bits){
        String randomNumStr = "";
        int c1 = 'a', c2 = 'A';
        for(int i = 0; i < bits; i++){
            int randomNum = (int)(Math.random() * 62);
            if (randomNum > 9 && randomNum < 36) {
                char ch = (char)(randomNum - 10 + c1);
                randomNumStr += ch;
            } else if (randomNum > 35) {
                char ch = (char)(randomNum - 36 + c2);
                randomNumStr += ch;
            } else
                randomNumStr += randomNum;
        }
        return randomNumStr;
    }

    public static Properties loadResource(String name) {
        Properties p = new Properties();
        try {
            InputStream in = Tools.class.getClassLoader().getResourceAsStream(name);
            p.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    //创建颜色
    public static Color getRandColor(int range_s, int range_e){
        if(range_s>255)
            range_s = 255;
        if(range_e>255)
            range_e = 255;
        if (range_e < range_s) {
            int t = range_e;
            range_e = range_s; range_s = t;
        }

        int r = range_s + random.nextInt(range_e - range_s);
        int g = range_s + random.nextInt(range_e - range_s);
        int b = range_s + random.nextInt(range_e - range_s);
        return new Color(r,g,b);
    }

    public static Font getRandFont(int min_size, int total) {
        return new Font("Times New Roman", random.nextInt(3), min_size + random.nextInt(total));
    }

    public static Font[] getRandFonts(int min_size, int total, int count) {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = new Font[count];
        Font[] all = e.getAllFonts();
        int c = all.length;
        for (int i = 0; i < count; i++) {
            fonts[i] = all[random.nextInt(c)].deriveFont(random.nextInt(3),
                    min_size + random.nextInt(total));
        }
        return fonts;
    }

    public static Cookie makeCookie(String name, String value) {
        Cookie c = new Cookie(name, value);
//        HttpCookie
        c.setMaxAge(2 * 24 * 60 * 60);
//        c.setDomain("如.baidu.com");
        c.setPath("/");
        return c;
    }

}
