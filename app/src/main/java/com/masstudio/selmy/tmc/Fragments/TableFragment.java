package com.masstudio.selmy.tmc.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.masstudio.selmy.tmc.Activities.TableMapActivity;
import com.masstudio.selmy.tmc.POJO.Survey;
import com.masstudio.selmy.tmc.POJO.TableElement;
import com.masstudio.selmy.tmc.R;
import com.masstudio.selmy.tmc.Utils.Constants;
import com.masstudio.selmy.tmc.Utils.TModels;
import com.masstudio.selmy.tmc.retrofit.ApiClient;
import com.masstudio.selmy.tmc.retrofit.ApiInterface;
import com.masstudio.selmy.tmc.retrofit.Element;
import com.masstudio.selmy.tmc.retrofit.Elements;
import com.masstudio.selmy.tmc.retrofit.MatrixResponse;
import com.masstudio.selmy.tmc.retrofit.Stats;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TableFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private Handler handler;
    private List<MatrixResponse> dataList = new ArrayList<>();
    private List<String> origins = new ArrayList<>();
    private List<String> destinations = new ArrayList<>();
    private List<TableElement> tableElements;
    private int count = 0;
    private int dataCount = 0;
    private Boolean first = true;
    private MyThread thread;
    private DatabaseReference firebaseDatabase;

    public TableFragment() {
        // Required empty public constructor
    }
    public static TableFragment getInstance(){
        TableFragment myFragment=new TableFragment();
        return myFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        thread = new MyThread();
        thread.start();
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table_list);
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        getData();
        if (thread.isAlive() || thread.isInterrupted())
            Log.d("RetrofitTable","TLive");
        else {
            Log.d("RetrofitTable", "TDeasd");
            thread.start();
        }

        return view;
    }
    private void getData(){
        count = 0;
        for (String path : Constants.SEGMENTS_PATHES_ARRAY){
            origins.add(latTOstring(Constants.getStart(path)));
            destinations.add(latTOstring(Constants.getEnd(path)));
            count++;
        }
        getEstimations();
    }
    private void getEstimations() {
        Log.d("RetrofitTable", "Start of Estimation");
        final String origin = origins.get(dataCount);
        String destination = destinations.get(dataCount);
        String time = String.valueOf(System.currentTimeMillis());
        String Tmodel =  TModels.Pessimistic;
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
                    Log.d("RetrofitTable", "Success");
                    if (first)
                        dataList.add(response.body());
                    else {
                        if (dataCount ==0)
                            dataList.clear();
                        dataList.add(response.body());
                    }
                    dataCount++;
                    Log.d("RetrofitTable", "dataCount = " +dataCount+"dataListSize = "+dataList.size());
                    if (dataCount >= origins.size())
                        useEstimations();
                    else
                        getEstimations();
                    //Log.d("RetrofitTable", "FAILED2");
                }else{
                    //  TODO : Snacksbar
                    //Log.d("Retrofit*", "FAILED2");
                }
            }

            @Override
            public void onFailure(Call<MatrixResponse> call, Throwable t) {
                Log.d("Retrofit*", "FAILED");
            }
        });
    }
    private void useEstimations() {
        tableElements = new ArrayList<>();
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
                if (i >= 4)
                    tElement.setName((i + 2) + " > " + (i + 3));
                else
                    tElement.setName((i + 1) + " > " + (i + 2));
                tElement.setValue(traffic.getText());
                tElement.setPath(Constants.SEGMENTS_PATHES_ARRAY[i]);
                firebaseDatabase.child((i + 1) + " > " + (i + 2)).setValue(tElement);
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

    @Override
    public void onStart() {
        super.onStart();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Table");
        firebaseDatabase.keepSynced(true);
        FirebaseRecyclerAdapter<TableElement,Holder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<TableElement,Holder>(
                TableElement.class,//PoJo
                R.layout.table_row,//row view
                Holder.class,//Hoder class
                firebaseDatabase
        ) {
            @Override
            protected void populateViewHolder(final Holder holder, final TableElement model, int position) {
                holder.segment.setText(model.getName());
                holder.segmentV.setText(model.getValue());
                Log.d("RetrofitTable", ""+model.getName());
                //holder.btnMap(element.getPath());
                //holder.rowExpandable.collapse();
                holder.rowCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.rowExpandable.toggle();
                    }
                });
                holder.btnMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TableMapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("poly",model.getPath());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                holder.btnSurvey.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference surveyDB = FirebaseDatabase.getInstance().getReference().child("Survey");
                        DatabaseReference newSurvey = surveyDB.push();
                        Survey survey =  new Survey(model.getName(),model.getPath(),-System.currentTimeMillis(),0,0);
                        newSurvey.setValue(survey);
                    }
                });
            }

        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    public static class Holder extends RecyclerView.ViewHolder {
        CardView rowCard;
        ExpandableRelativeLayout rowExpandable;
        TextView segment,segmentV;
        ImageButton btnMap,btnSurvey;
        View view;
        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            rowExpandable= (ExpandableRelativeLayout) view.findViewById(R.id.expandable_row);
            rowCard = (CardView) itemView.findViewById(R.id.card_row);
            segment = (TextView) view.findViewById(R.id.text_row);
            segmentV = (TextView) view.findViewById(R.id.value_row);
            btnMap = (ImageButton) view.findViewById(R.id.btn_row_map);
            btnSurvey = (ImageButton) view.findViewById(R.id.btn_row_survey);
        }

    }
    private String latTOstring(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }
    public class MyThread extends Thread {
        public Handler mHandler;

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
                    Thread.sleep(5*60*1000);
                    Log.d("RetrofitTable","TE");
                    handler.postAtFrontOfQueue(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("RetrofitTable", "handler go Estimation");
                            getEstimations();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
