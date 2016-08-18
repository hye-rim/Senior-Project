package com.onpuri.Activity;

import android.app.Activity;
import android.content.Context;
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
import com.onpuri.ActivityList;
import com.onpuri.Server.CloseSystem;
import com.onpuri.Server.PacketUser;
import com.onpuri.Server.SocketConnection;
import com.onpuri.Thread.workerLogin;
import com.tsengvn.typekit.TypekitContextWrapper;

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

    Button btLogin, btNew;
    EditText et_loginId, et_loginPw;
    CheckBox checkAuto;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    PacketUser mPacketUser;

    int i;
    char check;
    char checkLength;
    boolean isLoginBtn;

    private workerLogin mworker_login;

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
                    editor.putString("ID", id);
                    editor.putString("PW", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();
                }
                else{
                    loginChecked = false;
                    editor.putBoolean("autoLogin", false);
                    editor.clear();
                    editor.commit();
                }
                // goto mainActivity

                mainGo();

            } else {
                editor.clear();
                editor.commit();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
    private void mainGo(){
        mPacketUser = mworker_login.getmPacketUser();

        if(mworker_login != null && mworker_login.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_login.interrupt();
        }

        Log.d(TAG,"M id : " + mPacketUser.userId);
        Log.d(TAG,"M name : " + mPacketUser.name);
        Log.d(TAG,"M joinDate : " + mPacketUser.joinDate);
        Log.d(TAG,"M phone : " + mPacketUser.phone);
        Log.d(TAG,"M nowPass : " + mPacketUser.nowPass);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("UserId", mPacketUser.userId);
        intent.putExtra("Name", mPacketUser.name);
        intent.putExtra("JoinDate", mPacketUser.joinDate);
        intent.putExtra("Phone",mPacketUser.phone);
        intent.putExtra("NowPass", mPacketUser.nowPass);

        startActivity(intent);
        finish();
    }
    @Override
    public void onClick(View v) {
        Intent intent;


        switch (v.getId()) {
            case R.id.btnNew :
                intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLogin:
                if(loginChecked){
                    Log.d(TAG, "로그인");
                    String id = et_loginId.getText().toString();
                    String password = et_loginPw.getText().toString();

                    editor.putString("ID", id);
                    editor.putString("PW", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();

                }else {
                    editor.clear();
                    editor.commit();
                }

                id = et_loginId.getText().toString();
                pass = et_loginPw.getText().toString();
                Boolean validation = loginCorrect(id,pass);

                isLoginBtn = true;

                if(validation)
                    mainGo();

                break;

        }
    }

    private Boolean loginCorrect(String id, String password) {
        if(mworker_login != null && mworker_login.isAlive()){  //이미 동작하고 있을 경우 중지
            mworker_login.interrupt();
        }

        String toServerDataUser = et_loginId.getText().toString() + "+" + et_loginPw.getText().toString();
        mworker_login = new workerLogin(true, toServerDataUser, check, checkLength);
        mworker_login.start();

        try {
            mworker_login.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        check = mworker_login.getCheck();
        checkLength = mworker_login.getCheckLength();

        if ( (check != '0' && check != '5') && checkLength != '1') {
            return true;
        }
        else if( check == '5' || et_loginId.getText().equals(null)){
            Toast.makeText(getApplicationContext(), "ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (setting.getString("id","").equals(null)) {
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

    //EditText 영문+숫자만 입력되도록 하는 함수
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

    @Override
    protected void onStop(){
        super.onStop();
        int i = 0;
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }
}