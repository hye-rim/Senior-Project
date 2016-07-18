package com.onpuri.Activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onpuri.R;

/**
 * Created by HYERIM on 2016-07-18.
 */
public class UserSetVersionFragment extends Fragment {
    private static final String TAG = "UserSetVersionFragment";
    private static View view;

    private TextView tvVersion;

    public static UserSetVersionFragment newInstance() {
        UserSetVersionFragment fragment = new UserSetVersionFragment();
        return fragment;
    }

    public UserSetVersionFragment() {

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
            view = inflater.inflate(R.layout.fragment_my_set_version, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }

        PackageInfo pi = null;
        try {
            pi = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        String appVersion = pi.versionName;

        tvVersion = (TextView)view.findViewById(R.id.tv_set_version);
        tvVersion.setText("Ver. " + appVersion);
        return view;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
