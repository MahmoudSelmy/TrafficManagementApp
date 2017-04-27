package com.masstudio.selmy.tmc.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.masstudio.selmy.tmc.POJO.TableElement;
import com.masstudio.selmy.tmc.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.List;

/**
 * Created by tech lap on 11/04/2017.
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.Holder> {
    private LayoutInflater layoutInflater;
    public List<TableElement> data;

    public TableAdapter(Context context , List<TableElement> data){
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = layoutInflater.inflate(R.layout.table_row, parent, false);
        Holder holder = new Holder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        TableElement element = data.get(position);
        holder.segment.setText(element.getName());
        holder.segmentV.setText(element.getValue());
        Log.d("RetrofitTable", ""+element.getName());
        //holder.btn(element.getPath());
        holder.rowCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.rowExpandable.toggle();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class Holder extends RecyclerView.ViewHolder {
        CardView rowCard;
        ExpandableRelativeLayout rowExpandable;
        TextView segment,segmentV;
        ImageButton btn;
        View view;
        public Holder(View itemView) {
            super(itemView);
            view = itemView;
            rowExpandable= (ExpandableRelativeLayout) view.findViewById(R.id.expandable_row);
            rowCard = (CardView) itemView.findViewById(R.id.card_row);
            segment = (TextView) view.findViewById(R.id.text_row);
            segmentV = (TextView) view.findViewById(R.id.value_row);
            btn = (ImageButton) view.findViewById(R.id.btn_row_map);
        }

    }
}