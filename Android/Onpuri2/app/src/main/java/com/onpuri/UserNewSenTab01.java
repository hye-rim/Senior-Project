package com.onpuri;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by kutemsys on 2016-05-11.
 */
public class UserNewSenTab01  extends Activity {
   // TextView tvSen;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //tvSen = (TextView)findViewById(R.id.tv_sentence);

    }
/*
    public void onBackPressed(){
        UserNewSen parent = ((UserNewSen)getParent());
        parent.onBackPressed();
    }
*/
}
