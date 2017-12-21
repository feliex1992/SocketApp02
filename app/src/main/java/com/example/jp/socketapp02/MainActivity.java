package com.example.jp.socketapp02;

import android.app.Application;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    EditText etIpServer;
    EditText etPortServer;
    EditText etMessage;
    EditText etFeedBack;

    SocketClient sckClient = new SocketClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIpServer = (EditText)findViewById(R.id.etIpServer);
        etPortServer = (EditText)findViewById(R.id.etPortServer);
        etMessage = (EditText)findViewById(R.id.etMessage);
        etFeedBack = (EditText)findViewById(R.id.etFeedBack);

        Thread myThread = new Thread(new MyServerThread());
        myThread.start();
    }

    public void Connect(View view){
        Log.d("MAIN","" + Integer.parseInt(etPortServer.getText().toString()));
        sckClient.Connect(etIpServer.getText().toString(),Integer.parseInt(etPortServer.getText().toString()));
    }

    public void Send(View view){
        Handler hendlerd = new Handler();

        sckClient.Send(etMessage.getText().toString());

        hendlerd.postDelayed(new Runnable() {
            @Override
            public void run() {
                sckClient.Send(etMessage.getText().toString());
            }
        },1000);
    }

    class MyServerThread implements Runnable {
        Socket s;
        ServerSocket ss;
        InputStreamReader isr;
        BufferedReader bufferedReader;
        Handler h = new Handler();

        String message;
        int X = 0;

        @Override
        public void run() {
            try {
                ss = new ServerSocket(1124);
                while (true){
                    s = ss.accept();
                    while (!s.isConnected()){
                        String strLoop;
                        X = X +1;
                        strLoop = String.valueOf(X);
                        Log.d("RECEIVE", "LOOP " + strLoop);
                    }
                    isr = new InputStreamReader(s.getInputStream());
                    bufferedReader = new BufferedReader(isr);
                    message = bufferedReader.readLine();

                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            etFeedBack.setText(message);
                        }
                    });
                }
            }catch (Exception e){

            }
        }
    }
}
