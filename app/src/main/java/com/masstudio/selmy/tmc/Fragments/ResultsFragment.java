package com.masstudio.selmy.tmc.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.masstudio.selmy.tmc.POJO.Survey;
import com.masstudio.selmy.tmc.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference firebaseDatabase;


    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment getInstance(){
        ResultsFragment myFragment=new ResultsFragment();
        return myFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.table_list);
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Survey");
        firebaseDatabase.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Survey,Holder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Survey,Holder>(
                Survey.class,//PoJo
                R.layout.results_row,//row view
                Holder.class,//Hoder class
                firebaseDatabase
        ) {
            @Override
            protected void populateViewHolder(final Holder holder, final Survey model, int position) {
                holder.segment.setText(model.getSegment());
                Double percentAccident = getPercent(model.getAccident(),(model.getAccident() + model.getSecoundRow()));
                Double percentParking = 1 - percentAccident;
                //holder.accidentV.setText(String.valueOf(percentAccident));
                //holder.parkingV.setText(String.valueOf(percentParking));
                holder.accidentV.setText(String.valueOf(model.getAccident()));
                holder.parkingV.setText(String.valueOf(model.getSecoundRow()));
                holder.rowCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.rowExpandable.toggle();
                    }
                });
            }

        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private Double getPercent(int accident, int i) {
        long factor = (long) Math.pow(10,2);
        Double value = (accident + 0.000001)/i;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        CardView rowCard;
        ExpandableRelativeLayout rowExpandable;
        TextView segment,accidentV,parkingV;
        View view;
        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            rowExpandable= (ExpandableRelativeLayout) view.findViewById(R.id.expandable_row);
            rowCard = (CardView) itemView.findViewById(R.id.card_row);
            segment = (TextView) view.findViewById(R.id.text_row);
            accidentV = (TextView) view.findViewById(R.id.row_accident_text);
            parkingV = (TextView) view.findViewById(R.id.row_secondRow_text);
        }

    }

}
