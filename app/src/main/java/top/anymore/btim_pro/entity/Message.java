package top.anymore.btim_pro.entity;


import java.sql.Date;

/**
 * Created by anymore on 17-3-26.
 */

public class Message {
    public static final int MESSAGE_TYPE_GET = 0;
    public static final int MESSAGE_TYPE_SEND = 1;
    private Date mDate;
    private String content;
    private int type;

    public Message() {
    }

    public Message(Date mDate, String content, int type) {
        this.mDate = mDate;
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
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }
}
