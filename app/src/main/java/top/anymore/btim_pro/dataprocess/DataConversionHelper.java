package top.anymore.btim_pro.dataprocess;

/**
 * Created by anymore on 17-3-30.
 */

public class DataConversionHelper {
    /**
     * 数据转换，其中s是一个字符串，以空格分割例如
     * “1 2 3 12 12 52 25.2”
     * 然后通过处理转换为数组
     * @param s
     * @return
     */
    public static double[] Strings2doubles(String s){
        String[] temp = s.split(" ");
        double[] result = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            result[i] = Integer.parseInt(temp[i]);
        }
        return result;
    }
}
