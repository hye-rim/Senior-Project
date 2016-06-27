package com.onpuri.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.onpuri.R;

/**
 * Created by kutemsys on 2016-05-17.
 */
public class UserMainSenTrans extends Activity {
    TextView tvSen;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sen_trans);

        Intent intent = getIntent();
        String sen = (String)intent.getSerializableExtra("Sentence");

        tvSen = (TextView)findViewById(R.id.tv_trans_sentence);
        tvSen.setText(sen);
    }
}
