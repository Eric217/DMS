package src.eric;

public class Enums {

    private static String[] ProjectType =
            {"普通项目", "比赛项目", "学习项目", "创新项目", "其他项目"};



    public static boolean checkProjectType(String to_check) {
        if (to_check == null || to_check.isEmpty())
            return false;
        for (String i: ProjectType) {
            if (to_check.equals(i))
                return true;
        }
        return false;
    }

}
