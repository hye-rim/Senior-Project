package com.onpuri.Activity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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

/**
 * Created by kutemsys on 2016-07-16.
 */

public class ListenAddFragment extends Fragment implements View.OnClickListener, MediaRecorder.OnInfoListener {
    private static final String TAG = "ListenAddFragment";

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
                Button btn_listen = (Button) view.findViewById(R.id.listen);
                /*
                if (Islisten) {
                    if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder=null;
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(RECORDED_FILE);
                    try {
                        Toast.makeText(getActivity(),"녹음시작", Toast.LENGTH_LONG).show();
                        recorder.prepare();
                        recorder.start();
                        System.out.println("2");
                    } catch (Exception e) {
                        Log.e("error","Exception : ", e);
                    }
                    Islisten = false;
                    btn_listen.setText("중지");
                } else {
                    recorder.stop();
                    recorder.release();
                    recorder = null;

                    Islisten = true;
                    btn_listen.setText("녹음");
                }
                */
                if (Islisten) {
                    Islisten = false;
                    btn_listen.setText("중지하기");
                    onBtnStop();
                } else {
                    Islisten = true;
                    btn_listen.setText("녹음하기");
                    btnRecord();
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
}