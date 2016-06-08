package com.example.maxime.noteshare;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class SmartContentPagerAdapter extends FragmentStatePagerAdapter {

    private HashMap<String, String> smartContents;
    private ArrayList<String> requests;

    public SmartContentPagerAdapter(FragmentManager fm, HashMap<String, String> smartContents) {
        super(fm);
        this.smartContents = smartContents;
        this.requests = new ArrayList<>(smartContents.keySet());
    }

    @Override
    public Fragment getItem(int position) {
        SmartContentFragment smartContentFragment = new SmartContentFragment();
        smartContentFragment.setArguments(requests.get(position), smartContents.get(requests.get(position)));
        return smartContentFragment;
    }

    @Override
    public int getCount() {
        return smartContents.size();
    }
}
