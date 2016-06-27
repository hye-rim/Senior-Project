package com.onpuri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by kutemsys on 2016-03-29.
 */
public class SocketConnection {
    static java.net.Socket socket;

    void SocketConnection(){

    }

    static void start(){  //바로 연결시작
        try {
            socket = new java.net.Socket("218.150.182.52", 8888);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static   void close(){
        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
