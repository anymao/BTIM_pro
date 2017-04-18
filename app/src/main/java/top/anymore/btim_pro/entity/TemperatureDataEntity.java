package top.anymore.btim_pro.entity;

/**
 * 房间温度数据实体类包括房间号，时间，实际温度，警戒温度，是否超过警戒温度,是否处理等字段
 * Created by anymore on 17-4-1.
 */

public class TemperatureDataEntity {
    private int room_id;//房间号
    private long time;//时间
    private double real_temper;//实际温度
    private double warn_temper;//警戒温度
    private int is_dager;//是否超过警戒温度
    private int is_handle;//超过警戒温度是否处理
    public static final int STATE_NOT_DANGER = 0;
    public static final int STATE_DANGER = 1;
    public static final int STATE_HANDLE = 3;
    public static final int STATE_NOT_HANDLE = 4;

    public TemperatureDataEntity(int room_id, long time, double real_temper, double warn_temper, int is_dager, int is_handle) {
        this.room_id = room_id;
        this.time = time;
        this.real_temper = real_temper;
        this.warn_temper = warn_temper;
        this.is_dager = is_dager;
        this.is_handle = is_handle;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getReal_temper() {
        return real_temper;
    }

    public void setReal_temper(double real_temper) {
        this.real_temper = real_temper;
    }

    public double getWarn_temper() {
        return warn_temper;
    }

    public void setWarn_temper(double warn_temper) {
        this.warn_temper = warn_temper;
    }

    public int getIs_handle() {
        return is_handle;
    }

    public void setIs_handle(int is_handle) {
        this.is_handle = is_handle;
    }

    public int getIs_dager() {
        return is_dager;
    }

    public void setIs_dager(int is_dager) {
        this.is_dager = is_dager;
    }

    @Override
    public String toString() {
        return "TemperatureDataEntity{" +
                "room_id=" + room_id +
                ", time=" + time +
                ", real_temper=" + real_temper +
                ", warn_temper=" + warn_temper +
                ", is_dager=" + is_dager +
                ", is_handle=" + is_handle +
                '}';
    }
}
