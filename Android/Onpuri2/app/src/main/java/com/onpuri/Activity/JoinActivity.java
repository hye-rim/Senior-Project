package com.onpuri.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.onpuri.R;
import com.onpuri.Server.ActivityList;
import com.onpuri.Server.CloseSystem;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by kutemsys on 2016-03-25.
 */
//Join Activity
public class JoinActivity extends Activity {
    Button btCheck, btJoin;
    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    private worker_check mworker_check;

    int i;
    char check = '5';
    int checkID = 0;
    boolean checkPw = false;

    EditText et_newId, et_newPw, et_comparePw;
    EditText et_newName, et_newPhone1, et_newPhone2, et_newPhone3;
    TextView tv_comparePw, tv_compareId;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    private com.onpuri.Server.CloseSystem CloseSystem; //BackKeyPressed,close
    private ActivityList actManager = ActivityList.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_new);
        CloseSystem = new CloseSystem(this); //backKey Event

        btCheck = (Button) findViewById(R.id.btnCheck);
        btJoin = (Button) findViewById(R.id.btnJoin);

        et_newId = (EditText) findViewById(R.id.et_newId);
        et_newId.setFilters(new InputFilter[]{filterAlphaNum});
        et_newId.setPrivateImeOptions("defaultInputmode=english;");
        tv_compareId = (TextView)findViewById(R.id.tv_compareId);

        et_newPw = (EditText) findViewById(R.id.et_newPw);
        et_newPw.setFilters(new InputFilter[]{filterAlphaNum});
        et_newPw.setPrivateImeOptions("defaultInputmode=english;");

        et_newName = (EditText) findViewById(R.id.et_newName);
        et_newPhone1 = (EditText) findViewById(R.id.et_newPhone1);
        et_newPhone2 = (EditText) findViewById(R.id.et_newPhone2);
        et_newPhone3 = (EditText) findViewById(R.id.et_newPhone3);
        et_comparePw = (EditText) findViewById(R.id.et_comparePw);
        tv_comparePw = (TextView) findViewById(R.id.tv_comparePw);


        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mworker_check = new worker_check(true);
                mworker_check.start();
                //}

                System.out.println("abc");
                try {
                    mworker_check.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("abc2");
                //중복확인
                if (check == '0') {
                    System.out.println("qq" + check + "\n");
                    checkID = 2;
                    tv_compareId.setText("사용 불가능한 아이디입니다");
                    et_newId.setText(null);
                } else if (check == '1') {
                    checkID = 1;
                    tv_compareId.setText("사용 가능한 아이디입니다");
                }
                System.out.println("stop2");
                mworker_check.interrupt();
            }
        });

        et_comparePw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_comparePw.getText().toString().equals(et_newPw.getText().toString()) && !et_newPw.getText().toString().equals("")) {
                    tv_comparePw.setText("비밀번호가 일치합니다");
                    checkPw = true;
                } else {
                    tv_comparePw.setText("비밀번호가 불일치합니다");
                    checkPw = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_newPw.getText().toString().equals(et_comparePw.getText().toString()) && !et_newPw.getText().toString().equals("")) {
                    tv_comparePw.setText("비밀번호가 일치합니다");
                    checkPw = true;
                } else {
                    tv_comparePw.setText("비밀번호가 불일치합니다");
                    checkPw = false;
                }
            }
        });


        //포커스 이동
        et_newPhone1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_newPhone1.length() == 3) {  // edit1  값의 제한값을 3이라고 가정했을때
                    et_newPhone2.requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_newPhone2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_newPhone2.length() == 4) {  // edit1  값의 제한값을 4이라고 가정했을때
                    et_newPhone3.requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //등록버튼 클릭 이벤트
        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkID == 1 && checkPw) {
                    if(worker_join.getState() == Thread.State.NEW)
                        worker_join.start();
                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    worker_join.interrupt();
                }
                else if(checkID == 2){
                    Toast.makeText(getApplicationContext(), "중복된 ID입니다", Toast.LENGTH_SHORT).show();
                }
                else if(!checkPw){
                    Toast.makeText(getApplicationContext(), "비밀번호가 다릅니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "ID 중복체크를 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onBackPressed(){
        super.onBackPressed();
    }


    class worker_check extends Thread {
        private boolean isPlay = false;

        public worker_check (boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread () {
            isPlay = !isPlay;
        }

        public void run () {
            super.run ();
            while (isPlay) {

                String toServerDataUser;
                toServerDataUser = et_newId.getText ().toString ();
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_CHK;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) toServerDataUser.length ();
                for (i = 4; i < 4 + toServerDataUser.length (); i++) {
                    outData[i] = (byte) toServerDataUser.charAt (i - 4);
                }
                outData[4 + toServerDataUser.length ()] = (byte) 85;


                try {
                    dos = new DataOutputStream (SocketConnection.socket.getOutputStream ());
                    dos.write (outData,0,outData[3]+5); // packet transmission
                    dos.flush();
                    System.out.println("abc4");
                    dis = new DataInputStream (SocketConnection.socket.getInputStream ());
                    dis.read (inData);
                    //System.out.println("Data form server: " + ((char)inData[0].) + (char)inData[1]);
                    int SOF = inData[0];
                    System.out.println("abc5");
                    System.out.println (inData[0]);
                    System.out.println (inData[1]);
                    System.out.println (inData[2]);
                    System.out.println (inData[3]);
                    System.out.println ((char) inData[4]);
                    System.out.println (inData[5]);
                    check = (char) inData[4];
                    System.out.println("cqq"+(char)inData[4]+"\n");
                    System.out.println("cqq"+check+"\n");
                    if( check == '0' || check == '1')
                        isPlay = !isPlay;

                } catch (IOException e) {
                    e.printStackTrace ();
                }

            }
        }

    }

    Thread worker_join = new Thread() {
        public void run() {
                       String toServerDataUser;
            toServerDataUser = et_newId.getText().toString() + "+" + et_newPw.getText().toString() + "+" + et_newName.getText().toString()
                    + "+" + et_newPhone1.getText().toString() + et_newPhone2.getText().toString() + et_newPhone3.getText().toString() ;
            outData[0] = (byte) PacketUser.SOF;
            outData[1] = (byte) PacketUser.USR_REG;
            outData[2] = (byte) PacketUser.getSEQ();
            outData[3] = (byte) toServerDataUser.length();
            for (i = 4; i < 4 + toServerDataUser.length(); i++) {
                outData[i] = (byte) toServerDataUser.charAt(i - 4);
            }
            outData[4 + toServerDataUser.length()] = (byte) 85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write (outData,0,outData[3]+5); // packet transmission

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

        }
    };

    //edittext 영문+숫자만 입력되도록 하는 함수
    public InputFilter filterAlphaNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
            Pattern ps = Pattern.compile("^[-_a-zA-Z0-9]+$");
            if(source.equals("")|| ps.matcher(source).matches()){
                source.equals(""); //백스페이스를 위해 추가한 부분
                return source;
            }

            //Toast.makeText(getActivity(), "영문, 숫자, _, - 만 입력 가능합니다.", Toast.LENGTH_SHORT).show();

            return "";
        }
    };


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "New Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.onpuri/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "New Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.onpuri/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
