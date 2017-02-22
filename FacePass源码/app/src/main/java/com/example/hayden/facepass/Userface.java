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
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Userface extends AppCompatActivity {
    private Button mButton5;
    private Camera cm1;
    private FrameLayout mflayout5;
    private ProgressDialog progDialog;
    //识别成功
    private static final int MSG_SUCCESS = 0X111;
    //识别失败
    private static final int MSG_ERROR = 0X112;



    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate( Bundle savedInstanceState ) {

            //在主线程里请求访问网络
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());



        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_userface);
        mButton5=(Button)findViewById(R.id.button5) ;
        mflayout5=(FrameLayout)findViewById(R.id.flayout5);
        cm1 = getCameraInstance();
        CameraPreview preview = new CameraPreview(this, cm1);
        mflayout5.addView(preview);
        boolean hascamera = checkCameraHardware(this);
        if (hascamera) {
            Toast.makeText(this, "请务必保持平稳，不要抖动镜头", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "未检测到摄像头", Toast.LENGTH_SHORT).show();
        }
        mButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //等待画面
                ProgressDialog progDialog = null;
                progDialog = new ProgressDialog(Userface.this);
                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progDialog.setIndeterminate(false);
                progDialog.setCancelable(true);
                progDialog.setMessage("正在注册中，请稍候...");
                progDialog.show();



                cm1.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            File file = new File(Environment.getExternalStorageDirectory(),"test0");
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
                });//此处camera调用结束



                FaceDetect.detect(new FaceDetect.CallBack() {
                    @Override
                    public void success(JSONObject result) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SUCCESS;
                        msg.obj = result;
                        mHandler0.sendMessage(msg);
                    }

                    @Override
                    public void error(FaceppParseException e) {
                        Message msg = Message.obtain();
                        msg.what = MSG_ERROR;
                        msg.obj = e.getErrorMessage();
                        mHandler0.sendMessage(msg);
                    }
                });


                Intent intent=getIntent();
                final String name=intent.getStringExtra("Name");
                PersonCreate.create(name, new PersonCreate.CallBack() {
                    @Override
                    public void success(JSONObject create) {
                        Message msg = Message.obtain();
                        msg.what = MSG_SUCCESS;
                        msg.obj = create;
                        mHandler1.sendMessage(msg);

                    }

                    @Override
                    public void error(FaceppParseException e) {
                        Message msg = Message.obtain();
                        msg.what = MSG_ERROR;
                        msg.obj = e.getErrorMessage();
                        mHandler1.sendMessage(msg);
                    }
                });





            }//此处是点击事件的结束



        });
    }


    private Handler mHandler0 = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case MSG_SUCCESS:
                    JSONObject result = (JSONObject) msg.obj;
                    parseResult(result);
                    break;
                case MSG_ERROR:
                    String errorMsg = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMsg))
                    {
                        Dialog alertDialog = new AlertDialog.Builder(Userface.this).setTitle("错误信息：").setMessage("服务器错误！请点击确定按钮重试!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Userface.this, MainActivity.class));
                                        Userface.this.finish();
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

        private void parseResult(JSONObject result) {

            try {
                JSONArray faces = result.getJSONArray("face");
                int faceCount = faces.length();
                if (faceCount == 0)
                {
                   Dialog alertDialog = new AlertDialog.Builder(Userface.this).setTitle("检测结果：").setMessage("未检测到人脸，请点击确定按钮重试!")
                           .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   startActivity(new Intent(Userface.this, MainActivity.class));
                                   Userface.this.finish();
                               }
                           })
                           .create();
                        alertDialog.show();

                    return;
                }else {


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        };




    private Handler mHandler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    JSONObject creat = (JSONObject) msg.obj;
                    parseResult1(creat);
                    break;
                case MSG_ERROR:
                    String errorMsg = (String) msg.obj;
                    if (TextUtils.isEmpty(errorMsg)) {
                        Dialog alertDialog = new AlertDialog.Builder(Userface.this).setTitle("注册结果：").setMessage("用户名已被占用！请点击确定按钮重试!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Userface.this, MainActivity.class));
                                        Userface.this.finish();
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
        private void parseResult1(JSONObject creat) {
            try {
                File mfile=new File(Environment.getExternalStorageDirectory()+"/test0");
                    FileInputStream fis=new FileInputStream(mfile);
                    int length=fis.available();
                    byte[] buffer=new byte[length];
                    fis.read(buffer);
                    fis.close();

                Intent intent=getIntent();
                final String name=intent.getStringExtra("Name");

                HttpRequests httpRequests = new HttpRequests(constant.KEY, constant.SECRET, true, true);
                System.out.println(httpRequests.groupAddPerson(new PostParameters().setGroupName("MyGroup").setPersonName(name)));
                JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(buffer));
                for (int i = 0; i < result.getJSONArray("face").length(); ++i) {
                    JSONObject addFace = httpRequests.personAddFace(new PostParameters().setPersonName(name).setFaceId(result.getJSONArray("face").getJSONObject(i).getString("face_id")));
                }
                Dialog alertDialog = new AlertDialog.Builder(Userface.this).setTitle("注册结果：").setMessage("注册成功！请点击确定按钮返回主界面!")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Userface.this, MainActivity.class));
                                Userface.this.finish();
                            }
                        })
                        .create();
                alertDialog.show();



            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FaceppParseException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    };


    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            return false;
        }
    }
    /** A safe way to get an instance of the Camera object.初始化相机*  */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(1);
            c.setDisplayOrientation(90);

        }
        catch (Exception e){
        }
        return c;
    }




}

