package com.onpuri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Hye-rim on 2016-03-18.
 */
public class SplashActivity extends Activity {
    private ProgressBar spinner;
    DataOutputStream dos;
    DataInputStream dis;

    TextView tv_splash;
    String load = ".";
    byte[] outData = new byte[261];
    byte[] inData = new byte[261];

    private splashThread msplashTread;

    private ActivityList actManager = ActivityList.getInstance();

    int i;
    //data to the server
    //String SOF,DATA;
    //int OPC, SEQ, LEN, CRC;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        actManager.addActivity(this);
        setContentView(R.layout.activity_splash);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(ProgressBar.VISIBLE);
        spinner.setIndeterminate(true);
        spinner.setMax(100);

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
       // Log.v("태그", "메시지");
        //TelephonyManager tm =(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //String deviceid = tm.getDeviceId();

        String androID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //device ID get
        Log.v("Device Id:", androID);
        Log.v("getDeviceId();", getDeviceId());
        Log.v("getDeviceId();", getPhoneNumber());
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
        //if(socket != null) {
   /*         try {
              //  socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
      //  }
    }

    Thread worker = new Thread() {
        public void run() {
            SocketConnection.start();
            String toServerData ;

            toServerData = getDeviceId()+"+"+Build.MODEL;
            outData[0] = (byte)PacketInfo.SOF;
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
                dis = new DataInputStream(SocketConnection.socket.getInputStream());
                dis.read(inData);
                //System.out.println("Data form server: " + ((char)inData[0].) + (char)inData[1]);
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

    public String getPhoneNumber()
    {
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
