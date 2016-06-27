package com.onpuri.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.onpuri.R;

/**
 * Created by kutemsys on 2016-05-11.
 */
public class UserMainSentence extends Activity {

    TextView tvSen;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        Intent intent = getIntent();
        String sen = (String)intent.getSerializableExtra("Sentence");

        tvSen = (TextView)findViewById(R.id.tv_sentence);
        tvSen.setText(sen);

/*
        final Button btn_listen = (Button)findViewById(R.id.btn_listen);
        btn_listen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(UserMainSentence.this, UserMainSenListen.class);

                intent.putExtra("Sentence", tvSen.getText());

            }
        });

        final Button btn_trans = (Button)findViewById(R.id.btn_trans);
        btn_trans.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(UserMainSentence.this, UserMainSenTrans.class);

                intent.putExtra("Sentence", tvSen.getText());

            }
        });
        */
    }
}
