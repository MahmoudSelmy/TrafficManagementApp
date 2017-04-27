package com.masstudio.selmy.tmc;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tech lap on 16/03/2017.
 */

public class TMC extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //firebase only store stringd in your cashe
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
