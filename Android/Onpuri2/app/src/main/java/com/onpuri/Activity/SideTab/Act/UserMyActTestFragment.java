package com.onpuri.Activity.SideTab.Act;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.onpuri.R;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class UserMyActTestFragment extends Fragment {
    private static final String TAG = "UserMyActTestFragment";
    private static View view;

    private RecyclerView mRecyclerNew;
    private RecyclerView.Adapter mNewAdapter;

    protected RecyclerView.LayoutManager mLayoutManager;

    public static UserMyActFragment newInstance() {
        UserMyActFragment fragment = new UserMyActFragment();
        return fragment;
    }

    public UserMyActTestFragment() {
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
            view = inflater.inflate(R.layout.fragment_my_act, container, false);
        } catch (InflateException e) {
            //map is already there, just return view as it is
        }

        return view;
    }

    private void initActData() {
    }
}
