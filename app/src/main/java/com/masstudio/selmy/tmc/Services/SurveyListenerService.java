package com.masstudio.selmy.tmc.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.masstudio.selmy.tmc.Activities.SurveyActivity;
import com.masstudio.selmy.tmc.POJO.Survey;
import com.masstudio.selmy.tmc.R;
import com.masstudio.selmy.tmc.Utils.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SurveyListenerService extends Service {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Set<String> set ;
    private int size = 0;
    private SharedPreferences sharedpreferences;
    private LatLng lastKnownLocation;
    //private NewMessageNotification Note;

    @Override
    public void onCreate() {
        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        set = sharedpreferences.getStringSet("set",null);
        size =sharedpreferences.getInt("size", 0);
        if (set == null){
            set = new HashSet<String>();
            Log.d("NotificationLis","SETEMPTY");
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("NOTI","onBind");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getUid()!=null){

        }

        return null;
    }

    //When the service is started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SURVEYLis","1");
        if (intent != null)
            if(intent.getExtras() != null) {
            lastKnownLocation = intent.getExtras().getParcelable("LOC");
            Log.d("SURVEYLis","2 " + lastKnownLocation.toString()) ;

        }
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Survey");
        //Note = new NewMessageNotification();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Survey survey = dataSnapshot.getValue(Survey.class);
                Long surveyTime = survey.getTime() * -1;
                Long currentTime = System.currentTimeMillis();
                List<String> segments = Arrays.asList(Constants.SEGMENTS_PATHES_ARRAY);

                if ((currentTime - surveyTime) < 5*60*1000){
                    int n = segments.indexOf(survey.getPath());
                    Log.d("SURVEYLis","3 n = " + n);
                    if (n == 0 || n == 4){
                        if (PolyUtil.isLocationOnPath(lastKnownLocation,Constants.decodeSegment(survey.getPath()),false,10)){
                            notifySurvey(key);
                            Log.d("SURVEYLis","4 n = " + n);
                        }
                    }else {
                        if (PolyUtil.isLocationOnPath(lastKnownLocation,Constants.decodeSegment(survey.getPath()),false,10)){
                            notifySurvey(key);
                            Log.d("SURVEYLis","5 n = " + n);
                        }else if (PolyUtil.isLocationOnPath(lastKnownLocation,Constants.decodeSegment(segments.get(n-1)),false,10)){
                            rerouteNotif(key , n);
                            Log.d("SURVEYLis","6 n = " + n);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //START_NOT_STICKY
        //START_STICKY
        return START_STICKY;
    }

    private void rerouteNotif(String key , int n) {
        if (checkKey(key)){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putString("KEY",key);
            intent.putExtras(bundle);
            Log.d("NotificationLis",key);
            Notification.Builder noti  = new Notification.Builder(this)
                    .setContentTitle("TMC")
                    .setContentText(Constants.SEGMENTS_REROTE_MSG_ARRAY[n])
                    .setSmallIcon(R.drawable.ic_drive_eta_white_24dp)
                    .setContentIntent(PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setAutoCancel(true);
            if (!Constants.SEGMENTS_REROTE_MSG_ARRAY[n].equals("null")){
                notificationManager.notify(1, noti.build());
            }
        }
    }

    private void notifySurvey(String key) {
        if (checkKey(key)){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(getApplicationContext(), SurveyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putString("KEY",key);
            intent.putExtras(bundle);
            Log.d("NotificationLis",key);
            Notification.Builder n  = new Notification.Builder(this)
                    .setContentTitle("TMC")
                    .setContentText("why you take so long ?")
                    .setSmallIcon(R.drawable.ic_drive_eta_white_24dp)
                    .setContentIntent(PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT))
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setAutoCancel(true);
            notificationManager.notify(1, n.build());
        }

    }

    private Boolean checkKey(String key){
        if ((set.isEmpty() || !set.contains(key))&& set.size() >= size){
            set.add(key);
            size = set.size();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putStringSet("set", set);
            editor.putInt("size",size);
            editor.commit();
            Log.d("NotificationLis","Set size = " + set.size());
            return true;
        }
        Log.d("NotificationLis","key Exist "+ key);

        return false;
    }

}
