package com.example.jp.socketapp02;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private String IP_Server;
    private int PORT_Server;

    EditText etBarcode;
    Button btScan;
    TextView tvLblBarcode;
    TextView tvLblNama;
    TextView tvNama;
    TextView tvLblBerat;
    TextView tvBerat;
    TextView tvLblHarga;
    TextView tvHarga;
    TextView tvStatus;

    SocketClient sckClient = new SocketClient();
    int intKodeKirim;

    //Menampilkan Gambar
    private NetworkImageView mNetworkImageView;
    private ImageLoader mImageLoader;
    private String PATH_IMAGE;
    //====

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitialingObject();
        ClearData();
        intKodeKirim = 1;
        Thread myThread = new Thread(new MyServerThread());
        myThread.start();

        //KeyPress TxtBarcodenya di sini.
        etBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                        if (etBarcode.getText().toString() != null){
                            Connect();
                        }
                    }
                }
                return false;
            }
        });
        //End KeyPress
        UpdateStatus("Program Siap.");
    }

    public void InitialingObject(){
        IP_Server = "192.168.2.11";
        PORT_Server = 1024;

        etBarcode = (EditText)findViewById(R.id.etBarcode);
        btScan = (Button)findViewById(R.id.btSearch);
        tvLblBarcode = (TextView)findViewById(R.id.textView2);
        tvLblNama = (TextView)findViewById(R.id.tvLabelNama);
        tvNama = (TextView)findViewById(R.id.tvNama);
        tvLblBerat = (TextView)findViewById(R.id.tvLabelBerat);
        tvBerat = (TextView)findViewById(R.id.tvBerat);
        tvLblHarga = (TextView)findViewById(R.id.tvLabelHarga);
        tvHarga = (TextView)findViewById(R.id.tvHarga);
        tvStatus = (TextView)findViewById(R.id.tvStatus);

        mNetworkImageView = (NetworkImageView) findViewById(R.id
                .networkImageView);
    }

    private void ClearData(){
        tvLblNama.setText("");
        tvNama.setText("");
        tvLblBerat.setText("");
        tvBerat.setText("");
        tvLblHarga.setText("");
        tvHarga.setText("");

        mNetworkImageView.setDefaultImageResId(0);
    }

    private void SetLabelData(){
        tvLblNama.setText("Nama");
        tvLblBerat.setText("Berat");
        tvLblHarga.setText("Harga");
    }

    public void UpdateStatus(String status){
        tvStatus.setText("Status : " + status);
    }

    //=====================================[SCAN BARCODE]========================================================
    public void scanBarcode(View view){

    }
    //===================================[END SCAN BARCODE]======================================================

    //====================================[KODING SOCKET]========================================================
    public void Connect(){
        ClearData();
        sckClient.strKirim = "-!" + intKodeKirim + "|" + ",.,." + "|" + etBarcode.getText().toString() + "!-";
        Log.d("MAIN","" + PORT_Server);
        sckClient.Connect(IP_Server,PORT_Server);
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
                            if (!message.isEmpty()) {
                                String strKodeTerima;
                                strKodeTerima = message.substring(0,1);
                                if (Integer.parseInt(strKodeTerima) == intKodeKirim){
                                    intKodeKirim = intKodeKirim + 1;
                                    if (intKodeKirim > 9){
                                        intKodeKirim = 0;
                                    }
                                    String strMessage[] = message.split(",.,.");
                                    if (Integer.parseInt(strMessage[1]) == 1) {
                                        SetLabelData();
                                        tvNama.setText(strMessage[4]);
                                        tvBerat.setText(strMessage[5]);
                                        tvHarga.setText(strMessage[6]);
                                        PATH_IMAGE = "http://" + IP_Server + "/nsipic/" + strMessage[3] + ".jpg";
                                        ShowImage();
                                        UpdateStatus("Terima Data Berhasil.");
                                    }else if (Integer.parseInt(strMessage[1]) == 2){
                                        UpdateStatus("Data Kosong.");
                                    }else{
                                        UpdateStatus("Error("+ strMessage[2] + ")");
                                    }
                                    sckClient.Disconnect();
                                }
                            }
                        }
                    });
                }
            }catch (Exception e){
                UpdateStatus("Error ("+ e.toString() + ")");
            }
        }
    }
    //==================================[END KODING SOCKET]======================================================

    //Menampilkan Gambar
    private void ShowImage(){
        try {
            // Instantiate the RequestQueue.
            mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                    .getImageLoader();
            //Image URL - This can point to any image file supported by Android
            //final String url = "http://192.168.2.11/nsipic/test.jpg";
            final String url = PATH_IMAGE;
            mImageLoader.get(url, ImageLoader.getImageListener(mNetworkImageView,
                    0, 0));
            mNetworkImageView.setImageUrl(url, mImageLoader);
        }catch (Exception e){

        }
    }
    //====
}
