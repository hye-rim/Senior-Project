package com.onpuri.Server;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import static android.app.PendingIntent.getActivities;
import static android.app.PendingIntent.getActivity;

/**
 * Created by kutemsys on 2016-03-29.
 */
public class SocketConnection {
    static public java.net.Socket socket;

    static public void start(){  //바로 연결시작
        try {
            socket = new java.net.Socket("218.150.182.58", 2040);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public  void close(){
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
