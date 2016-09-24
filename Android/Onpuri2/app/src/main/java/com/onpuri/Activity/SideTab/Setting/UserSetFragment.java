package com.onpuri.Activity.SideTab.Setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.onpuri.Activity.SplashActivity;
import com.onpuri.R;
import com.onpuri.Activity.SideTab.Setting.workerLeave;

/**
 * Created by kutemsys on 2016-05-26.
 */

//설정 프래그먼트
public class UserSetFragment extends Fragment implements View.OnClickListener {
    private static View view;

    Button btn_notice, btn_question,  btn_tou, btn_out;
    FragmentManager mFragmentManager;

    String userId;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    private workerLeave mworker_leave;

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

        setting = getActivity().getSharedPreferences("setting",0);
        editor = setting.edit();

        userId = null;
        userId = getArguments().getString("SetId");

        btn_notice = (Button)view.findViewById(R.id.btn_set_notice);
        btn_question = (Button)view.findViewById(R.id.btn_set_question);
        btn_tou = (Button)view.findViewById(R.id.btn_set_tou);
        btn_out = (Button)view.findViewById(R.id.btn_set_out);

        btn_notice.setOnClickListener(this);
        btn_question.setOnClickListener(this);
        btn_tou.setOnClickListener(this);
        btn_out.setOnClickListener(this);
        return view;
    }
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction;
        switch (v.getId()){
            case R.id.btn_set_notice:
                mFragmentManager.beginTransaction()
                        .add(R.id.containerView, new UserSetNoticeFragment())
                        .addToBackStack("fragBack")
                        .commit();
                break;

            case R.id.btn_set_question:
                UserSetQuestionFragment setQuestionFragment = new UserSetQuestionFragment();
                Bundle args = new Bundle();
                args.putString("SetId",userId );
                setQuestionFragment.setArguments(args);

                mFragmentManager.beginTransaction()
                        .add(R.id.containerView, setQuestionFragment)
                        .addToBackStack("fragBack")
                        .commit();
                break;

            case R.id.btn_set_tou:
                mFragmentManager.beginTransaction()
                        .add(R.id.containerView, new UserSetTouFragment())
                        .addToBackStack("fragBack")
                        .commit();
                break;

            case R.id.btn_set_out:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

                alertBuilder.setTitle(" ");
                alertBuilder.setMessage("회원탈퇴를 하시겠습니까?");

                alertBuilder.setCancelable(false
                ).setPositiveButton("확인",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_LONG).show();
                        Leave();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertBuilder.create();
                alertDialog.show();  //<-- See This!

                break;
            default:
                break;
        }

    }
    private void Leave() {
        mworker_leave = new workerLeave(true);
        mworker_leave.start();

        try {
            mworker_leave.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(mworker_leave != null && mworker_leave.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_leave.interrupt();
        }

        if (setting.getBoolean("autoLogin", false)) {
            editor.clear();
            editor.commit();
        }

        Intent loginIntent = new Intent(getActivity(), SplashActivity.class);
        startActivity(loginIntent);
        getActivity().finish();

    }
}
