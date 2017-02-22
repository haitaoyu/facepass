package com.example.hayden.facepass;

import android.os.Environment;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Hayden on 2016/5/12.
 */
public class FaceDetect {
    // 回调接口
    public interface CallBack
    {
        // 识别成功
        void success(JSONObject result);

        // 识别失败
        void error(FaceppParseException e);
    }

    // 开始识别
    public static void detect(final CallBack callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpRequests request = new HttpRequests(constant.KEY, constant.SECRET, true, true);
                File mfile=new File(Environment.getExternalStorageDirectory()+"/test0");
                FileInputStream fis= null;
                try {
                    fis = new FileInputStream(mfile);
                    int length=fis.available();
                    byte[] buffer=new byte[length];
                    fis.read(buffer);
                    fis.close();

                    //如果识别成功，调用success回调函数
                    JSONObject result=request.detectionDetect(new PostParameters().setImg(buffer));
                    Log.e("TAG", result.toString());
                    if (callback != null)
                    {
                        callback.success(result);
                    }
                } catch (FaceppParseException e)
                {
                    e.printStackTrace();
                    if (callback != null)
                    {
                        callback.error(e);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
