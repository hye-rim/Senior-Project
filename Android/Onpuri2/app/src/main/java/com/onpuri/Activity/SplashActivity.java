package com.onpuri.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.R;
import com.onpuri.Server.ActivityList;
import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.SocketConnection;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Hye-rim on 2016-03-18.
 */
//Loading Activity
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private ProgressBar spinner;
    DataOutputStream dos;
    DataInputStream dis;

    TextView tv_splash, tvVersion;
    String load = ".";
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    private splashThread msplashTread;

    private ActivityList actManager = ActivityList.getInstance();

    int i;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_splash);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(ProgressBar.VISIBLE);
        spinner.setIndeterminate(true);
        spinner.setMax(100);

        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        String appVersion = pi.versionName;

        tvVersion = (TextView)findViewById(R.id.tv_version);
        tvVersion.setText("Ver." + appVersion);


        //if(worker.getState() == Thread.State.NEW)
        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tv_splash = (TextView)findViewById(R.id.tv_splash);
        tv_splash.setText("");
        load = ".";

        String androID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //device ID get
        Log.v("Device Id:", androID);
        Log.v("getDeviceId();", getDeviceId());
        Log.v("Device Model:", Build.MODEL);

        msplashTread = new splashThread(true);
        msplashTread.start();

    }

    public void onBackPressed(){
        super.onBackPressed ();
    }

    class splashThread extends Thread {
        private boolean isPlay = false;

        public splashThread(boolean isPlay) {
            this.isPlay = isPlay;
        }

        public void stopThread() {
            isPlay = !isPlay;
        }

        public void run() {
            super.run();
            while (isPlay) {
                try{
                    synchronized(this){
                        //기다리는 시간
                        if((char)inData[4] == '1') { //1일 경우 1초 후 바로 넘어가도록 한다
                            wait(1000);//Toast.makeText(getApplicationContext(), "접속성공", Toast.LENGTH_SHORT).show();
                            isPlay = !isPlay;

                        }
                        else
                            wait(5000);
                    }
                }catch(InterruptedException ex){
                }
                finish();

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.setClass(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    Thread worker = new Thread() {
        public void run() {
            SocketConnection.start();
            String toServerData ;

            toServerData = getDeviceId()+"+"+Build.MODEL;
            outData[0] = (byte) PacketInfo.SOF;
            outData[1] = (byte)PacketInfo.MPC_RDY;
            outData[2] = (byte)PacketInfo.getSEQ();
            outData[3] = (byte)toServerData.length();
            for(i = 4; i < 4+toServerData.length() ; i++)
            {
                outData[i] = (byte)toServerData.charAt(i-4);
            }
            outData[4+toServerData.length()] = (byte)85;

            try {
                dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                dos.write (outData,0,outData[3]+5);
                dos.flush();
            } catch (IOException e) {
                Toast.makeText( getApplicationContext() ,"서버와의 연결이 현재 불가능합니다.",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            try {
                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);

                int SOF = inData[0];

                System.out.println(inData[0]);
                System.out.println(inData[1]);
                System.out.println(inData[2]);
                System.out.println(inData[3]);
                System.out.println((char)inData[4]);
                System.out.println(inData[5]);

            }catch(IOException e){
                e.printStackTrace();
            }

        }
    };

    public String getDeviceId()
    {//:: LG-F340L
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getDeviceId();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
