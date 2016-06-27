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


public class UserMyFragment extends Fragment {

    TextView tv_userID,  tv_userShell, tv_userProblem, tv_userAverage, tv_userRank;
    TextView tv_userQuestion, tv_userSolving, tv_userAttend, tv_userPurchase, tv_userSale, tv_userDeclaration;
    Button btnOk;
    private static View view;

    private com.onpuri.Server.CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();

    public static UserMyFragment newInstance() {
        UserMyFragment fragment = new UserMyFragment();
        return fragment;
    }

    public UserMyFragment() {

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
            view = inflater.inflate(R.layout.fragment_my, container, false);
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
