package com.onpuri.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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
 * Created by Hye-rim on 2016-03-18.
 */
//Login
public class LoginActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private String id, pass;
    private com.onpuri.Server.CloseSystem CloseSystem; // BackKeyPressed,close

    DataOutputStream dos;
    DataInputStream dis;

    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    Button btLogin, btNew;
    EditText et_loginId, et_loginPw;
    CheckBox checkAuto;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    int i, index;
    char check;
    char checkLength;
    boolean isLoginBtn;

    private worker_login mworker_login;

    private ActivityList actManager = ActivityList.getInstance();
    private boolean loginChecked;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_login);
        CloseSystem = new CloseSystem(this); //backKey Event
        check = '5';

        btLogin = (Button) findViewById(R.id.btnLogin);
        btNew = (Button) findViewById(R.id.btnNew);
        checkAuto = (CheckBox)findViewById(R.id.check_auto);

        et_loginId = (EditText) findViewById(R.id.et_loginId);
        et_loginId.setFilters(new InputFilter[]{filterAlphaNum}); //영문+숫자만 되도록 제한
        et_loginId.setPrivateImeOptions("defaultInputmode=english;"); //default 영문키패드로 설정
        et_loginId.setText("");

        et_loginPw = (EditText) findViewById(R.id.et_loginPw);
        et_loginPw.setFilters(new InputFilter[]{filterAlphaNum});
        et_loginPw.setPrivateImeOptions("defaultInputmode=english;");
        et_loginPw.setText("");

        isLoginBtn = false;

        setting = getSharedPreferences("setting",0);
        editor = setting.edit();
        String id, password;
        Boolean validation;
        // if autoLogin checked, get input
        if (setting.getBoolean("autoLogin", false)) {
            et_loginId.setText(setting.getString("ID", ""));
            et_loginPw.setText(setting.getString("PW", ""));
            checkAuto.setChecked(true);
            // goto mainActivity

            id = et_loginId.getText().toString();
            password = et_loginPw.getText().toString();
            validation = loginCorrect(id, password);

            mainGo();
        } else {
            // if autoLogin unChecked
            id = et_loginId.getText().toString();
            password = et_loginPw.getText().toString();
            validation = loginCorrect(id, password);

            if(validation) {
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_LONG).show();
                // save id, password to Database

                if(loginChecked) {
                    // if autoLogin Checked, save values
                    editor.putString("id", id);
                    editor.putString("pw", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();
                }
                // goto mainActivity

                mainGo();

            } else {
                Log.d(TAG, "Login failed");
                // goto LoginActivity
            }
        }

        btLogin.setOnClickListener(this);
        btNew.setOnClickListener(this);

        checkAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    loginChecked = true;
                } else {
                    // if unChecked, removeAll
                    loginChecked = false;
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }

    private void mainGo(){
        if(mworker_login != null && mworker_login.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_login.interrupt();
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userId", id);

        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        Intent intent;

        if(checkAuto.isChecked()){
            Log.d(TAG, "로그인");
            String id = et_loginId.getText().toString();
            String password = et_loginPw.getText().toString();

            editor.putString("ID", id);
            editor.putString("PW", password);
            editor.putBoolean("autoLogin", true);
            editor.commit();

            mainGo();

        }else{
            editor.clear();
            editor.commit();

            switch (v.getId()) {
                case R.id.btnNew :
                    intent = new Intent(LoginActivity.this, JoinActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btnLogin:
                    id = et_loginId.getText().toString();
                    pass = et_loginPw.getText().toString();
                    Boolean validation = loginCorrect(id,pass);

                    isLoginBtn = true;

                    if(validation)
                        mainGo();

                    break;
            }
        }
    }

    private Boolean loginCorrect(String id, String password) {
        if(mworker_login != null && mworker_login.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_login.interrupt();
        }

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
            return true;
        }
        else if( check == '5' || et_loginId.getText().equals(null)){
            Toast.makeText(getApplicationContext(), "ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (setting.getString("id","").equals(null)) {
            // sign in first
            Toast.makeText(getApplicationContext(), "가입을 먼저 해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if( check != '5' && isLoginBtn){
            Toast.makeText(getApplicationContext(), "ID 또는 비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
            return false;

        }else{
            return false;
        }
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
