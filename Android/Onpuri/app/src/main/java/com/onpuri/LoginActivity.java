package com.onpuri;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by Hye-rim on 2016-03-18.
 */
public class LoginActivity extends Activity{
    private String id, pass;
    private String admin = "test";  // admin IP/PW
    private CloseSystem CloseSystem; // BackKeyPressed,close

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    Button btLogin, btNew;
    EditText et_loginId, et_loginPw;

    int i, index;
    char check = '5';
    char checkLength;

    private worker_login mworker_login;

    private ActivityList actManager = ActivityList.getInstance();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_login);
        CloseSystem = new CloseSystem(this); //backKey Event

        btLogin = (Button) findViewById(R.id.btnLogin);
        btNew = (Button) findViewById(R.id.btnNew);

        et_loginId = (EditText) findViewById(R.id.et_loginId);
        et_loginId.setFilters(new InputFilter[]{filterAlphaNum}); //영문+숫자만 되도록 제한
        et_loginId.setPrivateImeOptions("defaultInputmode=english;"); //default 영문키패드로 설정
        et_loginId.setText("");

        et_loginPw = (EditText) findViewById(R.id.et_loginPw);
        et_loginPw.setFilters(new InputFilter[]{filterAlphaNum});
        et_loginPw.setPrivateImeOptions("defaultInputmode=english;");
        et_loginPw.setText("");

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = et_loginId.getText().toString();
                pass = et_loginPw.getText().toString();

                mworker_login = new worker_login(true);
                mworker_login.start();

                 try {
                   mworker_login.join();
                 } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //isPause = false;
                System.out.println("result: " + (char)inData[4]);
               // mworker_login.stopThread();
                if ( (check != '0' && check != '5') && checkLength != '1') {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else if( check == '5'){
                    Toast.makeText(getApplicationContext(), "ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "ID 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(LoginActivity.this, NewActivity.class);
                    startActivity(intent);

            }
        });
    }
    public void onBackPressed(){
        CloseSystem.onBackPressed();
    }
    //edittext 영문+숫자만 입력되도록 하는 함수
    public InputFilter filterAlphaNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
            Pattern ps = Pattern.compile("^[-_a-zA-Z0-9]+$");
            if(source.equals("")|| ps.matcher(source).matches()){
                source.equals(""); //백스페이스를 위해 추가한 부분
                return source;
            }

            return "";
        }
    };
   class worker_login extends Thread{
       private boolean isPlay = false;

        public worker_login(boolean isPlay){
            this.isPlay = isPlay;
        }

        public void stopThread(){
            isPlay = !isPlay;
        }
        public void run() {
           super.run();
            while (isPlay) {

                String toServerDataUser;
                toServerDataUser = et_loginId.getText().toString() + "+" + et_loginPw.getText().toString();
                System.out.println("data : " + toServerDataUser);
                outData[0] = (byte) PacketUser.SOF;
                outData[1] = (byte) PacketUser.USR_LOG;
                outData[2] = (byte) PacketUser.getSEQ();
                outData[3] = (byte) toServerDataUser.length();

                for (i = 4; i < 4 + toServerDataUser.length(); i++) {
                    outData[i] = (byte) toServerDataUser.charAt(i - 4);
                }

                outData[4 + toServerDataUser.length()] = (byte) 85;
                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData,0,outData[3]+5); // packet transmission
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

                    PacketUser.data_len = (int) inData[3];

                    if (inData[4] != '0') { //ID, PW가 틀렸을 경우 실행하지 않도록 한다.
                        index = 0;
                        while (true) { //아이디
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.userId = PacketUser.userId + (char) inData[4 + index];
                                index++;
                            }
                        }

                        while (true) { //shell
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.shell = "" + (char) inData[4 + index];
                                index++;
                            }
                        }

                        while (true) { //problem
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.problem = PacketUser.problem + (char) inData[4 + index];
                                index++;
                            }
                        }

                        while (true) { //average
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.average = PacketUser.average + (char) inData[4 + index];
                                index++;
                            }
                        }

                        while (true) { //ranking
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.ranking = PacketUser.ranking + (char) inData[4 + index];
                                index++;
                            }
                        }

                        while (true) { //question
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.question = PacketUser.question + (char) inData[4 + index];
                                index++;
                            }
                        }

                        while (true) { //solving
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.solving = PacketUser.solving + (char) inData[4 + index];
                                index++;
                            }
                        }
                        while (true) { //attend
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.attend = PacketUser.attend + (char) inData[4 + index];
                                index++;
                            }
                        }
                        while (true) { //purchase
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.purchase = PacketUser.purchase + (char) inData[4 + index];
                                index++;
                            }
                        }
                        while (true) { //sale
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.sale = PacketUser.sale + (char) inData[4 + index];
                                index++;
                            }
                        }
                        while (true) { //declaration
                            if ((char) (inData[4 + index]) == '+') {
                                index++;
                                break;
                            } else {
                                PacketUser.declaration = PacketUser.declaration + (char) inData[4 + index];
                                index++;
                            }
                        }
                    }
                        check = (char) inData[4];
                        checkLength = (char) inData[3];

                        if (check == '0' || checkLength != '1')
                            isPlay = !isPlay;

                    }catch(IOException e){
                        e.printStackTrace();
                    }


            }
        }
    }
    @Override
    protected void onStop(){
        super.onStop();

        int i = 0;
        inData = new byte[inData.length]; //초기화가 되지않아....
     //   SocketConnection.close();
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
