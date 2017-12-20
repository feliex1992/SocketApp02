package com.example.jp.socketapp02;

import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    SocketClient sckClient = new SocketClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etIpServer = (EditText)findViewById(R.id.etIpServer);
        etPortServer = (EditText)findViewById(R.id.etPortServer);
        etMessage = (EditText)findViewById(R.id.etMessage);
    }

    class MyServerThread implements Runnable{
        InputStreamReader isr;
        BufferedReader br;

        String strMessage;
        Handler h = new Handler();

        @Override
        public void run(){
            Log.d("MAIN","Terima Data OK.1");
            try {
                Log.d("MAIN","Terima Data OK.");
                while (sckClient.sckClient.isConnected()) {
                    Log.d("MAIN","Terima Data OK.!!!");
                    isr = new InputStreamReader(sckClient.sckClient.getInputStream());
                    br = new BufferedReader(isr);
                    strMessage = br.readLine();
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), strMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }catch (Exception e){
                Log.d("MAIN","Error Terima Message : " + e.toString());
            }
        }
    }

    public void Connect(View view){
        Log.d("MAIN","" + Integer.parseInt(etPortServer.getText().toString()));
        sckClient.Connect(etIpServer.getText().toString(),Integer.parseInt(etPortServer.getText().toString()));
    }

    public void Send(View view){
        sckClient.Send(etMessage.getText().toString());
    }
}
