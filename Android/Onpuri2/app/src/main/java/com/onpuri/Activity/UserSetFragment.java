package com.onpuri.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onpuri.R;
import com.onpuri.Server.ActivityList;

/**
 * Created by kutemsys on 2016-05-26.
 */
public class UserSetFragment extends Fragment implements View.OnClickListener {
    private com.onpuri.Server.CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();
    private static View view;

    Button btn_notice, btn_question, btn_version, btn_tou, btn_out;
    FragmentManager mFragmentManager;


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
        mFragmentManager = getFragmentManager();

        btn_notice = (Button)view.findViewById(R.id.btn_set_notice);
        btn_question = (Button)view.findViewById(R.id.btn_set_question);
        btn_version = (Button)view.findViewById(R.id.btn_set_version);
        btn_tou = (Button)view.findViewById(R.id.btn_set_tou);
        btn_out = (Button)view.findViewById(R.id.btn_set_out);

        btn_notice.setOnClickListener(this);
        btn_question.setOnClickListener(this);
        btn_version.setOnClickListener(this);
        btn_tou.setOnClickListener(this);
        btn_out.setOnClickListener(this);
        return view;
    }
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void onBackPressed(){
        CloseSystem.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction;
        switch (v.getId()){
            case R.id.btn_set_notice:
                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new UserSetNoticeFragment()).commit();
                break;
            case R.id.btn_set_question:
                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new UserSetQuestionFragment()).commit();
                break;
            case R.id.btn_set_version:
                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new UserSetVersionFragment()).commit();
                break;
            case R.id.btn_set_tou:
                fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerView, new UserSetTouFragment()).commit();
                break;
            case R.id.btn_set_out:

                break;
            default:
                break;
        }

    }
}
