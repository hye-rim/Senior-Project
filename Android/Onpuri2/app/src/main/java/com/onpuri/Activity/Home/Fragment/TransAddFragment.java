package com.onpuri.Activity.Home.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

import com.onpuri.DividerItemDecoration;
import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static com.onpuri.R.drawable.divider_dark;

/**
 * Created by kutemsys on 2016-07-16.
 */
public class TransAddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransAddFragment";
    private WorkerTransAdd worker_add_trans;

    private static View view;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    String sentence="";
    int sentence_num;

    TextView item;
    EditText trans;

    int i, checkLen;

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

            item.setTextIsSelectable(true);
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


        switch (v.getId()) {
            case R.id.btn_new_trans:
            new AlertDialog.Builder(getActivity())
                    .setTitle("해석을 등록하시겠습니까?\n등록후에는 수정이 불가능합니다.")
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
                            senMsg();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                        }
                    }).show();
            break;
            case R.id.btn_new_trans_back:
                new AlertDialog.Builder(getActivity())
                        .setTitle("해석 등록을 취소하시겠습니까?\n작성중인 내용이 전부 사라집니다.")
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
                                final FragmentManager fm = getActivity().getSupportFragmentManager();
                                final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                fm.popBackStack();
                                ft.commit();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;
        }
    }

    private void senMsg() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        switch (checkLen){
            case 0: //ok
                Addtranslation();
                fm.popBackStack();
                ft.commit();
                Toast.makeText(getActivity(), "등록되었습니다.", Toast.LENGTH_SHORT).show();
                break;

            case 1: //null
                Toast.makeText(getActivity(), "해석을 입력해주세요.", Toast.LENGTH_SHORT).show();
                break;

            case 2: //max
                Toast.makeText(getActivity(), "최대 글자 수(125자)를 초과하셨습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void checkingLen() {
        String inputTrans = trans.getText().toString();

        if(inputTrans.compareTo("") == 0 || trans.getText().toString().isEmpty()){
            checkLen = 1; //null
        }else if( inputTrans.getBytes().length >= 250){
            checkLen = 2;//max
        }else{
            checkLen = 0; //OK
        }
    }

    private void Addtranslation() {
        if(worker_add_trans != null && worker_add_trans.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_add_trans.interrupt();
        }
        worker_add_trans = new WorkerTransAdd(true);
        worker_add_trans.start();
        try {
            worker_add_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    class WorkerTransAdd extends Thread {

        private boolean isPlay = false;
        String addTrans = trans.getText().toString();

        public WorkerTransAdd(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                Log.d(TAG, "worker add trans start");
                byte[] dataByte = addTrans.getBytes();
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_ATRANS;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) dataByte.length;

                for (i = 4; i < 4 + dataByte.length; i++) {
                    outData[i] = (byte) dataByte[i - 4];
                }

                outData[4 + dataByte.length] = (byte) (sentence_num / 255 + 1);
                outData[5 + dataByte.length] = (byte) (sentence_num % 255 + 1);
                outData[6 + dataByte.length] = (byte) PacketUser.CRC;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, (dataByte.length)+7); // packet transmission
                    dos.flush();

                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(temp, 0, 4);
                    for (int index = 0; index < 4; index++) {
                        inData[index] = temp[index];    // SOF // OPC// SEQ// LEN 까지만 읽어온다.
                    }
                    if (inData[1] == PacketUser.ACK_ATRANS) {
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