package com.onpuri;

import android.app.Application;
import com.tsengvn.typekit.Typekit;
/**
 * Created by kutemsys on 2016-08-03.
 */
public class CustomFont extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothicBold.ttf"))
                .addCustom1(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"));
    }
}
