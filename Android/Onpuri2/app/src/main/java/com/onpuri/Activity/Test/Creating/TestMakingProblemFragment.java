package com.onpuri.Activity.Test.Creating;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

/**
 * Created by kutemsys on 2016-09-17.
 */
public class TestMakingProblemFragment extends Fragment {
    private static final String TAG = "TestMakingProblemFragment";

    private static View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_test_making_problem, container, false);
        } catch (InflateException e) {}


        return view;
    }

}

