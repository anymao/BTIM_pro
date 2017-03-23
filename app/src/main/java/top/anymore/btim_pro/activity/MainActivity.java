package top.anymore.btim_pro.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

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
        //为设置按钮设置点击事件
        menu.findItem(R.id.action_setting).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //当右边抽屉打开时候点击设置关闭右边抽屉
                if (mDrawerLayout.isDrawerOpen(rightMenuLayout)){
                    mDrawerLayout.closeDrawer(rightMenuLayout,true);
                }else {//否则打开右边抽屉
                    mDrawerLayout.openDrawer(rightMenuLayout,true);
                }
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void initViews() {
        //初始化控件
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        leftMenuLayout = (FrameLayout) findViewById(R.id.left_menu_layout);
        rightMenuLayout = (FrameLayout) findViewById(R.id.right_menu_layout);
        contentLayout = (FrameLayout) findViewById(R.id.content_layout);
        mTitle = (String) getTitle();
        mActionBar = getSupportActionBar();
        //动态加载三个碎片
        getSupportFragmentManager().beginTransaction()
                .add(R.id.left_menu_layout,new LeftMenuFragment())
                .add(R.id.content_layout,new ContentFragment())
                .add(R.id.right_menu_layout,new RightMenuFragment())
                .commit();
        //初始化mActionBarDrawerToggle，并且重写其中的方法，以达到监听抽屉状态的目的
        mActionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,mDrawerLayout,R.string.open,R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                switch (drawerView.getId()){
                    case R.id.left_menu_layout:
                        mActionBar.setTitle("左侧设置");
                        LogUtil.v(tag,"左侧菜单打开");
                        break;
                    case R.id.right_menu_layout:
                        mActionBar.setTitle("右侧设置");
                        LogUtil.v(tag,"右侧菜单打开");
                        break;
                }
                //必须重新绘制ActionBar布局，下同
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                switch (drawerView.getId()){
                    case R.id.left_menu_layout:
                        mActionBar.setTitle(mTitle);
                        LogUtil.v(tag,"左侧菜单关闭");
                        break;
                    case R.id.right_menu_layout:
                        mActionBar.setTitle(mTitle);
                        LogUtil.v(tag,"右侧菜单关闭");
                        break;
                }
                invalidateOptionsMenu();
            }
        };
        //为mDrawerLayout添加监听器
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
//        mActionBar = getSupportActionBar();
//        mActionBar.setHomeButtonEnabled(true);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setLogo(R.mipmap.ic_launcher);
//        mActionBar.setDisplayShowTitleEnabled(true);
//        mActionBar.setDis
    }
}
