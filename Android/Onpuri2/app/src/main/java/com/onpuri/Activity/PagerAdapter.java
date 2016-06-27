package com.onpuri.Activity;

/**
 * Created by kutemsys on 2016-05-25.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int _numOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this._numOfTabs = numOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NoteFragment tab1 = new NoteFragment(); // Fragment 는 알아서 만들자
                return tab1;
            case 1:
                HomeFragment tab2 = new HomeFragment();
                return tab2;
            case 2:
                NewSenFragment tab3 = new NewSenFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return _numOfTabs;
    }
}