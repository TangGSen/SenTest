package com.sen.haoliyou.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mozillaonline.providers.DownloadManager;
import com.sen.haoliyou.R;
import com.sen.haoliyou.base.BaseActivity;
import com.sen.haoliyou.fragment.FragmentRepository;
import com.sen.haoliyou.fragment.FragmentStudy;
import com.sen.haoliyou.fragment.FragmentTest;
import com.sen.haoliyou.tools.ResourcesUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity {


    @Bind(R.id.home_layout_content)
    FrameLayout home_layout_content;
    @Bind(R.id.layout_buttom_tab)
    TabLayout layout_buttom_tab;
    //tab item name
    String tabTiles[];
    //tab item drawable
    int tabItemDrawableNormal[];
    int tabItemDrawableSelected[];
    private int tabCount;

    FragmentManager mFragmentManager;

    private FragmentStudy mFragmentStudy;
    private FragmentTest mFragmentTest;
    private FragmentRepository mFragmentRepository;

    private int currentFragPosition = 0;
    private final String FRAG_POSITION = "currentFragPosition";


    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DownloadManager mDownloadManager = new DownloadManager(getContentResolver(), getPackageName());


    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        tabTiles = ResourcesUtils.getStringArray(this, R.array.tabButtonItemName);
        tabItemDrawableNormal = new int[]{R.drawable.ic_tab_home_normal, R.drawable.ic_tab_classification_normal,  R.drawable.ic_tab_personal_normal};
        tabItemDrawableSelected = new int[]{R.drawable.ic_tab_home_selected, R.drawable.ic_tab_classification_selected,  R.drawable.ic_tab_personal_selected};
        initTabView();
        mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null) {

            //取出上一次保存的数据
            currentFragPosition = savedInstanceState.getInt(FRAG_POSITION,0);
            Log.e("sen","恢复的状态"+currentFragPosition);
            mFragmentStudy = (FragmentStudy) mFragmentManager.findFragmentByTag(tabTiles[0]);
            mFragmentTest = (FragmentTest) mFragmentManager.findFragmentByTag(tabTiles[1]);
            mFragmentRepository = (FragmentRepository) mFragmentManager.findFragmentByTag(tabTiles[2]);

            layout_buttom_tab.getTabAt(currentFragPosition).select();


        }

        setSelectedFragment(currentFragPosition);
    }



    private void setSelectedFragment(int position) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        hideAllFragments(transaction);
        switch (position) {
            case 0:
                if (mFragmentStudy == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mFragmentStudy = new FragmentStudy();
                    transaction.add(R.id.home_layout_content, mFragmentStudy, tabTiles[position]);
                } else {
                    // 如果不为空，则直接将它显示出来
                    transaction.show(mFragmentStudy);
                }
                break;
            case 1:
                if (mFragmentTest == null) {
                    mFragmentTest = new FragmentTest();
                    transaction.add(R.id.home_layout_content, mFragmentTest, tabTiles[position]);
                } else {
                    transaction.show(mFragmentTest);
                }
                break;
            case 2:
                if (mFragmentRepository == null) {
                    mFragmentRepository = new FragmentRepository();
                    transaction.add(R.id.home_layout_content, mFragmentRepository, tabTiles[position]);
                } else {
                    transaction.show(mFragmentRepository);
                }
                break;

        }
        currentFragPosition = position;
        transaction.commit();
    }

    private void hideAllFragments(FragmentTransaction transaction) {
        if (mFragmentStudy != null) {
            transaction.hide(mFragmentStudy);
        }
        if (mFragmentTest != null) {
            transaction.hide(mFragmentTest);
        }
        if (mFragmentRepository != null) {
            transaction.hide(mFragmentRepository);
        }

    }

    //系统销毁Activity 的时候保存Fragment 的状态
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //保存tab选中的状态
        Log.e("sen","保存tab选中的状态"+currentFragPosition);
        outState.putInt(FRAG_POSITION, currentFragPosition);
        super.onSaveInstanceState(outState);
    }



    private void initTabView() {
        tabCount = tabItemDrawableNormal.length;
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tab = layout_buttom_tab.newTab();
            tab.setCustomView(getTabView(tabItemDrawableNormal[i], tabTiles[i]));
            layout_buttom_tab.addTab(tab, i);
        }

        layout_buttom_tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int positionTab = tab.getPosition();
                AppCompatTextView textView = (AppCompatTextView) tab.getCustomView();
                changeSelecteTabColor(textView, tabItemDrawableSelected[positionTab], true);
                setSelectedFragment(positionTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int positionTab = tab.getPosition();
                AppCompatTextView textView = (AppCompatTextView) tab.getCustomView();
                changeSelecteTabColor(textView, tabItemDrawableNormal[positionTab], false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //不知为啥 0 的不会调用setOnTabSelectedListener 的方法
        //  layout_buttom_tab.getTabAt(0).select();


        AppCompatTextView textView = (AppCompatTextView) layout_buttom_tab.getTabAt(0).getCustomView();

        changeSelecteTabColor(textView, tabItemDrawableSelected[0], true);

    }

    @Override
    protected void initActionBar() {
        super.initActionBar();


    }

    //自定义TabView
    public View getTabView(int id, String text) {
        View view = LayoutInflater.from(this).inflate(R.layout.main_item_tab, null);
        AppCompatTextView textView = (AppCompatTextView) view.findViewById(R.id.tab_name);
        //设置图片
        Drawable topDrawable = ResourcesUtils.getResDrawable(this, id);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        textView.setCompoundDrawables(null, topDrawable, null, null);
        textView.setText(text);
        return view;
    }

    public void changeSelecteTabColor(AppCompatTextView textView, int drawableId, boolean isSelected) {
        Drawable topDrawable = ResourcesUtils.getResDrawable(this, drawableId);
        topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
        textView.setCompoundDrawables(null, topDrawable, null, null);
        textView.setSelected(isSelected);
    }





}
