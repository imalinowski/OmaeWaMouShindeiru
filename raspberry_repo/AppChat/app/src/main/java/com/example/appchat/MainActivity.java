package com.example.appchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ArrayList<Message> messages;

    private EditText message;
    private TextView debug;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager layoutManager;

    static Socket TCP;
    private DataOutputStream out;
    private DataInputStream in;
    private final String SECURE_CODE = "3ce965ac7f9328";

    private String debugS = "disconnect";
    private int state = 0;
    private int port = 8000;//9090
    private String ip = "192.168.0.6"; //35.228.116.93 192.168.0.6 адресс сервера!
    private MyTimer timer;
    static String myName="";

    static int messageC = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.editText);
        debug = findViewById(R.id.debug);

        messages = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.messages_recycler);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MessagesAdapter(messages);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        connect();
        timer = new MyTimer();
        timer.start();
    }

    void connect(){
        byte[] data = new byte[1024];
        new Thread(()->{
            try {
                TCP = new Socket(InetAddress.getByName(ip), port);
                Log.i("RASPBERRY","connect!");
                state = 1;
                debugS = "connect";
                out = new DataOutputStream(TCP.getOutputStream());
                in = new DataInputStream(TCP.getInputStream());

                while (in.read(data)!=-1 && !TCP.isClosed()) {
                    messages.add(new Message(new JSONObject(new String(data))));
                    Log.i("RASPBERRY",new String(data));
                    Arrays.fill(data, (byte) 0);
                }
                TCP.close();
                debugS = "disconnect";
            } catch (IOException | JSONException e) {
                debugS = e.getMessage();
                Log.i("RASPBERRY",e.getMessage());
            }finally { try {
                if(TCP!=null)TCP.close();
                state = 2;
                in = null;
                out = null;
            } catch (IOException e) {
                e.printStackTrace();
            }}
        }).start();
    }

    public void pushMes(View view) {
        if(message.getText().toString().contains("set ip>")){
            String line = message.getText().toString();
            message.setText("");
            ip = line.substring(line.indexOf('>')+1);
            Toast.makeText(this,"ip set to"+ip+"\nreconnect...",Toast.LENGTH_LONG).show();
            try{
                if(TCP!=null)TCP.close();
                connect();}
            catch (IOException e) {
                debugS = e.getMessage();
            }
            return;
        }

        if(message.getText().toString().contains("set port>")){
            String line = message.getText().toString();
            message.setText("");
            port = Integer.parseInt(line.substring(line.indexOf('>')+1));
            Toast.makeText(this,"port set to"+port+"\nreconnect...",Toast.LENGTH_LONG).show();
            try{
                if(TCP!=null)TCP.close();
                connect();}
            catch (IOException e) {
                debugS = e.getMessage();
            }
            return;
        }

        if(message.getText().toString().equals("")) return;

        String json = "{\"hour\":" + (new Date()).getHours()
                +",\"minute\":" + (new Date()).getMinutes()
                +",\"message\":\""+(message.getText().toString())+"\"}";
        try {
            messages.add(new Message(new JSONObject(json)));
        } catch (JSONException e) {
            Log.i("RASPBERRY",e.getMessage());
            message.setText(("error"));
            return;
        }
        new Thread(()->{ try {
                if(out==null) return;
                byte[] t = (SECURE_CODE+message.getText().toString()).getBytes();
                if(myName.equals("")) myName = message.getText().toString();
                out.write(t);
                out.flush();

                Log.i("RASPBERRY","send -> "+ new String(t));
            } catch (IOException e) {
            debugS = e.getMessage();
            try {
                TCP.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        }).start();

        message.setText("");
    }

    void update()  {

        if(state == 1) Toast.makeText(this, "connect", Toast.LENGTH_SHORT).show();
        if(state == 2) Toast.makeText(this, "disconnect", Toast.LENGTH_SHORT).show();
        if(state!=0) state = 0;

        if(messages.size() > messageC){
            adapter.notifyDataSetChanged();
            layoutManager.smoothScrollToPosition(recyclerView,
                    new RecyclerView.State(),messages.size()-1);
            messageC++;
        }

        debug.setText((TCP!=null && !TCP.isClosed())?"connect":""+debugS);

    }

    class MyTimer extends CountDownTimer {
        MyTimer() {
            super(Integer.MAX_VALUE, 1000/10);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }
        @Override
        public void onFinish() {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (TCP != null) TCP.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.cancel();
    }
}