package com.onpuri;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by kutemsys on 2016-04-26.
 */
public class UserProfileTab02 extends Activity {
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_2);

    }

    public void onBackPressed(){
        UserProfile parent = ((UserProfile)getParent());
        parent.onBackPressed();
    }

}
