package top.anymore.btim_pro.dataprocess;

import android.content.Context;
import android.content.SharedPreferences;

import top.anymore.btim_pro.logutil.LogUtil;

/**
 * 警戒温度类
 * Created by anymore on 17-4-2.
 */

public class WarnTemperature {
    private static final String tag = "WarnTemperature";
    public static double[] warnTempers = new double[8];
    public static double defaultWarnTemper = 50;
    private static boolean isInited = false;

    /**
     * 初始化所有警戒温度，也就是从SharedPreferences读取数据
     * @param context
     */
    public static void initWarnTempers(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("WARN_TEMPER_SETTING_DATA", Context.MODE_PRIVATE);
        for (int i = 0; i < 8; i++) {
            String key = "room_id_"+i;
            warnTempers[i] = sharedPreferences.getFloat(key, (float) defaultWarnTemper);
        }
        isInited = true;
    }
    public static boolean getIsInited(){
        return isInited;
    }

    /**
     * 修改警戒温度
     * @param context
     * @param room_id
     * @param temper
     */
    public static void changeWarnTemper(Context context,int room_id,double temper){
        warnTempers[room_id] = temper;
        SharedPreferences.Editor editor = context.getSharedPreferences("WARN_TEMPER_SETTING_DATA", Context.MODE_PRIVATE).edit();
        editor.putFloat("room_id_"+room_id, (float) temper);
        editor.apply();
        LogUtil.v(tag,"警戒温度已经修改");
    }
    public static double getDefalutWarnTemperature(){
        return defaultWarnTemper;
    }
}
