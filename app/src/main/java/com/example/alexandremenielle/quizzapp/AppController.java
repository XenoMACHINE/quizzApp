package com.example.alexandremenielle.quizzapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.duelmanagerlib.AppManager;

/**
 * Created by alexandremenielle on 23/05/2018.
 */

public class AppController extends Application implements Application.ActivityLifecycleCallbacks
{

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Toast.makeText(this, "----------onCreate()---------", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i("ApplicationName","----------onActivityStarted()---------");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i("ApplicationName","----------onActivityResumed()---------");
        AppManager.getInstance().setUserConnected(true);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //Toast.makeText(this, "----------onActivityPaused()---------", Toast.LENGTH_LONG).show();
        AppManager.getInstance().setUserConnected(false);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i("ApplicationName","----------onActivityStopped()---------");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i("ApplicationName","----------onActivitySaveInstanceState()---------");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}