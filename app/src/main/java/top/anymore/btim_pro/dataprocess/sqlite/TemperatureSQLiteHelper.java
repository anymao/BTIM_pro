package top.anymore.btim_pro.dataprocess.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by anymore on 17-4-1.
 */

public class TemperatureSQLiteHelper extends SQLiteOpenHelper{

    private static final String CREATE_TABLE_TEMP = "create table TEMPERATUREDATA(" +
            "id integer primary key autoincrement," +
            "room_id integer," +
            "time integer," +
            "real_temper real," +
            "warn_temper real," +
            "isdanger integer," +
            "ishandle integer)";
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
