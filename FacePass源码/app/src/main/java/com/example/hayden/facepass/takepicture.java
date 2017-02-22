package com.example.hayden.facepass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class takepicture extends AppCompatActivity {
    private Button mButton1;
    private Camera cm;
    private FrameLayout mflayout;
    private Button mButton4;
    private static final int MSG_SUCCESS = 0X111;
    private static final int MSG_ERROR = 0X112;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {

        //在主线程里请求访问网络
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_takepicture);
        mButton1=(Button)findViewById(R.id.button1) ;
        mflayout=(FrameLayout)findViewById(R.id.flayout);
        Intent intent=getIntent();
        final String username=intent.getStringExtra("UserName");

        cm = getCameraInstance();//创建camera实例
        CameraPreview preview = new CameraPreview(this, cm);//初始化预览
        mflayout.addView(preview);//输出预览到screen
        boolean hascamera = checkCameraHardware(this);
        if (hascamera) {
            Toast.makeText(this, "请务必保持平稳，不要抖动镜头", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "未检测到摄像头", Toast.LENGTH_SHORT).show();
        }
        mButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //等待画面
                ProgressDialog progDialog = null;
                progDialog = new ProgressDialog(takepicture.this);
                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDialog.setIndeterminate(false);
                progDialog.setCancelable(true);
                progDialog.setMessage("正在返回信息，请稍候...");
                progDialog.show();

                cm.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            File file = new File(Environment.getExternalStorageDirectory(),"test1");
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(data);
                            fos.close();
                            Bitmap bMap= BitmapFactory.decodeByteArray(data, 0, data.length);

                            Bitmap bMapRotate;
                            Configuration config = getResources().getConfiguration();
                            if (config.orientation==1)
                            { // 坚拍
                                Matrix matrix = new Matrix();
                                matrix.reset();
                                matrix.postRotate(270);
                                bMapRotate = Bitmap.createBitmap(bMap, 0, 0,
                                        bMap.getWidth(), bMap.getHeight(),
                                        matrix, true);
                                bMap = bMapRotate;
                            }

                            //
                            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                            File
                                    mfile = new File(Environment.getExternalStorageDirectory(),"test1");
                            BufferedOutputStream

                                    bos =new BufferedOutputStream(new FileOutputStream(file));bMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中
                            bos.flush();//输出
                            bos.close();//关闭
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                );//camera end
                Dialog alertDialog = new AlertDialog.Builder(takepicture.this).setTitle("提示信息：").setMessage("点击确定键查看检测结果！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/test1");
                                RecognitionCompare.Compare(username,bitmap, new RecognitionCompare.CallBack() {
                                    @Override
                                    public void success(JSONObject compare) {
                                        Message msg = Message.obtain();
                                        msg.what = MSG_SUCCESS;
                                        msg.obj = compare;
                                        mHandler2.sendMessage(msg);
                                    }

                                    @Override
                                    public void error(FaceppParseException e) {
                                        Message msg = Message.obtain();
                                        msg.what = MSG_ERROR;
                                        msg.obj = e.getErrorMessage();
                                        mHandler2.sendMessage(msg);
                                    }
                                });
                            }
                        })
                        .create();
                alertDialog.show();
            }//此处是点击事件的结束


        });
    }

    private Handler mHandler2 = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case MSG_SUCCESS:
                    JSONObject compare = (JSONObject) msg.obj;
                    parseResult2(compare);
                    break;
                case MSG_ERROR:
                    String errorMsg = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMsg))
                    {
                        Dialog alertDialog = new AlertDialog.Builder(takepicture.this).setTitle("错误信息：").setMessage("用户名未注册，请先注册！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }
                    break;

                default:
                    break;
            }
        }

        private void parseResult2(JSONObject compare) {

            try {
                double facesimilar = compare.getDouble("similarity");
                Intent intent=getIntent();
                final String username=intent.getStringExtra("UserName");

                if (facesimilar>80.000000 )
                {
                    Dialog alertDialog = new AlertDialog.Builder(takepicture.this).setTitle("认证结果：").setMessage("欢迎"+" "+username+" "+"少侠归来！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    finish();
                                }
                            })
                            .create();
                    alertDialog.show();
                    return;
                }else {
                    Dialog alertDialog = new AlertDialog.Builder(takepicture.this).setTitle("认证结果：").setMessage("请少侠不要冒充别人登录！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create();
                    alertDialog.show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    };





    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object.初始化相机*  */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(1); // attempt to get a Camera instance
            c.setDisplayOrientation(90);//将摄像头翻转90度
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }



    }














