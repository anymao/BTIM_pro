package top.anymore.btim_pro.dataprocess.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import top.anymore.btim_pro.entity.Message;

/**数据库操作工具类
 * Created by anymore on 17-3-26.
 */

public class DataProcessUtil {
    private static final String tag = "DataProcessUtil";
    private MessageDataSQLiteOpenHelper mSQLiteOpenHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public DataProcessUtil(Context mContext,String databaseName) {
        this.mContext = mContext;
        mSQLiteOpenHelper = new MessageDataSQLiteOpenHelper(mContext,databaseName,null,1);
        mDatabase = mSQLiteOpenHelper.getWritableDatabase();
    }
    //添加数据条目
    public void addData(Message message){
        ContentValues values = new ContentValues();
        values.put("msg_content",message.getContent());
        values.put("msg_time",message.getDate().getTime());
        values.put("msg_type",message.getType());
        mDatabase.insert("Message",null,values);
        values.clear();
    }
    //添加多个条目
    public void addData(List<Message> messages){
        for (Message message:messages) {
            addData(message);
        }
    }
    //删除数据条目
    public void removeData(){
        //TODO未完成的功能
    }
    //修改数据条目
    public void updateData(){
        //未实现
    }
    //查询数据
    public List<Message> getAllMessage(){
        List<Message> messages = new ArrayList<>();
        Cursor cursor = mDatabase.query("Message",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                String msg_content;
                Date msg_time;
                int msg_type;
                msg_content = cursor.getString(cursor.getColumnIndex("msg_content"));
                msg_time = new Date(cursor.getInt(cursor.getColumnIndex("msg_time")));
                msg_type = cursor.getInt(cursor.getColumnIndex("msg_type"));
                messages.add(new Message(msg_time,msg_content,msg_type));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return messages;
    }
}
