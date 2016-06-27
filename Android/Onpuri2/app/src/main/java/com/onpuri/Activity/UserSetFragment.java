package com.onpuri.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onpuri.R;
import com.onpuri.Server.ActivityList;

/**
 * Created by kutemsys on 2016-05-26.
 */
public class UserSetFragment extends Fragment {
    private com.onpuri.Server.CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();
    private static View view;

    public static UserSetFragment newInstance() {
        UserSetFragment fragment = new UserSetFragment();
        return fragment;
    }

    public UserSetFragment() {

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
            view = inflater.inflate(R.layout.fragment_my_set, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    public void onBackPressed(){
        CloseSystem.onBackPressed();
    }
}
