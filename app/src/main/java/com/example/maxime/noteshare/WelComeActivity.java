package com.example.maxime.noteshare;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class WelComeActivity extends Activity {
    private int[] ids = { R.drawable.demo_save, R.drawable.demo_local, R.drawable.demo_distant};

    private List<View> guides = new ArrayList<>();
    private ViewPager pager;
    private ImageView curDot;
    // 位移量
    private int offset;
    // 记录当前的位置
    private int curPos = 0;

    SharedPreferences share;

    SharedPreferences.Editor editor;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        share = getSharedPreferences("showWelcomm", Context.MODE_PRIVATE);
        editor = share.edit();
        // if first log in
//        if (share.contains("shownum")) {
//            setContentView(R.layout.activity_main);
//            int num = share.getInt("shownum", 0);
//            editor.putInt("shownum", num++);
//            editor.commit();
//            skipActivity(1);
//        } else {
//            editor.putInt("shownum", 1);
//            editor.commit();
//            setContentView(R.layout.welcome_page);
//            initView();
//        }
        editor.putInt("shownum", 1);
        editor.commit();
        setContentView(R.layout.welcome_page);
        initView();
    }

    private void initView() {
        for (int i = 0; i < ids.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(ids[i]);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            guides.add(iv);
        }
        curDot = (ImageView) findViewById(R.id.cur_dot);
        curDot.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        offset = curDot.getWidth();
                        return true;
                    }
                });

        WelcomePagerAdapter adapter = new WelcomePagerAdapter(guides);
        pager = (ViewPager) findViewById(R.id.showwelom_page);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int arg0) {
                moveCursorTo(arg0);
                if (arg0 == ids.length - 1) {// 到最后一张了
                    skipActivity(2);
                }
                curPos = arg0;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }

        });

    }
    /**
     * 移动指针到相邻的位置
     *
     * @param position
     *            指针的索引值
     * */
    private void moveCursorTo(int position) {
        TranslateAnimation anim = new TranslateAnimation(offset * curPos,
                offset * position, 0, 0);
        anim.setDuration(300);
        anim.setFillAfter(true);
        curDot.startAnimation(anim);
    }


    /**
     * 延迟多少秒进入主界面
     * @param min 秒
     */
    private void skipActivity(int min) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
//                Intent intent = new Intent(WelComeActivity.this, MainActivity.class);
//                startActivity(intent);
                WelComeActivity.this.finish();
            }
        }, 1000 * min);
    }

}
