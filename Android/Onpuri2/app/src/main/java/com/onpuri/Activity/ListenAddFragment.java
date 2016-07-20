package com.onpuri.Activity;

import android.media.MediaRecorder;
import android.os.Bundle;
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

/**
 * Created by kutemsys on 2016-07-16.
 */

public class ListenAddFragment extends Fragment implements View.OnClickListener {

    private static View view;
    private Toast toast;

    TextView item;
    String sentence = "";

    static final String RECORDED_FILE = "/DailyE_record/recorded.mp4";
    MediaRecorder recorder;
    boolean Islisten = true;
    boolean Isplay = true;

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

        Button btn_listen = (Button) view.findViewById(R.id.listen);
        btn_listen.setOnClickListener(this);
        Button btn_play = (Button) view.findViewById(R.id.play);
        btn_play.setOnClickListener(this);

        Button btn_new_listen = (Button) view.findViewById(R.id.btn_new_listen);
        btn_new_listen.setOnClickListener(this);
        Button btn_new_trans_back = (Button) view.findViewById(R.id.btn_new_listen_back);
        btn_new_trans_back.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listen:
                Button btn_listen = (Button) view.findViewById(R.id.listen);
                if (Islisten) {
                  /*  if (recorder != null) {
                        recorder.stop();
                        recorder.release();
                        recorder=null;
                    }
                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    recorder.setOutputFile(RECORDED_FILE);
                    try {
                        Toast.makeText(getActivity(),"녹음시작", Toast.LENGTH_LONG).show();
                        recorder.prepare();
                        recorder.start();
                    } catch (Exception e) {
                        Log.e("error","Exception : ", e);
                    }*/
                    Islisten = false;
                    btn_listen.setText("중지");
                } else {
                    /*recorder.stop();
                    recorder.release();
                    recorder = null;
                    */
                    Islisten = true;
                    btn_listen.setText("녹음");
                }
                break;
            case R.id.play:
                Button btn_play = (Button) view.findViewById(R.id.play);
                if (Isplay) {
                    Isplay = false;
                    btn_play.setText("정지");

                } else {
                    Isplay = true;
                    btn_play.setText("재생");

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
}