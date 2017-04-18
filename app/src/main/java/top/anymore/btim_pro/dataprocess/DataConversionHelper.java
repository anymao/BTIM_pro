package top.anymore.btim_pro.dataprocess;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 辅助类，用于数据转换
 * Created by anymore on 17-3-30.
 */

public class DataConversionHelper {
    //时间格式转换类
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 数据转换，其中s是一个字符串，以空格分割例如
     * “1 2 3 12 12 52 25.2”
     * 然后通过处理转换为数组
     * @param s
     * @return
     */
    public static double[] Strings2doubles(String s){
        s = s.trim();
        String[] temp = s.split(" ");
        double[] result = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = Double.parseDouble(temp[i]);
        }
        return result;
    }

    /**
     * 将long类型的时间转换成yyyy-MM-dd hh:mm:ss类型字符串
     * @param time
     * @return
     */
    public static String long2Date(long time){
       return format.format(new Date(time));
    }
}
