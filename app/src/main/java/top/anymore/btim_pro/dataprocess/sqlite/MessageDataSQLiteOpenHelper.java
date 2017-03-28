package top.anymore.btim_pro.dataprocess.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-3-26.
 */

public class MessageDataSQLiteOpenHelper extends SQLiteOpenHelper{
    private static final String tag = "MessageDataSQLiteOpenHelper";
    public static final String CREATE_MESSAGE = "create table Message (" +
            "id integer primary key autoincrement" +
            ", msg_content text" +
            ", msg_time integer" +//时间用于存储毫秒数
            ", msg_type int2)";
    private Context mContext;
    public MessageDataSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MESSAGE);
        LogUtil.v(tag,"创建数据表成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
