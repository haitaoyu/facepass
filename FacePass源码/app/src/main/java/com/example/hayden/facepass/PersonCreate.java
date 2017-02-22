package com.example.hayden.facepass;

import android.util.Log;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

import org.json.JSONObject;

/**
 * Created by Hayden on 2016/5/14.
 */
public class PersonCreate {
    public interface CallBack
    {
        void success(JSONObject create);

        void error(FaceppParseException e);
    }

    public static void create(final String name,final CallBack callback)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
               try {
                   HttpRequests httpRequests = new HttpRequests(constant.KEY, constant.SECRET, true, true);
                   JSONObject create=httpRequests.personCreate(new PostParameters().setPersonName(name));

                    Log.e("TAG", create.toString());
                    if (callback != null)
                    {
                        callback.success(create);
                    }
                } catch (FaceppParseException e)
                {
                    e.printStackTrace();
                    if (callback != null)
                    {
                        callback.error(e);
                    }
                }
            }
        }).start();
    }
}
