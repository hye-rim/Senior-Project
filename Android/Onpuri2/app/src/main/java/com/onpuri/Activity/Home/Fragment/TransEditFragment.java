package com.onpuri.Activity.Home.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
 * Created by kutemsys on 2016-07-21.
 */

public class TransEditFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransEditFragment";
    private WorkerTransEdit worker_edit_trans;

    private static View view;
    private Toast toast;

    DataOutputStream dos;
    DataInputStream dis;
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    byte[] temp = new byte[261];

    String sentence;
    String trans;
    String sentence_num;

    TextView item_sen;
    EditText edittrans;

    int i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_trans_edit, container, false);
        } catch (InflateException e) {
        }

        edittrans = (EditText)view.findViewById(R.id.new_trans);
        item_sen = (TextView) view.findViewById(R.id.tv_sentence);

        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            item_sen.setText(sentence);
            sentence_num = getArguments().getString("sen_num");
            trans = getArguments().getString("sen_trans");
            edittrans.setText(trans);

            item_sen.setTextIsSelectable(true);

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
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        switch (v.getId()) {
            case R.id.btn_new_trans:
            new AlertDialog.Builder(getActivity())
                    .setTitle("편집한 해석을 등록하시겠습니까?\n등록후에는 수정이 불가능합니다.")
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
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            EditTranslation();
                            fm.popBackStack();
                            ft.commit();
                            Toast.makeText(getActivity(), "등록되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dlg, int sumthin) {
                            Toast.makeText(getActivity(), "취소되었습니다", Toast.LENGTH_SHORT).show();
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
    private void EditTranslation() {
        if(worker_edit_trans != null && worker_edit_trans.isAlive()){  //이미 동작하고 있을 경우 중지
            worker_edit_trans.interrupt();
        }
        worker_edit_trans = new WorkerTransEdit(true);
        worker_edit_trans.start();
        try {
            worker_edit_trans.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    class WorkerTransEdit extends Thread {

        private boolean isPlay = false;
        String addTrans = edittrans.getText().toString();
        int sNum = Integer.parseInt(sentence_num);

        public WorkerTransEdit(boolean isPlay) {
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

                outData[4 + dataByte.length] = (byte) (sNum / 255 + 1);
                outData[5 + dataByte.length] = (byte) (sNum % 255 + 1);
                outData[6 + dataByte.length] = (byte) PacketUser.CRC;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3] + 7); // packet transmission
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
