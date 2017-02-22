package com.example.hayden.facepass;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Hayden on 2016/5/14.
 */
public class RecognitionCompare {

    public interface CallBack
    {
        void success(JSONObject compare);

        void error(FaceppParseException e);
    }

    public static void Compare(final String username, final Bitmap bitmap, final CallBack callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpRequests request = new HttpRequests(constant.KEY, constant.SECRET, true, true);
//
                try {File mfile=new File(Environment.getExternalStorageDirectory()+"/test1");
//                FileInputStream fis= null;
//                    fis = new FileInputStream(mfile);
//                    int length=fis.available();
//                    byte[] buffer=new byte[length];
//                    fis.read(buffer);
//                    fis.close();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100, outStream);
                    byte[] arrays = outStream.toByteArray();
                    outStream.flush();
                    outStream.close();

                     JSONObject result0=null,getinto=null,compare=null;
                     result0=request.detectionDetect(new PostParameters().setImg(arrays));
                     getinto=request.personGetInfo(new PostParameters().setPersonName(username));
                     for (int i = 0; i < result0.getJSONArray("face").length(); ++i) {
                     compare = request.recognitionCompare(new PostParameters().setFaceId1(result0.getJSONArray("face").getJSONObject(0)
                                .getString("face_id")).setFaceId2(getinto.getJSONArray("face").getJSONObject(0)
                                .getString("face_id")));
                    }

                    Log.e("TAG",compare.toString());
                    if (callback != null)
                    {
                        callback.success(compare);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
