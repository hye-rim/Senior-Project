package com.onpuri.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.ActivityList;


public class UserMyFragment extends Fragment implements View.OnClickListener {

    TextView tv_userID, tv_userName, tv_userJoinDate, tv_userPhone, tv_userNowPass;
    Button btnOk, btnCancel;
    private static View view;

    String userId, name, joinDate, phone, nowPass ;

    private com.onpuri.Server.CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();
    private FragmentTransaction fragmentTransaction;
    private FragmentManager mFragmentManager;

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
        mFragmentManager = getFragmentManager();

        userId = new String();
        userId = getArguments().getString("MyId");
        tv_userID = (TextView)view.findViewById(R.id.tv_userID);
        tv_userID.setText(userId);

        name = new String();
        name = getArguments().getString("MyName");
        tv_userName = (TextView)view.findViewById(R.id.tv_userName);
        tv_userName.setText(name);

        joinDate = new String();
        joinDate = getArguments().getString("MyJoin");
        tv_userJoinDate = (TextView)view.findViewById(R.id.tv_userDate);
        tv_userJoinDate.setText(joinDate);

        phone = new String();
        phone = getArguments().getString("MyPhone");
        tv_userPhone = (TextView)view.findViewById(R.id.tv_userPhone);
        tv_userPhone.setText(phone);

        nowPass = new String();
        nowPass = getArguments().getString("MyPass");

        btnOk = (Button)view.findViewById(R.id.btn_my_ok);
        btnCancel = (Button)view.findViewById(R.id.btn_my_cancel);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_my_ok:
                Toast.makeText(getActivity(), "정보수정은 구현예정입니다.", Toast.LENGTH_SHORT).show();

                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new TabViewPager()).commit();

                break;
            case R.id.btn_my_cancel:
                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new TabViewPager()).commit();

                break;
            default:
                break;
        }

    }
}
