package com.example.hayden.facepass;

import android.graphics.Bitmap;
import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Hayden on 2016/5/15.
 */
public class RecognitionVerify {

    public interface CallBack
    {
        void success(JSONObject verify);

        void error(FaceppParseException e);
    }

    public static void verify(final String username,final Bitmap bitmap, final CallBack callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpRequests httpRequests = new HttpRequests(constant.KEY, constant.SECRET, true, true);
//                File mfile=new File(Environment.getExternalStorageDirectory()+"/test1");
//                FileInputStream fis= null;
                try {
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

                    JSONObject syncRet = null,result=null,verify=null;
                    result=httpRequests.detectionDetect(new PostParameters().setImg(arrays));
                    for (int i = 0; i < result.getJSONArray("face").length(); ++i) {
                        syncRet = httpRequests.trainVerify(new PostParameters().setPersonName(username));
                        System.out.println(httpRequests.getSessionSync(syncRet.get("session_id").toString()));
                    }
                   verify=httpRequests.recognitionVerify(new PostParameters().setPersonName(username).setFaceId(
                            result.getJSONArray("face").getJSONObject(0).getString("face_id")));



                    Log.e("TAG",verify.toString());
                    if (callback != null)
                    {
                        callback.success(verify);
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
