package top.anymore.btim_pro.entity;


import java.io.Serializable;
import java.util.Date;

/**
 * 发送的数据实体类，包括时间，内容，方向等内容
 * Created by anymore on 17-3-26.
 */

public class Message implements Serializable{
    public static final int MESSAGE_TYPE_GET = 0;
    public static final int MESSAGE_TYPE_SEND = 1;
    private Date mTime;
    private String content;
    private int type;

    public Message() {
    }

    public Message(Date mTime, String content, int type) {
        this.mTime = mTime;
        this.content = content;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return mTime;
    }

    public void setDate(Date time) {
        this.mTime = time;
    }
}
