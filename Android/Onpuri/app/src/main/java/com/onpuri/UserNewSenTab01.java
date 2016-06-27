package com.onpuri;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-05-11.
 */
public class UserNewSenTab01  extends Activity {
    TextView tvSen;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_note);

        tvSen = (TextView)findViewById(R.id.tv_sentence);

    }

    public void onBackPressed(){
        UserNewSen parent = ((UserNewSen)getParent());
        parent.onBackPressed();
    }

}
