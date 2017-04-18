package top.anymore.btim_pro.dataprocess.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 温度数据数据库Openhelper
 * Created by anymore on 17-4-1.
 */

public class TemperatureSQLiteHelper extends SQLiteOpenHelper{
    public static final String ROOM_ID = "room_id";
    public static final String TIME = "time";
    public static final String REAL_TEMPER = "real_temper";
    public static final String WARN_TEMPER = "warn_temper";
    public static final String IS_DANGER = "is_danger";
    public static final String IS_HANDLE = "is_handle";
    private static final String CREATE_TABLE_TEMP = "create table TEMPERATUREDATA(" +
            "id integer primary key autoincrement," +
            "room_id integer," +
            "time integer," +
            "real_temper real," +
            "warn_temper real," +
            "is_danger integer," +
            "is_handle integer)";
    private static final String tag = "TemperatureSQLiteHelper";
    public TemperatureSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TEMP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
