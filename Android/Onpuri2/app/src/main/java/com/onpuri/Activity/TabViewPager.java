package com.onpuri.Activity;

/**
 * Created by kutemsys on 2016-06-27.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onpuri.R;
import com.tsengvn.typekit.TypekitContextWrapper;

//Fragment Tab 설정
public class TabViewPager extends Fragment {
    private static final String TAG = "TabViewPager";

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 4;
    private String userId;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate tab_layout and setup Views.
        View x = inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        userId = null;
        userId = getArguments().getString("UserId");

        // Set an Apater for the View Pager
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        viewPager.setCurrentItem(0);
        return x;
    }

    SparseArray< View > views = new SparseArray< View >();

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    HomeRootFragment tab1 = new HomeRootFragment();
                    bundle = new Bundle();
                    bundle.putString("UserId", userId); //아이디 전송
                    tab1.setArguments(bundle);
                    return tab1;

                case 1:
                    NoteRootFragment tab2 = new NoteRootFragment();
                    return tab2;

                case 2:
                    TestFragment tab3 = new TestFragment();
                    return tab3;

                case 3:
                    NewSenFragment tab4 = new NewSenFragment();
                    return tab4;
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }
        /**
         * This method returns the title of the tab according to the position.
         */
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "홈";
                case 1:
                    return "내노트";
                case 2:
                    return "시험";
                case 3:
                    return "문장등록";
            }
            return null;
        }
    }
}