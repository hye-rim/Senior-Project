package com.onpuri.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onpuri.R;
import com.onpuri.Server.ActivityList;

/**
 * Created by HYERIM on 2016-07-18.
 */
public class UserSetNoticeFragment extends Fragment {
    private static View view;

    public static UserSetNoticeFragment newInstance() {
        UserSetNoticeFragment fragment = new UserSetNoticeFragment();
        return fragment;
    }

    public UserSetNoticeFragment() {

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
            view = inflater.inflate(R.layout.fragment_my_set_notice, container, false);
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
