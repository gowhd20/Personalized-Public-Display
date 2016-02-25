package com.example.dhaejong.acp2;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.os.IBinder;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by dhaejong on 24.2.2016.
 */
public class httpService extends Service {

    private static final String TAG = "httpService";

    httpNetwork mHttpNetwork;
    Context context = this;
    boolean exit = false;
    protected ArrayList<CategoryList> tags;
    private void getRequest(){

        mHttpNetwork = new httpNetwork(this);

        try {
            String response = mHttpNetwork.getRequest(SystemPreferences.GET_CATEGORIES_URL);
            int categoryId = getIdOfCategory(response);
            Log.d(TAG, Integer.toString(categoryId));
            Log.d(TAG, response);

            // store category names
            CategoryList mCategory = new CategoryList();
            mCategory.setCategories(response);


            // TODO: modern style, will do later
            /*
            ArrayList<String> test = new ArrayList<>();
            test.add(response);
            CategoryList cList = new CategoryList(test);
            //tags = cList.categories;
            //Log.d(TAG, cList.toString());
            for(CategoryList m : tags){
                for(int i=0; i<m.categories.size(); i++) {
                    nTags.add(m.categories.get(i));
                    Log.d(TAG, m.categories.get(i));
                }
            }*/

            // save id of category in sharedpreference for future post
            SharedPref mSharedPref = new SharedPref(this);
            mSharedPref.saveInSp(SystemPreferences.CATEGORY_ID_IN_USE, categoryId);

            //Log.d(TAG, response);
        }catch(IOException e){
            e.printStackTrace();
            Log.e(TAG, "get failed");
        }
    }


    private int getIdOfCategory(String response){
        JSONArray arrayObj;
        JSONObject obj;
        String eventId;

        try{
            arrayObj = new JSONArray(response);
            obj = arrayObj.getJSONObject(0);

            while(obj.keys().hasNext()){
                if(obj.get("category").toString().equals(SystemPreferences.CATEGORY_IN_USE)){
                    eventId = obj.get("id").toString();
                    return Integer.valueOf(eventId);
                }
                obj.keys().next();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return 0;
    }

    Thread getRequestThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(!exit) {
                getRequest();
                try {
                    Thread.sleep(SystemPreferences.GET_CATEGORY_REQUEST_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });

    @Override
    public void onCreate() {
        this.tags = new ArrayList<>();
        // init request with server
        mHttpNetwork = new httpNetwork(this);

        // TODO: need a condition to check whether the user is available of Internet connection,
        // TODO: ckeck out https://github.com/novoda/merlin

        getRequestThread.start();


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        exit = true;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

}
