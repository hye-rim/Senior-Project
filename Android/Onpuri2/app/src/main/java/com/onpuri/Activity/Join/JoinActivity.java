package com.onpuri.Activity.Join;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.onpuri.Activity.Login.LoginActivity;
import com.onpuri.ActivityList;
import com.onpuri.R;
import com.onpuri.Activity.Join.workerIdCheck;
import com.onpuri.Activity.Join.workerJoin;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.util.regex.Pattern;

/**
 * Created by kutemsys on 2016-03-25.
 */
//Join Activity
public class JoinActivity extends Activity {
    private static final String TAG = "JoinActivity";
    private final long FINISH_INTERVAL_TIME = 3000;
    private long backPressedTime = 0;

    private workerIdCheck worker_check;
    private workerJoin worker_join;

    int i;
    char check = '5';
    int checkID = 0;
    boolean checkPw = false;

    EditText et_newId, et_newPw, et_comparePw;
    EditText et_newName, et_newPhone1, et_newPhone2, et_newPhone3;
    TextView tv_comparePw, tv_compareId;
    Button btCheck, btJoin, btCancel;

    private GoogleApiClient client;

    private ActivityList actManager = ActivityList.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_join);

        et_newId = (EditText) findViewById(R.id.et_newId);
        tv_compareId = (TextView)findViewById(R.id.tv_compareId);
        et_newPw = (EditText) findViewById(R.id.et_newPw);
        et_newName = (EditText) findViewById(R.id.et_newName);
        et_newPhone1 = (EditText) findViewById(R.id.et_newPhone1);
        et_newPhone2 = (EditText) findViewById(R.id.et_newPhone2);
        et_newPhone3 = (EditText) findViewById(R.id.et_newPhone3);
        et_comparePw = (EditText) findViewById(R.id.et_comparePw);
        tv_comparePw = (TextView) findViewById(R.id.tv_comparePw);

        btCheck = (Button) findViewById(R.id.btnCheck);
        btJoin = (Button) findViewById(R.id.btnJoin);
        btCancel = (Button) findViewById(R.id.btnJoinCancel);

        et_newName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        et_newId.setFilters(new InputFilter[]{filterAlphaNum, new InputFilter.LengthFilter(10)});
        et_newId.setPrivateImeOptions("defaultInputmode=english;");
        et_newPw.setFilters(new InputFilter[]{filterAlphaNum, new InputFilter.LengthFilter(15)});
        et_newPw.setPrivateImeOptions("defaultInputmode=english;");

        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNull =  et_newId.getText ().toString ().equals(null);

                if(!isNull) {
                    worker_check = new workerIdCheck(true, et_newId.getText().toString());
                    worker_check.start();

                    try {
                        worker_check.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    check = worker_check.getCheck();

                    //중복확인
                    if (check == '0') {
                        Log.d(TAG, "check : " + check);
                        checkID = 2;
                        tv_compareId.setText("사용 불가능한 아이디입니다");
                        et_newId.setText(null);
                    } else if (check == '1') {
                        checkID = 1;
                        tv_compareId.setText("사용 가능한 아이디입니다");
                    }

                    if (worker_check != null && worker_check.isAlive()) {  //이미 동작하고 있을 경우 중지
                        worker_check.interrupt();
                    }
                }else{
                    tv_compareId.setText("사용 불가능한 아이디입니다");
                }
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

        et_newName.setNextFocusDownId(R.id.et_newPhone1);
        //포커스 이동
        et_newPhone1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_newPhone1.length() == 3 && et_newPhone1.getText() != null) {  // edit1  값의 제한값을 3이라고 가정했을때
                    et_newPhone2.requestFocus(); // 두번째EditText 로 포커스가 넘어가게 됩니다
                }
                else
                    et_newPhone1.requestFocus();
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
                    Toast.makeText(getApplicationContext(), "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    String joinData = et_newId.getText().toString()
                            + "+" + et_newPw.getText().toString()
                            + "+" + et_newName.getText().toString()
                            + "+" + et_newPhone1.getText().toString()
                            + "-" + et_newPhone2.getText().toString()
                            + "-" + et_newPhone3.getText().toString() ;

                    worker_join = new workerJoin(true, joinData);
                    worker_join.start();

                    try {
                        worker_join.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(worker_join != null && worker_join.isAlive()){  //이미 동작하고 있을 경우 중지
                        worker_join.interrupt();
                    }

                    Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

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

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onBackPressed(){
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime){
            super.onBackPressed();
        }
        else{
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "\'뒤로\' 버튼을 한번 더 누르시면 \n로그인화면으로 이동합니다." , Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
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
