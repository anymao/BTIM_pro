package top.anymore.btim_pro.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.fragment.ContentFragment;
import top.anymore.btim_pro.fragment.LeftMenuFragment;
import top.anymore.btim_pro.fragment.RightMenuFragment;
import top.anymore.btim_pro.logutil.LogUtil;

public class MainActivity extends AppCompatActivity {
    private static final String tag = "MainActivity";
    private FrameLayout leftMenuLayout,rightMenuLayout,contentLayout;//左，右，中三块容纳碎片的布局
    private ActionBarDrawerToggle mActionBarDrawerToggle;//ActionBar
    private DrawerLayout mDrawerLayout;//MainActivity的根布局
    private ActionBar mActionBar;
    private String mTitle;//用于存储最开始的标题
    //左中右碎片实例
    private LeftMenuFragment mLeftMenuFragment;
    private ContentFragment mContentFragment;
    private RightMenuFragment mRightMenuFragment;
    //退出(保留服务)标记
    private boolean isExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    //加载标题栏的功能按钮，在这里主要是右边的设置按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //获取左边抽屉的打开状态
        boolean isLeftDrawerOpen = mDrawerLayout.isDrawerOpen(leftMenuLayout);
        //设置ActionBar上的设置按钮的可见状态，在这里，左边抽屉打开，设置按钮不可见，反之可见
        menu.findItem(R.id.action_setting).setVisible(!isLeftDrawerOpen);
        //在onOptionsItemSelected实现
//        //为设置按钮设置点击事件
//        menu.findItem(R.id.action_setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                //当右边抽屉打开时候点击设置关闭右边抽屉
//                if (mDrawerLayout.isDrawerOpen(rightMenuLayout)){
//                    mDrawerLayout.closeDrawer(rightMenuLayout,true);
//                }else {//否则打开右边抽屉
//                    mDrawerLayout.openDrawer(rightMenuLayout,true);
//                }
//                return false;
//            }
//        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //实现点击左上角图标时候左边抽屉的开关
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_setting:
                //当右边抽屉打开时候点击设置关闭右边抽屉
                if (mDrawerLayout.isDrawerOpen(rightMenuLayout)){
                    mDrawerLayout.closeDrawer(rightMenuLayout,true);
                }else {//否则打开右边抽屉
                    mDrawerLayout.openDrawer(rightMenuLayout,true);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //需要将ActionBarDrawerToggle与DrawerLayout同步
        //将ActionBarDrawerToggle中的drawer图标与ActionBar中的Home-Button同步
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initViews() {
        //初始化控件
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        leftMenuLayout = (FrameLayout) findViewById(R.id.left_menu_layout);
        rightMenuLayout = (FrameLayout) findViewById(R.id.right_menu_layout);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        mTitle = (String) getTitle();
        mActionBar = getSupportActionBar();
        mLeftMenuFragment = new LeftMenuFragment();
        mContentFragment = new ContentFragment();
        mRightMenuFragment = new RightMenuFragment();
        //动态加载三个碎片
        getSupportFragmentManager().beginTransaction()
                .add(R.id.left_menu_layout,mLeftMenuFragment)
                .add(R.id.content_layout,mContentFragment)
                .add(R.id.right_menu_layout,mRightMenuFragment)
                .commit();
        //初始化mActionBarDrawerToggle，并且重写其中的方法，以达到监听抽屉状态的目的
        mActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,R.string.open,R.string.close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                switch (drawerView.getId()){
                    case R.id.left_menu_layout:
                        mActionBar.setTitle("功能");
//                        LogUtil.v(tag,"左侧菜单打开");
                        break;
                    case R.id.right_menu_layout:
                        mActionBar.setTitle("设置");
                        //关闭左边图标
                        mActionBar.setDisplayHomeAsUpEnabled(false);
                        //使左上角图标不可用
                        mActionBar.setHomeButtonEnabled(false);
//                        LogUtil.v(tag,"右侧菜单打开");
                        break;
                }
                //必须重新绘制ActionBar布局，下同
                //会调用onPrepareOptionsMenu()，所以需要重新此函数实现右边图标隐藏与否
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                switch (drawerView.getId()){
                    case R.id.left_menu_layout:
                        mActionBar.setTitle(mTitle);
//                        LogUtil.v(tag,"左侧菜单关闭");
                        break;
                    case R.id.right_menu_layout:
                        mActionBar.setTitle(mTitle);
                        //显示左边图标
                        mActionBar.setDisplayHomeAsUpEnabled(true);
                        //使左上角图标可用
                        mActionBar.setHomeButtonEnabled(true);
//                        LogUtil.v(tag,"右侧菜单关闭");
                        break;
                }
                invalidateOptionsMenu();
            }

        };
        //为mDrawerLayout添加监听器
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        //关闭右侧滑动开启，只能通过右上角设置按钮打开
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,rightMenuLayout);
        //显示左边图标
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //使左上角图标可用
        mActionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播接收器
        unregisterReceiver(mLeftMenuFragment.receiver);
        unregisterReceiver(mContentFragment.receiver);
        unbindService(mContentFragment.serviceConnection);
    }

    /**
     * 实现了功能，当左抽屉或者右抽屉打开时候，关闭抽屉
     * 否则弹出提示“再按一次返回到桌面”，并且在1.5秒内再次点击，会返回桌面
     */
    @Override
    public void onBackPressed() {
        boolean isLeftDrawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
        boolean isRightDrawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.END);
        if (isLeftDrawerOpen || isRightDrawerOpen){
            if (isLeftDrawerOpen){
                mDrawerLayout.closeDrawer(GravityCompat.START,true);
            }
            if (isRightDrawerOpen){
                mDrawerLayout.closeDrawer(GravityCompat.END,true);
            }
        }else{
            //退出操作，但是服务运行
            if (!isExit){
                Toast.makeText(MainActivity.this,"再按一次返回到桌面",Toast.LENGTH_SHORT).show();
                isExit = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isExit = false;
                    }
                }).start();
            }else {
                finish();
            }
        }
//        super.onBackPressed();
    }
}
