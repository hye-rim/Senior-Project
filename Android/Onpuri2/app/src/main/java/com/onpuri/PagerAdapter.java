package com.onpuri;

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
                UserProfile tab1 = new UserProfile(); // Fragment 는 알아서 만들자
                return tab1;
            case 1:
                UserMain tab2 = new UserMain();
                return tab2;
            case 2:
                UserNewSen tab3 = new UserNewSen();
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