package com.onpuri.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.ActivityList;
import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-05-26.
 */
public class UserSetFragment extends Fragment implements View.OnClickListener {
    private ActivityList actManager = ActivityList.getInstance();
    private static View view;

    Button btn_notice, btn_question, btn_version, btn_tou, btn_out;
    FragmentManager mFragmentManager;

    String userId;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    private char check_out;
    private worker_leave mworker_leave;

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

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction;
        switch (v.getId()){
            case R.id.btn_set_notice:
                mFragmentManager.beginTransaction()
                        .replace(R.id.containerView, new UserSetNoticeFragment())
                        .commit();
                break;

            case R.id.btn_set_question:
                UserSetQuestionFragment setQuestionFragment = new UserSetQuestionFragment();
                Bundle args = new Bundle();
                args.putString("SetId",userId );
                setQuestionFragment.setArguments(args);

                mFragmentManager.beginTransaction()
                        .replace(R.id.containerView, setQuestionFragment)
                        .commit();
                break;

            case R.id.btn_set_version:
                mFragmentManager.beginTransaction()
                        .replace(R.id.containerView, new UserSetVersionFragment())
                        .commit();
                break;

            case R.id.btn_set_tou:
                mFragmentManager.beginTransaction()
                        .replace(R.id.containerView, new UserSetTouFragment())
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
        mworker_leave = new worker_leave(true);
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

    }
    class worker_leave extends Thread {
        private boolean isPlay = false;
        private byte isGenerated;

        public worker_leave(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void setThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {

                byte out = 1;

                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_LEV;
                outData[2] = (byte) PacketInfo.getSEQ();
                outData[3] = out;
                outData[4] = out;
                outData[5] = (byte) 85;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3] + 5);
                    dos.flush();
                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(inData);
                    //System.out.println("Data form server: " + ((char)inData[0].) + (char)inData[1]);
                    int SOF = inData[0];

                    check_out = (char) inData[4];

                    System.out.println(inData[0]);
                    System.out.println(inData[1]);
                    System.out.println(inData[2]);
                    System.out.println(inData[3]);
                    System.out.println((char) inData[4]);
                    System.out.println(inData[5]);

                    if (check_out == '0' || check_out == '1')
                        isPlay = !isPlay;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
