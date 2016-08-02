package com.onpuri.Activity;

import android.graphics.Color;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by kutemsys on 2016-07-16.
 */

public class ListenAddFragment extends Fragment implements View.OnClickListener, MediaRecorder.OnInfoListener {
    private static final String TAG = "ListenAddFragment";
    private worker_add_listen worker_add_listen;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    DataOutputStream dos;
    DataInputStream dis;
    FileInputStream fis;
    BufferedInputStream bis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];
    private static View view;
    private Toast toast;

    TextView item;
    String sentence = "";
    int sentence_num;

    boolean Islisten = false;
    boolean Isplay = false;
    boolean Isstart = false;

    Button btn_listen, btn_play;
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    String mFilePath = "";
    private static String mFileName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_listen_add, container, false);
        } catch (InflateException e) {}

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) {
            sentence = getArguments().getString("sen");
            sentence_num=Integer.parseInt(getArguments().getString("sen_num"));
            item.setText(sentence);
        }

        btn_listen = (Button) view.findViewById(R.id.listen);
        btn_listen.setOnClickListener(this);
        btn_play = (Button) view.findViewById(R.id.play);
        btn_play.setOnClickListener(this);

        Button btn_new_listen = (Button) view.findViewById(R.id.btn_new_listen);
        btn_new_listen.setOnClickListener(this);
        Button btn_new_trans_back = (Button) view.findViewById(R.id.btn_new_listen_back);
        btn_new_trans_back.setOnClickListener(this);

        mFilePath = GetFilePath();
        return view;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.listen:
                Isstart = true;

                if (!Islisten) {
                    Islisten = true;
                    btn_listen.setText("중지");
                    btn_play.setEnabled(false);
                    btn_play.setTextColor(Color.parseColor("#FEE098"));
                    if(Build.VERSION.SDK_INT >= 23) {
                        perrmissionWork();
                    } else {
                        btnRecord();
                    }
                }
                else {
                    Islisten = false;
                    btn_listen.setText("녹음");
                    btn_play.setEnabled(true);
                    btn_play.setTextColor(Color.parseColor("#000000"));
                    onBtnStop();
                }
                break;

            case R.id.play:
                if(!Isstart) {
                    toast = Toast.makeText(getActivity(), "진행된 녹음이 없습니다", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                if (!Isplay) {
                    Isplay = true;
                    btn_play.setText("정지");
                    btn_listen.setEnabled(false);
                    btn_listen.setTextColor(Color.parseColor("#FEE098"));
                    onBtnPlay();
                }
                 else {
                    Isplay = false;
                    btn_play.setText("재생");
                    btn_listen.setEnabled(true);
                    btn_listen.setTextColor(Color.parseColor("#000000"));
                }
                break;

            case R.id.btn_new_listen:
                //AddListen();
                toast = Toast.makeText(getActivity(), "등록되었습니다(구현예정)", Toast.LENGTH_SHORT);
                toast.show();
                fm.popBackStack();
                ft.commit();
                break;

            case R.id.btn_new_listen_back:
                fm.popBackStack();
                ft.commit();
                break;
        }
    }

    private void btnRecord() {
        if( mRecorder != null ) {
            mRecorder.release();
            mRecorder = null;
        }
        mRecorder = new MediaRecorder();

        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        mRecorder.setMaxDuration(5 * 1000);
        mRecorder.setMaxFileSize(5 * 1000 * 1000);
        mRecorder.setOnInfoListener(this);

        try {
            mRecorder.prepare();
        } catch(IOException e) {
            Log.d("tag", "Record Prepare error");
        }
        mRecorder.start();
    }
    private void onBtnStop() {
        mRecorder.stop();
        mRecorder.release();
    }

    private void onBtnPlay() {
        if( mPlayer != null ) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
        } catch(IOException e) {
            Log.d(TAG, "Audio Play error");
            return;
        }
        mPlayer.start();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        switch( what ) {
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED :
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED :
                onBtnStop();
                break;
        }
    }

    //파일 경로
    public static synchronized String GetFilePath() {
        String sdcard = Environment.getExternalStorageState();
        File file = null;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED)) {
            file = Environment.getRootDirectory();
        }
        else {
            file = Environment.getExternalStorageDirectory();
        }
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat( "MMdd_HHmmss", Locale.KOREA );
        Date currentTime = new Date ( );
        String mTime = mSimpleDateFormat.format ( currentTime );
        mFileName = "record "+mTime+".mp3";

        String dir = file.getAbsolutePath() + String.format("/Daily E");
        String path = file.getAbsolutePath() + String.format("/Daily E/%s", mFileName);

        file = new File(dir);
        if ( !file.exists() )
        {
            file.mkdirs();
        }
        return path;
    }

    //마시멜로 권한설정
    private void perrmissionWork() {
        String permissionsNeeded = new String();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded = new String("RECORD_AUDIO");

        if (permissionsList.size() > 0) {
            if ( !permissionsNeeded.isEmpty() ) { //값이 있을 경우
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded;
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                    }
                });
                return;
            }
            requestPermissions(
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }else{
            btnRecord();
        }
    }

    private void AddListen() {
        if(worker_add_listen != null && worker_add_listen.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_add_listen.interrupt();
        }
        worker_add_listen = new worker_add_listen(true);
        worker_add_listen.start();
        try {
            worker_add_listen.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    class worker_add_listen extends Thread {
        private boolean isPlay = false;
        int control=0;

        public worker_add_listen(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                Log.d(TAG, "worker add listen start");
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_ALISTEN;
                outData[2] = (byte) PacketUser.getSEQ();

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());

                    File f = new File(mFileName);
                    fis = new FileInputStream(f);
                    bis = new BufferedInputStream(fis);
                    int size = 4096;
                    byte[] dataByte = new byte[size];

                    outData[3] = (byte) dataByte.length;
                    while((bis.read(dataByte)) != -1) {
                        control++;
                        if(control % 100 == 0) {
                            System.out.println("전송중" + control/100);
                            for (int i = 4; i < 4+dataByte.length; i++) {
                                outData[i] = (byte) dataByte[i-4];
                                Log.d(TAG, new String(outData));
                            }
                        }
                        outData[4 + dataByte.length] = (byte) (sentence_num/255 +1) ;
                        outData[5 + dataByte.length] = (byte) (sentence_num%255 +1) ;
                        outData[6 + dataByte.length] = (byte) PacketUser.CRC;

                    }
                    dos.write(outData, 0, outData[3]+7);
                    dos.flush();
                    bis.close();
                    fis.close();

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(temp, 0, 4);
                    for (int index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    if(inData[1] == PacketUser.ACK_ALISTEN) {
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

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList,String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message,android.content.DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(getActivity()).setMessage(message)
                .setPositiveButton("OK", onClickListener).setCancelable(false)
                .setNegativeButton("Cancel", null).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED ) {
                    // All Permissions Granted
                    btnRecord();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}