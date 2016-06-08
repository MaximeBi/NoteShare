package com.example.maxime.noteshare;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SmartContentPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> smartWord;
    private ArrayList<String> smartDef;

    public SmartContentPagerAdapter(FragmentManager fm, ArrayList<String> smartWord, ArrayList<String> smartDef) {
        super(fm);
        this.smartWord = smartWord;
        this.smartDef = smartDef;
    }

    @Override
    public Fragment getItem(int position) {
        SmartContentFragment smartContentFragment = new SmartContentFragment();
        smartContentFragment.setArguments(smartWord.get(position), smartDef.get(position));
        return smartContentFragment;
    }

    @Override
    public int getCount() {
        return smartWord.size();
    }
}
