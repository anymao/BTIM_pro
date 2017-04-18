package top.anymore.btim_pro.dataprocess.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;

/**温度数据数据库管理工具类
 * Created by anymore on 17-4-1.
 */

public class TemperatureDataProcessUtil {
    private static final String tag = "TemperatureDataProcessUtil";
    private SQLiteDatabase mSqLiteDatabase;
    private TemperatureSQLiteHelper mTemperatureSQLiteHelper;
    private Context mContext;

    public TemperatureDataProcessUtil(Context mContext,String databaseName) {
        this.mContext = mContext;
        mTemperatureSQLiteHelper = new TemperatureSQLiteHelper(mContext,databaseName,null,1);
        mSqLiteDatabase = mTemperatureSQLiteHelper.getWritableDatabase();
    }

    /**
     * 添加一条数据
     * @param entity
     */
    public void addData(TemperatureDataEntity entity){
        ContentValues values = new ContentValues();
        values.put("room_id",entity.getRoom_id());
        values.put("time",entity.getTime());
        values.put("real_temper",entity.getReal_temper());
        values.put("warn_temper",entity.getWarn_temper());
        values.put("is_danger",entity.getIs_dager());
        values.put("is_handle",entity.getIs_handle());
        mSqLiteDatabase.insert("TEMPERATUREDATA",null,values);
    }

    /**
     * 添加一组数据
     * @param entities
     */
    public void addData(List<TemperatureDataEntity> entities){
        LogUtil.v(tag,"xixixix");
        for (TemperatureDataEntity entity : entities) {
            addData(entity);
        }
    }

    /**
     * 获取一组数据，根据房间号，跳过的条目，获取条目的数量确定
     * @param room_id
     * @param skip
     * @param count
     * @return
     */
    public List<TemperatureDataEntity> getTemperatureDatas(int room_id,int skip,int count){
        Cursor cursor = mSqLiteDatabase.query("TEMPERATUREDATA",null,
                TemperatureSQLiteHelper.ROOM_ID+" = ?",
                new String[]{""+room_id},null,null,
                TemperatureSQLiteHelper.TIME+" DESC",skip + ","+count);
        List<TemperatureDataEntity> entities = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
//                int room_id = cursor.getInt(cursor.getColumnIndex("room_id"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                double real_temper = cursor.getDouble(cursor.getColumnIndex("real_temper"));
                double warn_temper = cursor.getDouble(cursor.getColumnIndex("warn_temper"));
                int is_danger = cursor.getInt(cursor.getColumnIndex("is_danger"));
                int is_handle = cursor.getInt(cursor.getColumnIndex("is_handle"));
                entities.add(new TemperatureDataEntity(room_id,time,real_temper,warn_temper,is_danger,is_handle));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return entities;
    }

    /**
     * 获取这个房间的所有数据条目
     * @param room_id
     * @return
     */
    public List<TemperatureDataEntity> getTemperatureDatas(int room_id){
        Cursor cursor = mSqLiteDatabase.query("TEMPERATUREDATA",null,
                TemperatureSQLiteHelper.ROOM_ID+" = ?",new String[]{""+room_id},
                null,null,"time DESC");
        List<TemperatureDataEntity> entities = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
//                int room_id = cursor.getInt(cursor.getColumnIndex("room_id"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                double real_temper = cursor.getDouble(cursor.getColumnIndex("real_temper"));
                double warn_temper = cursor.getDouble(cursor.getColumnIndex("warn_temper"));
                int is_danger = cursor.getInt(cursor.getColumnIndex("is_danger"));
                int is_handle = cursor.getInt(cursor.getColumnIndex("is_handle"));
                entities.add(new TemperatureDataEntity(room_id,time,real_temper,warn_temper,is_danger,is_handle));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return entities;
    }

    /**
     * 根据条件获取数据
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public List<TemperatureDataEntity> getDatas(String[] columns, String selection,
                                                String[] selectionArgs, String groupBy, String having,
                                                String orderBy, String limit){
        Cursor cursor = mSqLiteDatabase.query("TEMPERATUREDATA",columns,selection,selectionArgs,groupBy,having,orderBy,limit);
        List<TemperatureDataEntity> entities = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                int room_id = cursor.getInt(cursor.getColumnIndex("room_id"));
                long time = cursor.getLong(cursor.getColumnIndex("time"));
                double real_temper = cursor.getDouble(cursor.getColumnIndex("real_temper"));
                double warn_temper = cursor.getDouble(cursor.getColumnIndex("warn_temper"));
                int is_danger = cursor.getInt(cursor.getColumnIndex("is_danger"));
                int is_handle = cursor.getInt(cursor.getColumnIndex("is_handle"));
                entities.add(new TemperatureDataEntity(room_id,time,real_temper,warn_temper,is_danger,is_handle));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return entities;
    }
    public int updateData(ContentValues values, String whereClause, String[] whereArgs){
        return mSqLiteDatabase.update("TEMPERATUREDATA",values,whereClause,whereArgs);
    }
}
