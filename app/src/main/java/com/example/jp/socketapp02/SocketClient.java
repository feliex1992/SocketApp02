package com.example.jp.socketapp02;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.prefs.NodeChangeListener;

/**
 * Created by JP on 12/20/2017.
 */

public class SocketClient {
    public String TAG = "SocketClient";

    public Socket sckClient;
    private String serverIp = "192.168.2.11";
    private int serverPort = 1024;

    private String strData;
    public String strKirim;

    private long startTime = 01;

    public boolean isConnected() {
        return sckClient != null && sckClient.isConnected() && !sckClient.isClosed();
    }

    public void Connect(String ip, int port){
        serverIp = ip;
        serverPort = port;
        new Thread(new ConnectRunnable()).start();
    }

    public void Disconnect() {
        stopThreads();

        try {
            sckClient.close();
            Log.d(TAG,"Disconnected!");
        } catch (IOException e) { }

    }

    private void stopThreads() {
        //if (ConnectRunnable != null)
        //    ConnectRunnable.interrupt();
    }

    public class ConnectRunnable implements Runnable{

        public void run(){
            try {
                Log.d(TAG, "Connecting to Server.");
                InetAddress serverAddress = InetAddress.getByName(serverIp);
                startTime = System.currentTimeMillis();

                sckClient = new Socket();
                sckClient.connect(new InetSocketAddress(serverAddress,serverPort),5000);

                long time = System.currentTimeMillis() - startTime;
                Log.d(TAG,"Connected! Current duration: " + time + "ms");
                Send(strKirim);
            }catch (Exception e){
                Log.d(TAG,"Error Connect : " + e.toString());
            }
        }
    }

    public void Send(String string){
        strData = string;
        new Thread(new SendRunnable()).start();
    }

    public class SendRunnable implements Runnable{
        PrintWriter pw;

        public void run(){
            try {
                Log.d(TAG,"Send Message : " + strData);
                pw = new PrintWriter(sckClient.getOutputStream());
                pw.write(strData);
                pw.flush();
            }catch (Exception e){
                Log.d(TAG, "Error Kirim : " + e.toString());
            }
        }
    }
}
