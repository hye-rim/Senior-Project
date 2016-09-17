package com.onpuri.Activity.SideTab.Setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onpuri.R;

/**
 * Created by HYERIM on 2016-07-18.
 */

//약관 프래그먼트
public class UserSetTouFragment extends Fragment {
    private static View view;

    public static UserSetTouFragment newInstance() {
        UserSetTouFragment fragment = new UserSetTouFragment();
        return fragment;
    }

    public UserSetTouFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_my_set_tou, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
