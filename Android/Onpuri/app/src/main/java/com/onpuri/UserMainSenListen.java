package com.onpuri;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by kutemsys on 2016-05-17.
 */
public class UserMainSenListen extends Activity {
    TextView tvSen;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sen_listen);

        Intent intent = getIntent();
        String sen = (String)intent.getSerializableExtra("Sentence");

        tvSen = (TextView)findViewById(R.id.tv_listen_sentence);
        tvSen.setText(sen);

    }

    public void onBackPressed(){
        UserMain parent = ((UserMain)getParent());
        parent.onBackPressed();
    }
}