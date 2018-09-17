package src.eric;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class Tools {

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
    public static Color getRandColor(int fc, int bc){
        Random random = new Random();
        if(fc>255)
            fc = 255;
        if(bc>255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r,g,b);
    }

}
