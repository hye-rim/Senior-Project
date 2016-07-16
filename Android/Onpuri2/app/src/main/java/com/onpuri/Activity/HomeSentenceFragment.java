package com.onpuri.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;


/**
 * Created by kutemsys on 2016-05-11.
 */
public class HomeSentenceFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];
    DataOutputStream dos;
    DataInputStream dis;

    private static View view;
    private Toast toast;

    TextView item;
    String sentence="";
    String sentence_num="";
    TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_home_sen, container, false);
        } catch (InflateException e) {
        }

        item = (TextView) view.findViewById(R.id.tv_sentence);
        if (getArguments() != null) { //클릭한 문장 출력
            sentence = getArguments().getString("sen");
            sentence_num = getArguments().getString("sen_num");
            item.setText(sentence);
        }

        Button del_sen = (Button) view.findViewById(R.id.del_sen);
        del_sen.setOnClickListener(this);
        Button add_note = (Button) view.findViewById(R.id.add_note);
        add_note.setOnClickListener(this);
        Button add_trans = (Button) view.findViewById(R.id.add_trans);
        add_trans.setOnClickListener(this);

        TextView listen1 = (TextView) view.findViewById(R.id.listen1);
        TextView listen2 = (TextView) view.findViewById(R.id.listen2);
        TextView listen3 = (TextView) view.findViewById(R.id.listen3);
        listen1.setOnClickListener(this);
        listen2.setOnClickListener(this);
        listen3.setOnClickListener(this);

        tts = new TextToSpeech(getActivity(), this);

        return view;
    }

    @Override
    public void onDestroy() {
        if(tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listen1:
                tts.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.listen2:
                toast = Toast.makeText(getActivity(), "두번째 음성 파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.listen3:
                toast = Toast.makeText(getActivity(), "세번째 음성 파일이 존재하지 않습니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.del_sen:
                toast = Toast.makeText(getActivity(), "내가 등록한 문장만 삭제가능합니다", Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.add_note:
                final CharSequence[] items = {"노트1", "노트2", "노트3"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("노트를 선택해 주세요(노트 연동은 구현 예정)")
                        .setItems(items, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int index){
                                Toast.makeText(getActivity(), items[index]+"선택", Toast.LENGTH_SHORT).show();
                            }
                        })
                        /*
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                            }
                        })*/
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dlg, int sumthin) {
                            }
                        }).show();
                break;
            case R.id.add_trans:
                final AddTransFragment atf = new AddTransFragment();

                Bundle args = new Bundle();
                args.putString("sen",sentence);
                atf.setArguments(args);
                args.putString("sen_num",sentence_num);
                atf.setArguments(args);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.root_frame, atf);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }
    @Override
    public void onInit(int status) {
        tts.setLanguage(Locale.US);
    }

    /*
    public void test() {

        int i;
        String toServerDataUser;
        toServerDataUser = sentence_num;

        outData[0] = (byte) PacketUser.SOF;
        outData[1] = (byte) PacketUser.USR_SEN;
        outData[2] = (byte) PacketUser.getSEQ();
        outData[3] = (byte) PacketUser.USR_SEN_LEN;
        for (i = 4; i < 4 + PacketUser.USR_SEN_LEN; i++) {
            outData[i] = (byte) toServerDataUser.charAt(i - 4);
        }
        outData[4 + toServerDataUser.length()] = (byte) 85;

        try {
            dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
            dos.write(outData, 0, outData[3] + 5); // packet transmission
            dos.flush();
            dis = new DataInputStream(SocketConnection.socket.getInputStream());
            dis.read(inData);
            //System.out.println("Data form server: " + ((char)inData[0].) + (char)inData[1]);
            int SOF = inData[0];
            System.out.println(inData[0]);
            System.out.println(inData[1]);
            System.out.println(inData[2]);
            System.out.println(inData[3]);
            System.out.println((char) inData[4]);
            System.out.println(inData[5]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}