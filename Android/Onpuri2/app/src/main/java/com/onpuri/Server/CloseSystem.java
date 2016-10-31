package com.onpuri.Server;

/**
 * Created by kutemsys on 2016-04-01.
 */
import android.app.Activity;
import android.widget.Toast;

import com.onpuri.ActivityList;

public class CloseSystem {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    private ActivityList actManager = ActivityList.getInstance();

    public CloseSystem(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed() {
       if (System.currentTimeMillis() > backKeyPressedTime + 3000) { //
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 3000) {
            //actManager.finishAllActivity();

            activity .moveTaskToBack(true);
            activity .finish();
            toast.cancel();
        }
    }

}
