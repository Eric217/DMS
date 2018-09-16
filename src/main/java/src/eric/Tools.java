package src.eric;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Tools {

    public static String createRandomNum(int bits){
        String randomNumStr = "";
        for(int i = 0; i < bits;i ++){
            int randomNum = (int)(Math.random() * 10);
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

}
