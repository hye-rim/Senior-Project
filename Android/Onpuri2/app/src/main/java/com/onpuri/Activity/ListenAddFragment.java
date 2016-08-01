package com.onpuri.Activity;

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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;

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
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    private static View view;
    private Toast toast;

    TextView item;
    String sentence = "";

    static final String RECORDED_FILE = "/sdcard/DailyE_record/recorded.mp4";
    MediaRecorder recorder;
    boolean Islisten = true;
    boolean Isplay = true;

    Button btn_listen, btn_play;
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    String mFilePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_listen_add, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
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

        String sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath = sdRootPath + "/record.mp3";
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listen:
                if (Islisten) {
                    Islisten = false;
                    btn_listen.setText("중지하기");
                    onBtnStop();
                } else {
                    Islisten = true;
                    btn_listen.setText("녹음하기");
                    if (Build.VERSION.SDK_INT >= 23) {
                        perrmissionWork();
                    } else {
                        btnRecord();
                    }
                }

                break;
            case R.id.play:
                if (Isplay) {
                    Isplay = false;
                    btn_play.setText("정지하기");

                } else {
                    Isplay = true;
                    btn_play.setText("재생하기");
                    onBtnPlay();
                }
                break;
            case R.id.btn_new_listen:
                toast = Toast.makeText(getActivity(), "등록", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.btn_new_listen_back:
                toast = Toast.makeText(getActivity(), "취소", Toast.LENGTH_SHORT);
                toast.show();
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
    public void onBtnStop() {
        mRecorder.stop();
        mRecorder.release();

    }

    public void onBtnPlay() {
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