package com.jian.android.slidemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private List<String> mDatas;
    private SlideMenu mSlideMenu;
    private RelativeLayout mTitle;
    private MyLinearLayout linearLayout;
    private ImageView mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();
    }

    /**
     * 初始化数据操作
     */
    private void initData(){

        mDatas=new ArrayList<String>();

        for (int i=0;i<30;i++){
            mDatas.add("这是第"+i+"条测试数据");
        }

        MyAdapter adapter=new MyAdapter(mDatas);
        mRecycler.setAdapter(adapter);
    }

    /**
     * 初始化控件操作
     */
    private void initView(){
        mSlideMenu = (SlideMenu) findViewById(R.id.slide_menu);
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        mTitle = (RelativeLayout) findViewById(R.id.main_title);
        linearLayout = (MyLinearLayout) findViewById(R.id.my_linear);
        mMenu = (ImageView) findViewById(R.id.iv_menu);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        linearLayout.setSlideMenu(mSlideMenu);
    }

    /**
     * 初始化监听操作
     */
    private void initListener(){

        mSlideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
                Toast.makeText(MainActivity.this,"你打开了侧滑菜单",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClose() {
                Toast.makeText(MainActivity.this,"你关闭了侧滑菜单",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDraging(float fraction) {

                ViewHelper.setAlpha(mTitle,1-fraction/2);
            }
        });

        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideMenu.open();
            }
        });
    }

}
