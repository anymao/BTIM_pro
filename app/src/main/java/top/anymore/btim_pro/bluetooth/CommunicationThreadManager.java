package top.anymore.btim_pro.bluetooth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anymore on 17-3-24.
 */

public class CommunicationThreadManager {
    //把连接后的通信线程添加到管理器中，通过position取出，方便在不同activity中调用
//    private static List<CommunicationThread> communicationThreads = new ArrayList<>();
//    public static final String THREAD_POSTION = "com.any_more.btim_pro.thread_postion";
    private static CommunicationThread currentThread;
    public static void addCommunicationThread(CommunicationThread thread){
        currentThread = thread;
    }
    public static CommunicationThread getCommunicationThread(){
        return currentThread;
    }
//    public static void removeCommunicationThread(CommunicationThread thread){
//        communicationThreads.remove(thread);
//    }
}
