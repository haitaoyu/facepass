package com.example.hayden.facepass;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Username extends AppCompatActivity {
    private Button mbutton3;
    private EditText meditText;
    private ImageButton mimageButton;
    public String txt;

    @Override
   public void onCreate( Bundle savedInstanceState ) {
        //在主线程里请求访问网络
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        mimageButton= (ImageButton) findViewById(R.id.imageButton);
        mimageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                finish();
            }
        });

        meditText= (EditText) findViewById(R.id.editText);
        mbutton3= (Button)findViewById(R.id.button3);
        mbutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                final String txt = meditText.getText().toString();


                if (txt.length()==0) {
                    Toast.makeText(Username.this,"请先填写用户名",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent=new Intent();
                    intent.setClass(Username.this,Userface.class);
                    intent.putExtra("Name",txt);
                    startActivity(new Intent(intent));
                    Username.this.finish();
                }
        }
    });
    }
}