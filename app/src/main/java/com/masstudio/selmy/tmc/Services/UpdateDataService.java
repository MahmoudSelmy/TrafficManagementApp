package com.masstudio.selmy.tmc.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.masstudio.selmy.tmc.POJO.Survey;
import com.masstudio.selmy.tmc.POJO.TableElement;
import com.masstudio.selmy.tmc.POJO.TableRow;
import com.masstudio.selmy.tmc.Utils.Constants;
import com.masstudio.selmy.tmc.Utils.TModels;
import com.masstudio.selmy.tmc.retrofit.ApiClient;
import com.masstudio.selmy.tmc.retrofit.ApiInterface;
import com.masstudio.selmy.tmc.retrofit.Element;
import com.masstudio.selmy.tmc.retrofit.Elements;
import com.masstudio.selmy.tmc.retrofit.MatrixResponse;
import com.masstudio.selmy.tmc.retrofit.Stats;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateDataService extends Service {
    private List<MatrixResponse> dataList = new ArrayList<>();
    private List<String> origins = new ArrayList<>();
    private List<String> destinations = new ArrayList<>();
    private int dataCount = 0;
    private Boolean first = true;
    private MyThread thread;
    private DatabaseReference firebaseDatabase,firebaseDatabaseTable;
    private Handler handler;
    int count;
    private TableRow current,last;
    public UpdateDataService() {
    }

    @Override
    public void onCreate() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("STATS");
        firebaseDatabaseTable = FirebaseDatabase.getInstance().getReference().child("TABLE");
        thread = new MyThread();
        thread.start();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        getData();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public class MyThread extends Thread {
        @Override
        public void run(){
            /*
            Looper.prepare();
            mHandler = new Handler();
            Looper.loop();
            */
            while(true){
                try {
                    Log.d("RetrofitTable","TS");
                    Thread.sleep(15*1000);
                    Log.d("RetrofitTable","TE");
                    final long time = System.currentTimeMillis();
                    if (isFiveMin(time))
                        handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getEstimations(time);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private Boolean isFiveMin(long millis){
            long min, sec;
            sec = (millis % 60000) / 1000;
            min = ((millis - sec) % 3600000) / 60000;
            return min % 5 == 0;
        }

    }
    private void getData(){
        count = 0;
        for (String path : Constants.SEGMENTS_PATHES_ARRAY){
            origins.add(latTOstring(Constants.getStart(path)));
            destinations.add(latTOstring(Constants.getEnd(path)));
            count++;
        }
        //getEstimations();
    }
    private void getEstimations(final long timeStamp) {
        final String origin = origins.get(dataCount);
        String destination = destinations.get(dataCount);
        String time = String.valueOf(timeStamp);
        String Tmodel =  TModels.Best_guess;
        String key = Constants.API_KEY_DIRECTION_MATRIX;
        Map<String, String> query = new HashMap<>();
        query.put("origins", origin);
        query.put("destinations", destination);
        query.put("departure_time", time);
        query.put("traffic_model", Tmodel);
        query.put("key", key);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MatrixResponse> call = apiService.getEstimations(query);
        call.enqueue(new Callback<MatrixResponse>() {
            @Override
            public void onResponse(Call<MatrixResponse> call, Response<MatrixResponse> response) {
                if(response.isSuccessful()){
                    if (first)
                        dataList.add(response.body());
                    else {
                        if (dataCount ==0)
                            dataList.clear();
                        dataList.add(response.body());
                    }
                    dataCount++;
                    Log.d("RetrofitTable", "dataCount = " +dataCount+"dataListSize = "+dataList.size());
                    if (dataCount >= origins.size()){
                        useEstimations(timeStamp);
                        useEstimationsTable();
                    }
                    else {
                        getEstimations(timeStamp);
                    }
                    //Log.d("RetrofitTable", "FAILED2");
                }else{
                    //  TODO : Snacksbar
                    Log.d("RetrofitTable", "FAILED2");
                }
            }

            @Override
            public void onFailure(Call<MatrixResponse> call, Throwable t) {
                Log.d("Retrofit*", "FAILED");
            }
        });
    }
    private void useEstimations( long timeStamp ) {
        int i = 0;
        long rem = timeStamp  % 60000;
        Log.d("STAMP_SERVICE",rem+" "+ timeStamp );
        timeStamp = (timeStamp - rem);
        Log.d("STAMP_SERVICE",rem+" "+ timeStamp );

        final String parent =timestampToString(timeStamp);
        final TableRow tableRow = new TableRow();
        tableRow.setTime(-timeStamp);
        tableRow.setDate(parent);
        for (MatrixResponse mat : dataList){
            List<Elements> rows = mat.getRows();
            if (!rows.isEmpty()) {
                List<Element> row = rows.get(0).getElements();
                Element element = row.get(0);
                Stats distance = element.getDistance();
                Stats duration = element.getDuration();
                Stats traffic = element.getDurationInTraffic();
                final TableElement tElement = new TableElement();
                tElement.setName(Constants.SEGMENTS_NAMES_ARRAY[i]);
                tElement.setValue(""+traffic.getValue());
                tElement.setPath(Constants.SEGMENTS_PATHES_ARRAY[i]);
                tableRow.getElements().add(tElement);
                Log.d("RetrofitTable",i+" "+traffic.getText());
            }
            i++;
            //Log.d("STAMP_SERVICE","current Element = "+parent);
        }
        current = tableRow;
        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(tableRow.getDate())) {
                    Log.d("STAMP_SERVICE","current Element = "+parent);
                    firebaseDatabase.child(parent).setValue(tableRow);
                }
                long lastWeek = lastWeek(-1*tableRow.getTime());
                Log.d("STAMP_SERVICE*","last Element = "+lastWeek);
                final String lastString = timestampToString(lastWeek);
                Log.d("STAMP_SERVICE*","last Element = "+lastString);
                if (snapshot.hasChild(timestampToString(lastWeek))) {
                    Log.d("STAMP_SERVICE*","Exist");
                    firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            TableRow lastWeek = dataSnapshot.child(lastString).getValue(TableRow.class);
                            List<TableElement> elements = lastWeek.getElements();
                            int i =0;
                            for (TableElement tableElement : elements){
                                int thisWeekS = getValue(tableRow.getElements().get(i).getValue());
                                int lastWeekS = getValue(tableElement.getValue());
                                Log.d("STAMP_SERVICE","last = "+lastWeekS +" this = "+thisWeekS);
                                if (thisWeekS > lastWeekS){
                                    Log.d("STAMP_SERVICE","Ref violate = "+i);

                                    DatabaseReference surveyDB = FirebaseDatabase.getInstance().getReference().child("Survey");
                                    DatabaseReference newSurvey = surveyDB.push();
                                    TableElement surveyElement = tableRow.getElements().get(i);
                                    Survey survey =  new Survey(surveyElement.getName(),surveyElement.getPath(),-System.currentTimeMillis(),0,0);
                                    newSurvey.setValue(survey);

                                }
                                i++;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        first = false;
        dataCount = 0;
    }
    public  int getValue(String point){
        String string = point;
        String[] parts = string.split(" ");
        String value = parts[0];
        return Integer.valueOf(value);
    }
    private long lastWeek(Long time) {
        //long week = 1000 * 60 * 60; // last five min
        long week = 1000 * 60 * 60 * 24 * 7;
       // Log.d("STAMP_SERVICE","last Element = "+(time - week));
        return time - week;
    }
    private String latTOstring(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }
    private String timestampToString(long timestamp){
        long hour, min, sec, millis;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1; // jan = 0
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        millis = timestamp;
        sec = (millis % 60000) / 1000;
        hour = (millis % 86400000) / 3600000 + 2;
        min = ((millis - sec) % 3600000) / 60000;
        String date = "{"+mDay+"-"+mMonth+"-"+mYear+"}";
        if ((hour%24 <10)&&min < 10)
            return date + "("+"0" +hour%24+":0"+min+")";
        if (min < 10)
            return date + "("+hour%24+":0"+min+")";
        if (hour%24 < 10)
            return date + "("+"0"+hour%24+":"+min+")";
        return date+"("+hour%24+":"+min+")";
    }
    private void useEstimationsTable() {
        int i = 0;
        for (MatrixResponse mat : dataList){
            List<Elements> rows = mat.getRows();
            if (!rows.isEmpty()) {
                List<Element> row = rows.get(0).getElements();
                Element element = row.get(0);
                Stats distance = element.getDistance();
                Stats duration = element.getDuration();
                Stats traffic = element.getDurationInTraffic();
                TableElement tElement = new TableElement();
                tElement.setName(Constants.SEGMENTS_NAMES_ARRAY[i]);
                tElement.setValue(""+traffic.getValue());
                tElement.setPath(Constants.SEGMENTS_PATHES_ARRAY[i]);
                firebaseDatabaseTable.child((i + 1) + " > " + (i + 2)).setValue(tElement);
                Log.d("RetrofitTable", i + " " + traffic.getText());

            }
            i++;
        }
        first = false;
        dataCount = 0;
        //tableAdapter.notifyDataSetChanged();
        //Log.d("RetrofitTable",i+" "+tableAdapter.data.get(0).getName());
        //tableAdapter.setData(tableElements);
    }

}
