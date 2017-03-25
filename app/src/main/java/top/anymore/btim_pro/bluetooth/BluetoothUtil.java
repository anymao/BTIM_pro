package top.anymore.btim_pro.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-3-16.
 * 提供对手机蓝牙适配器的基本操作
 * 如打开关闭蓝牙，扫描与可见性的操作等
 */

public class BluetoothUtil {
    private static final String tag = "BluetoothUtil";
    private BluetoothAdapter mBluetoothAdapter;

    public static final String VISIBILITY_YES = "已开启,允许周围设备检测到";
    public static final String VISIBILITY_NO = "已关闭,仅允许配对设备检测到";

    public BluetoothUtil() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            LogUtil.e(tag,"本机没有蓝牙设备，APP出现异常");
        }else{
            LogUtil.v(tag,"本机拥有蓝牙设备");
        }
    }

    /**
     * 判断当前蓝牙是否打开
     * @return打开时true，否则false
     */
    public boolean isBluetoothOpened(){
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * 打开蓝牙设备
     * @return成功时true，否则false
     */
    public boolean openBluetooth(){
        return mBluetoothAdapter.enable();
    }

    /**
     * 关闭蓝牙设备
     * @return成功true，失败false
     */
    public boolean closeBluetooth(){
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            if (mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
            }
            return mBluetoothAdapter.disable();
        }
        LogUtil.v(tag,"已经关闭");
        return true;
    }

    /**
     * 开始扫描设备
     * @return成功true，失败false
     */
    public boolean startDiscovery(){
        return mBluetoothAdapter.startDiscovery();
    }

    /**
     * 设置蓝牙可见性
     * @param enable
     * enable为true时，可被周边设备在120秒内搜索到
     * false时，不可被搜索到
     */
    public void setVisibility(boolean enable){
        try {
//            Method setDiscoveryableTimeout = BluetoothUtil.class.getMethod()
            Method setScanMode = BluetoothAdapter.class.getMethod("setScanMode",int.class,int.class);
            setScanMode.setAccessible(true);
            if (enable){
                setScanMode.invoke(mBluetoothAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,120);
            }else{
                setScanMode.invoke(mBluetoothAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE,120);
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        if (!enable){
//            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,0);
//        }
//        context.startActivity(intent);
//        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        Bundle bundle = new Bundle();
//        if (b) {
//            bundle.putInt(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
//        }
//        else {
//            bundle.putInt(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.SCAN_MODE_NONE);
//        }
//        context.startActivity(intent);
    }

    /**
     * 结束扫描周边设备
     * @return成功true，失败false
     */
    public boolean endDiscovery(){
        return mBluetoothAdapter.cancelDiscovery();
    }

    /**
     * 判断当前蓝牙设备是否正在扫描
     * @return
     */
    public boolean isDiscovering(){
        return mBluetoothAdapter.isDiscovering();
    }

    /**
     * 获取已配对设备列表
     * @return
     */
    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> pairedDevices = new ArrayList<>();
        if (!mBluetoothAdapter.isEnabled()){
            LogUtil.v(tag,"蓝牙已关闭，获取配对列表失败");
            return pairedDevices;
        }
        //从集合中转换成列表，方便之后在recylerview中展示
        Set<BluetoothDevice> pairedDeviceSet = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDeviceSet) {
            LogUtil.v(tag,device.getName());
            pairedDevices.add(device);
        }
        return pairedDevices;
    }
}
