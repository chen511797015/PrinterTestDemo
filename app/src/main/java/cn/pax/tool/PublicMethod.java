package cn.pax.tool;

/**
 * Created by chendd on 2016/10/28.
 */

public class PublicMethod {


    public static String getSpaceByNum(int num) {
        String spaceStr = "";
        for (int i = 1; i < num; i++) {
            spaceStr += " ";
        }
        return spaceStr;
    }
}
