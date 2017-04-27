package com.masstudio.selmy.tmc.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.masstudio.selmy.tmc.POJO.Survey;
import com.masstudio.selmy.tmc.POJO.TableElement;
import com.masstudio.selmy.tmc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SegmentDetailsActivity extends AppCompatActivity {
    private int sId;
    private String key;
    private DatabaseReference firebaseDatabaseTable;
    private TextView segName,segTraffic;
    private Button segButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_details);
        sId = getIntent().getExtras().getInt("SID");
        key = (sId + 1) + " > " + (sId + 2);
        firebaseDatabaseTable = FirebaseDatabase.getInstance().getReference().child("TABLE");
    }

    @Override
    protected void onStart() {
        firebaseDatabaseTable.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TableElement data = dataSnapshot.getValue(TableElement.class);
                initViews();
                populateViews(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        super.onStart();
    }

    private void initViews() {
        segName = (TextView) findViewById(R.id.seg_name);
        segTraffic = (TextView) findViewById(R.id.seg_value);
        segButton = (Button) findViewById(R.id.seg_btn);
    }

    private void populateViews(final TableElement data) {
        segName.setText(data.getName());
        segTraffic.setText(data.getValue());
        segButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference surveyDB = FirebaseDatabase.getInstance().getReference().child("Survey");
                DatabaseReference newSurvey = surveyDB.push();
                Survey survey =  new Survey(data.getName(),data.getPath(),-System.currentTimeMillis(),0,0);
                newSurvey.setValue(survey);
            }
        });
    }
}
