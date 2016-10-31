package com.onpuri.Server;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.onpuri.Activity.MainActivity;
import com.onpuri.Activity.SplashActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import static android.app.PendingIntent.getActivities;
import static android.app.PendingIntent.getActivity;

/**
 * Created by kutemsys on 2016-03-29.
 */
public class SocketConnection {
    private static final String TAG = "SocketConnection";
    static public java.net.Socket socket;

    private static boolean isFail = false;

    public boolean isFail() {
        return isFail;
    }

    public SocketConnection() {
        socket = new Socket();
    }

    public static void start(){  //바로 연결시작
        try {
            socket = new java.net.Socket("218.150.182.58", 2040);
        } catch (Exception e) {
            isFail = true;
            Log.e(TAG, "연결 실패");
        }
    }

    public static boolean startTest(){  //바로 연결시작
        try {
            socket = new java.net.Socket("218.150.182.58", 2040);
        } catch (Exception e) {
            isFail = true;
            Log.e(TAG, "연결 실패");
            return false;
        }

        return true;
    }


    static public  void close(){
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
