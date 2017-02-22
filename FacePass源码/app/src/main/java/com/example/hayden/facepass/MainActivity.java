package com.example.hayden.facepass;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mbutton;
    private Button mbutton2;
    private EditText meditText2;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meditText2= (EditText) findViewById(R.id.editText2);
        mbutton = (Button) findViewById(R.id.button);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()){

                    final String username = meditText2.getText().toString();
                    if (username.length() == 0) {
                        Toast.makeText(MainActivity.this, "请先填写用户名", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, takepicture.class);
                        intent.putExtra("UserName", username);
                        startActivity(new Intent(intent));

                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this,"网络出错,请检查网络连接",Toast.LENGTH_SHORT).show();
                }

            }
        });

        mbutton2 = (Button) findViewById(R.id.button2);
        final ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mbutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                NetworkInfo info = cwjManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()){
                    startActivity(new Intent(MainActivity.this, Username.class));
                }
                else
                {
                    Toast.makeText(MainActivity.this,"网络出错，请检查网络连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }}