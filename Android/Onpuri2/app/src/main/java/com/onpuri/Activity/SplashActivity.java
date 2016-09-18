package com.onpuri.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onpuri.Activity.Login.LoginActivity;
import com.onpuri.ActivityList;
import com.onpuri.R;
import com.onpuri.Server.PacketInfo;
import com.onpuri.Server.SocketConnection;
import com.tsengvn.typekit.TypekitContextWrapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hye-rim on 2016-03-18.
 */
//Loading Activity
public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    private TextView tvVersion; //version name
    private ProgressBar spinner; //progressbar

    DataOutputStream dos; //out data stream
    DataInputStream dis; //in data stream
    byte[] outData = new byte[261]; // Stored out data
    byte[] inData = new byte[261]; //Stored in data

    private splashThread msplashTread; //Splash thread
    private boolean isFail = false;

    private ActivityList actManager = ActivityList.getInstance();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            perrmissionWork();
        } else {
            splashWork();
        }
    }

    private void splashWork() {
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(ProgressBar.VISIBLE);
        spinner.setIndeterminate(true);
        spinner.setMax(100);

        actManager.addActivity(this);

        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
            Log.e(TAG, "정보 가져오기 실패");
        }
        String appVersion = pi.versionName;

        tvVersion = (TextView)findViewById(R.id.tv_version);
        tvVersion.setText("Ver." + appVersion);

        msplashTread = new splashThread(true);
        msplashTread.start();

        if (worker != null && worker.isAlive()) {  //이미 동작하고 있을 경우 중지
            worker.interrupt();
        }

        worker.start();
        try {
            worker.join();
        } catch (InterruptedException e) {
            isFail = true;
            Log.e(TAG, "서버 접속 실패!");
        }

        String androID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); //device ID get
        Log.d("Device Id:", androID);
        Log.d("Device Model:", Build.MODEL);
    }

    private void failServer() {
        final boolean[] isDialogOk = {false};
        if(isFail){
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 내용
                    if (!SplashActivity.this.isFinishing()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(SplashActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SplashActivity.this.finish();
                            }
                        });
                        alert.setMessage("서버 접속에 실패 하였습니다.");
                        alert.show();
                    }
                }
            }, 0);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void perrmissionWork() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("PHONE_STATE");
        if (!addPermission(permissionsList,Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE_STORAGE");
        if (!addPermission(permissionsList,Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ_STORAGE");
        if(!addPermission(permissionsList,Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(
                    permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        splashWork();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList,String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    private void showMessageOKCancel(String message,android.content.DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this).setMessage(message)
                .setPositiveButton("OK", onClickListener).setCancelable(false)
                .setNegativeButton("Cancel", null).create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    splashWork();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        }
    }

    public String getDeviceId()
    {//:: LG-F340L
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getDeviceId();
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
            int i = 0;
            while (isPlay) {
                try{
                    synchronized(this){
                        //기다리는 시간
                        if((char)inData[4] == '1') { //1일 경우 1초 후 바로 넘어가도록 한다
                            wait(1000);
                            isPlay = !isPlay;
                        }
                        else {
                            wait(5000);
                            if(isFail)
                                failServer();
                        }
                    }
                }catch(InterruptedException ex){
                    isFail = true;
                    Log.e(TAG, "서버 접속 실패!");
                }

                if(!isFail && i < 1) {
                    i++;
                    Log.e(TAG, "로그인가자");
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.setClass(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }
    }

    Thread worker = new Thread() {
        public void run() {
            isFail = !SocketConnection.startTest(); //실패 : false 반환되므로 true로 전환
            String toServerData ;
            int i;

            if(!isFail){

                toServerData = getDeviceId() + "+" + Build.MODEL;
                outData[0] = (byte) PacketInfo.SOF;
                outData[1] = (byte) PacketInfo.MPC_RDY;
                outData[2] = (byte) PacketInfo.getSEQ();
                outData[3] = (byte) toServerData.length();
                for (i = 4; i < 4 + toServerData.length(); i++) {
                    outData[i] = (byte) toServerData.charAt(i - 4);
                }
                outData[4 + toServerData.length()] = (byte) 85;

                try {
                    dos = new DataOutputStream(SocketConnection.socket.getOutputStream());
                    dos.write(outData, 0, outData[3] + 5);
                    dos.flush();
                } catch (Exception e) {
                    isFail = true;
                    Log.e(TAG, "서버 접속 실패!");
                }

                try {
                    dis = new DataInputStream(SocketConnection.socket.getInputStream());
                    dis.read(inData);

                    int SOF = inData[0];

                    System.out.println(inData[0]);
                    System.out.println(inData[1]);
                    System.out.println(inData[2]);
                    System.out.println(inData[3]);
                    System.out.println((char) inData[4]);
                    System.out.println(inData[5]);

                } catch (Exception e) {
                    isFail = true;
                    Log.e(TAG, "서버 접속 실패!");
                }
            }
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public void onBackPressed(){
        super.onBackPressed ();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        actManager.removeActivity(this);
    }

}
