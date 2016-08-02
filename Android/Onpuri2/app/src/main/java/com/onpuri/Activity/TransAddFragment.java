package com.onpuri.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by kutemsys on 2016-07-16.
 */
public class TransAddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransAddFragment";
    private worker_add_trans worker_add_trans;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    private static View view;
    private Toast toast;

    String sentence="";
    int sentence_num;
    TextView item;
    EditText trans;

    int i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_trans_add, container, false);
        } catch (InflateException e) {}

        trans = (EditText) view.findViewById(R.id.new_trans);
        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num=Integer.parseInt(getArguments().getString("sen_num"));
            item.setText(sentence);
        }

        Button btn_new_trans = (Button) view.findViewById(R.id.btn_new_trans);
        btn_new_trans.setOnClickListener(this);
        Button btn_new_trans_back = (Button) view.findViewById(R.id.btn_new_trans_back);
        btn_new_trans_back.setOnClickListener(this);

        return view;
    }
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
    @Override
    public void onClick(View v) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.btn_new_trans:
                Addtranslation();
                toast = Toast.makeText(getActivity(), "등록되었습니다", Toast.LENGTH_SHORT);
                toast.show();
                fm.popBackStack();
                ft.commit();
                break;
            case R.id.btn_new_trans_back:
                fm.popBackStack();
                ft.commit();
                break;
        }
    }
    private void Addtranslation() {
        if(worker_add_trans != null && worker_add_trans.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_add_trans.interrupt();
        }
        worker_add_trans = new worker_add_trans(true);
        worker_add_trans.start();
        try {
            worker_add_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    class worker_add_trans extends Thread {
        private boolean isPlay = false;
        String AddTrans = trans.getText().toString();

        public worker_add_trans(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                Log.d(TAG, "worker add trans start");
                byte[] dataByte = AddTrans.getBytes();
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_ATRANS;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) dataByte.length;
                for (i = 4; i < 4+dataByte.length; i++) {
                    outData[i] = (byte) dataByte[i-4];
                    Log.d(TAG, new String(outData));
                    Log.d(TAG, new String(dataByte));
                }
                outData[4 + dataByte.length] = (byte) (sentence_num/255 +1) ;
                outData[5 + dataByte.length] = (byte) (sentence_num%255 +1) ;
                outData[6 + dataByte.length] = (byte) PacketUser.CRC;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3]+7); // packet transmission
                    dos.flush();

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(temp, 0, 4);
                    for (int index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    if(inData[1] == PacketUser.ACK_ATRANS) {
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
