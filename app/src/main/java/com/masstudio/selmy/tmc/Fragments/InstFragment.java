package com.masstudio.selmy.tmc.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.masstudio.selmy.tmc.POJO.Instruction;
import com.masstudio.selmy.tmc.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import at.blogc.android.views.ExpandableTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class InstFragment extends Fragment {
    private RecyclerView instsList;
    private DatabaseReference mdatabase;
    private EditText instF;
    private ImageButton sendBtn;
    private FirebaseAuth mAuth;

    public InstFragment() {
        // Required empty public constructor
    }

    public static InstFragment getInstance(){
        InstFragment myFragment=new InstFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_inst, container, false);
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Instructions");
        mdatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        instsList =(RecyclerView)view.findViewById(R.id.instList);
        instsList.setHasFixedSize(false);
        instsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        instF = (EditText)view.findViewById(R.id.inst_input);
        sendBtn = (ImageButton)view.findViewById(R.id.inst_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = instF.getText().toString();
                String uId = mAuth.getCurrentUser().getUid();
                // TODO : remove it
                //String uId ="456588";
                instF.setText("");
                if (!text.isEmpty()) {
                    Instruction instruction = new Instruction(text, uId, -1 * System.currentTimeMillis());
                    mdatabase.push().setValue(instruction);
                }
            }
        });
        // to make the list Vertical
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Instruction,InstViewHoder> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Instruction, InstViewHoder>(
                Instruction.class,//PoJo
                R.layout.inst_row_new,//row view
                InstViewHoder.class,//Hoder class
                mdatabase
        ) {
            @Override
            protected void populateViewHolder(final InstViewHoder viewHolder, final Instruction model, final int position) {
                viewHolder.titleField.setText(model.getText());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.titleField.toggle();
                    }
                });
            }
        };
        instsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class InstViewHoder extends RecyclerView.ViewHolder{

        private View mView;
        private ExpandableTextView titleField;
        public InstViewHoder(View itemView) {
            super(itemView);
            mView=itemView;
            titleField=(ExpandableTextView)mView.findViewById(R.id.inst_text);
        }

    }
}
