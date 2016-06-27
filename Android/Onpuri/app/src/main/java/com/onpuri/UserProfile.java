package com.onpuri;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by kutemsys on 2016-04-26.
 */
public class UserProfile extends ActivityGroup {
    public static UserProfile ProfileGroup;
    private ArrayList<View> history;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        history = new ArrayList<>();
        ProfileGroup = this;

        Intent intent =new Intent(UserProfile.this, UserProfileTab01.class );
        View view = getLocalActivityManager().startActivity("UserProfileTab01", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
        replaceView(view);
    }

    //새로운 level의 activity를 추가하는 경우
    public void replaceView(View view) {
        history.add(view);
        setContentView(view);
    }

    //back key가 눌러졌을 경우에 대한 처리
    public void back(){
        if(history.size() > 0) {
            history.remove(history.size() - 1);
            if (history.size() == 0)
                finish();
            else
                setContentView(history.get(history.size() - 1));

        }else{
            finish();
        }
    }

    //back key에 대한 event handler
    public void onBackPressed(){
        ProfileGroup.back();
        return;
    }

}
