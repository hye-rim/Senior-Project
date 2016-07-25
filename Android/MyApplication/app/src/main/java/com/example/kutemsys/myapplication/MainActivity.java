package com.example.kutemsys.myapplication;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
        implements MediaRecorder.OnInfoListener {
    MediaPlayer mPlayer = null;
    MediaRecorder mRecorder = null;
    String mFilePath;
    Button mBtnRecord;
    Button mBtnStop;
    Button mBtnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnRecord = (Button) findViewById(R.id.btnRecord);
        mBtnStop = (Button) findViewById(R.id.btnStop);
        mBtnPlay = (Button) findViewById(R.id.btnPlay);

        String sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilePath = sdRootPath + "/record.mp3";
    }

    public void onBtnRecord() {
        if (mRecorder != null) {
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
        } catch (IOException e) {
            Log.d("tag", "Record Prepare error");
        }
        mRecorder.start();

        // 버튼 활성/비활성 설정
        mBtnRecord.setEnabled(false);
        mBtnStop.setEnabled(true);
        mBtnPlay.setEnabled(false);
    }

    public void onBtnStop() {
        mRecorder.stop();
        mRecorder.release();

        // 버튼 활성/비활성 설정
        mBtnRecord.setEnabled(true);
        mBtnStop.setEnabled(false);
        mBtnPlay.setEnabled(true);
    }

    public void onBtnPlay() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
        } catch (IOException e) {
            Log.d("tag", "Audio Play error");
            return;
        }
        mPlayer.start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRecord:
                onBtnRecord();
                break;
            case R.id.btnStop:
                onBtnStop();
                break;
            case R.id.btnPlay:
                onBtnPlay();
                break;
        }
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        switch (what) {
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
            case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                onBtnStop();
                break;
        }
    }

}