package com.yuyi.family.test.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.yuyi.family.fragment.SelectDateFragment;
import com.yuyi.family.fragment.SelectEndTimeFragment;
import com.yuyi.family.fragment.SelectStartTimeFragment;

public class SelectTimeAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 3;
    private SelectDateFragment selectDateFragment = null;
    private SelectStartTimeFragment selectStartTimeFragment = null;
    private SelectEndTimeFragment selectEndTimeFragment= null;

    public SelectTimeAdapter(FragmentManager fm) {
        super(fm);
        selectDateFragment = new SelectDateFragment();
        selectStartTimeFragment = new SelectStartTimeFragment();
        selectEndTimeFragment = new SelectEndTimeFragment();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = selectDateFragment;
                break;
            case 1:
                fragment = selectStartTimeFragment;
                break;
            case 2:
                fragment = selectEndTimeFragment;
                break;
        }
        return fragment;
    }
}
