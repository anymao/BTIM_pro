package top.anymore.btim_pro.dataprocess;

import android.content.Context;
import android.content.SharedPreferences;

import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-4-2.
 */

public class WarnTemperature {
    private static final String tag = "WarnTemperature";
    public static double[] warnTempers = new double[8];
    public static double defaultWarnTemper = 50;
    private static boolean isInited = false;
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
