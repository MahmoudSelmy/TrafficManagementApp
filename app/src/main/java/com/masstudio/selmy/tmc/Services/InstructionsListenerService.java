package com.masstudio.selmy.tmc.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.masstudio.selmy.tmc.Activities.AuthActivity;
import com.masstudio.selmy.tmc.POJO.Instruction;
import com.masstudio.selmy.tmc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

public class InstructionsListenerService extends Service {

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private Set<String> set ;
    private int size = 0;
    private SharedPreferences sharedpreferences;
    private String uid;
    public InstructionsListenerService() {
    }

    @Override
    public void onCreate() {

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        set = sharedpreferences.getStringSet("setInst",null);
        size =sharedpreferences.getInt("sizeInst", 0);
        if (set == null){
            set = new HashSet<String>();
            Log.d("NotificationLis","SETEMPTY");
        }
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Instructions");
        //Note = new NewMessageNotification();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Instruction inst = dataSnapshot.getValue(Instruction.class);
                Log.d("INSTLIS","1");
                Long instTime = inst.getTime() * -1;
                Long currentTime = System.currentTimeMillis();
                if ((currentTime - instTime) < 5*60*1000) {
                    if (!inst.getSenderId().equals(uid)) {
                        Log.d("INSTLIS", "2");
                        notifySurvey(key);
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

    private void notifySurvey(String key) {
        if (checkKey(key)){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("NotificationLis",key);
            Notification.Builder n  = new Notification.Builder(this)
                    .setContentTitle("TMC")
                    .setContentText("New Instruction")
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
            editor.putStringSet("setInst", set);
            editor.putInt("sizeInst",size);
            editor.commit();
            Log.d("NotificationLis","Set size = " + set.size());
            return true;
        }
        Log.d("NotificationLis","key Exist "+ key);

        return false;
    }

}
