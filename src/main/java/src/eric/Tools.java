package src.eric;

import com.mysql.cj.util.StringUtils;

import javax.servlet.http.Cookie;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

public class Tools {

    private static Random random = new Random();

    public static boolean isRightMailFormat(String email) { // TODO: - Regex
        if (email == null || email.isEmpty())
            return false;

        // 要求：只有一个 @ 符号
        return true;
    }

    public static boolean isRightPass(String pass) { // TODO: - Regex
        if (pass == null || pass.isEmpty())
            return false;

        if (pass.length() > 16 || pass.length() < 8)
            return false;


        return true;
    }

    public static boolean validateRealStudent(String sdu_mail, String name) { // TODO: - CHECK
        return true;
    }

    public static boolean isNullOrTrimEmp(String... strings) {
        for (String str: strings) {
            if (str == null || str.trim().isEmpty())
                return true;
        }
        return false;
    }

    /** @param exclude 需要删除的元素，nullable */
    public static HashSet<String> split(String src, String regex, String exclude) {
        HashSet<String> set = new HashSet<>();
        if (src == null || src.isEmpty())
            return set;
        String[] arr = src.split(regex);
        if (exclude == null)
            exclude = "";
        for (String element: arr) {
            if (!element.trim().isEmpty() && !element.equals(exclude))
                set.add(element);
        }
        return set;
    }

    public static <T> HashSet<T> toSet(T obj) {
        HashSet<T> set = new HashSet<>();
        if (obj != null)
            set.add(obj);
        return set;
    }

    public static HashSet<Long> split_to_long(String src, String regex, Long exclude) {
        HashSet<Long> set = new HashSet<>();
        if (src == null || src.isEmpty())
            return set;
        var arr = src.split(regex);
        for (String element: arr)
            if (!element.isEmpty()) {
                try {
                    Long l = Long.parseLong(element);
                    if (!l.equals(exclude))
                        set.add(l);
                } catch (Exception ignored) {}
            }
        return set;
    }

    public static String[] toArray(Set<String> set) {
        if (set == null)
            return new String[0];
        int s = set.size();
        String[] arr = new String[s];
        int c = 0;
        for (String ele: set) {
            arr[c] = ele;
            c++;
        }
        return arr;
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

    /** 产生随机颜色，其 rgb 每个值在 range_s ~ range_e 之间 */
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
