package com.onpuri.Activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by kutemsys on 2016-05-03.
 */
//문장등록 tab
public class NewSenFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "NewSenFragment";
    private worker_add_sen worker_add_sen;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private static View view;

    private Button btn_ok, btn_cancel;
    private EditText mSentenceEditText;

    ViewPager viewPager;

    int i, checkLen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_new_sen, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewpager);

        mSentenceEditText = (EditText) view.findViewById(R.id.et_new_sentence);
        mSentenceEditText.setFilters(new InputFilter[]{filterAlphaNum});
        mSentenceEditText.setPrivateImeOptions("defaultInputmode=english;");

        btn_ok = (Button)view.findViewById(R.id.btn_new_sen);
        btn_cancel = (Button)view.findViewById(R.id.btn_new_sen_back);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_new_sen:
                new AlertDialog.Builder(getActivity())
                        .setTitle("문장을 등록하시겠습니까?\n등록후에는 수정이 불가능합니다.")
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if(keyCode == KeyEvent.KEYCODE_BACK){
                                    dialog.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        })
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                checkingLen();

                                checkEnrollment();

                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;

            case R.id.btn_new_sen_back:
                new AlertDialog.Builder(getActivity())
                        .setTitle("문장 등록을 취소하시겠습니까?\n작성중인 내용이 전부 사라집니다.")
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if(keyCode == KeyEvent.KEYCODE_BACK){
                                    dialog.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        })
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mSentenceEditText.setText("");
                                viewPager.setCurrentItem(0);
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;

            default:
                break;
        }
    }

    private void checkEnrollment() {
        switch (checkLen){
            case 0: //ok
                Addsentence();
                Toast.makeText(getActivity(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                mSentenceEditText.setText("");
                viewPager.setCurrentItem(0);
                break;

            case 1: //null
                Toast.makeText(getActivity(), "문장을 입력해주세요.", Toast.LENGTH_SHORT).show();
                break;

            case 2: //max
                Toast.makeText(getActivity(), "최대 글자 수(230자)를 초과하셨습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void checkingLen() {
        String inputSentence = mSentenceEditText.getText().toString();

        if(inputSentence.compareTo("") == 0 || mSentenceEditText.getText().toString().isEmpty()){
            checkLen = 1; //null
        }else if( inputSentence.getBytes().length >= 230){
            checkLen = 2;//max
        }else{
            checkLen = 0; //OK
        }
    }

    public InputFilter filterAlphaNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
            Pattern ps = Pattern.compile("^[.,-_a-zA-Z0-9 ]+"); //영문, 숫자, 띄어쓰기 허용
            if(source.equals("")|| ps.matcher(source).matches()){
                source.equals(""); //백스페이스를 위해 추가한 부분
                return source;
            }

            return "";
        }
    };

    private void Addsentence() {
        if(worker_add_sen != null && worker_add_sen.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_add_sen.interrupt();
        }
        worker_add_sen = new worker_add_sen(true);
        worker_add_sen.start();
        try {
            worker_add_sen.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class worker_add_sen extends Thread {
        private boolean isPlay = false;
        String AddSen = mSentenceEditText.getText().toString();

        public worker_add_sen(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                Log.d(TAG, "worker add trans start");
                byte[] dataByte = AddSen.getBytes();
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_ASEN;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) dataByte.length;
                for (i = 4; i < 4+dataByte.length; i++) {
                    outData[i] = (byte) dataByte[i-4];
                }
                outData[6 + dataByte.length] = (byte) PacketUser.CRC;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, (dataByte.length)+5); // packet transmission
                    dos.flush();

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(temp, 0, 4);
                    for (int index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    if(inData[1] == PacketUser.ACK_ASEN) {
                        Log.d(TAG, "등록완료");
                    }
                    dis.read(temp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlay = !isPlay;
            }
        }
    }
}