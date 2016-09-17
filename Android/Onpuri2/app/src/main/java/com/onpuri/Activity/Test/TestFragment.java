package com.onpuri.Activity.Test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.onpuri.Activity.Test.Creating.TestCreateFragment;
import com.onpuri.Activity.Test.Solving.TestSolveFragment;
import com.onpuri.R;

/**
 * Created by kutemsys on 2016-09-02.
 */
public class TestFragment  extends Fragment implements View.OnClickListener{
    private static final String TAG = "TestFragment";

    private static View view;

    private ImageButton btn_solving, btn_creating;

    ViewPager viewPager;

    int i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_test, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);

        btn_solving = (ImageButton)view.findViewById(R.id.btn_test_solving);
        btn_creating = (ImageButton)view.findViewById(R.id.btn_test_creating);
        btn_solving.setOnClickListener(this);
        btn_creating.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        final FragmentManager fm = getActivity().getSupportFragmentManager();

        switch (v.getId()){
            case R.id.btn_test_solving:
                final TestSolveFragment tsf = new TestSolveFragment();
                fm.beginTransaction()
                        .replace(R.id.root_test, tsf)
                        .addToBackStack(null)
                        .commit();
                break;

            case R.id.btn_test_creating:
                final TestCreateFragment testCreateFragment = new TestCreateFragment();
                fm.beginTransaction()
                        .replace(R.id.root_test, testCreateFragment)
                        .addToBackStack(null)
                        .commit();
                break;

            default:
                break;
        }
    }
}
