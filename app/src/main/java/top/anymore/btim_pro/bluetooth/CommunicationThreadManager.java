package top.anymore.btim_pro.bluetooth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anymore on 17-3-24.
 */

public class CommunicationThreadManager {
    //把连接后的通信线程添加到管理器中，通过position取出，方便在不同activity中调用
    private static List<CommunicationThread> communicationThreads = new ArrayList<>();
    public static final String THREAD_POSTION = "com.any_more.btim_pro.thread_postion";
    public static int addCommunicationThread(CommunicationThread thread){
        communicationThreads.add(thread);
        return communicationThreads.indexOf(thread);
    }
    public static CommunicationThread getCommunicationThread(int postion){
        return communicationThreads.get(postion);
    }
    public static void removeCommunicationThread(CommunicationThread thread){
        communicationThreads.remove(thread);
    }
}
