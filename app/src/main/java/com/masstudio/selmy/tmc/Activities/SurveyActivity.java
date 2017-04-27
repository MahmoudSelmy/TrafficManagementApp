package com.masstudio.selmy.tmc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.masstudio.selmy.tmc.POJO.Survey;
import com.masstudio.selmy.tmc.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SurveyActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private Boolean accident = false;
    private DatabaseReference surveyDB;
    private String key = " ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        surveyDB = FirebaseDatabase.getInstance().getReference().child("Survey");
        key = getIntent().getExtras().getString("KEY");

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.parking_2nd){
                    accident = false;
                }else{
                    accident = true;
                }
                Toast.makeText(SurveyActivity.this,""+ accident, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void vote(View view) {
        final DatabaseReference survey = surveyDB.child(key);
        survey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Survey surveyO = dataSnapshot.getValue(Survey.class);
                if (accident){
                    surveyO.setAccident(surveyO.getAccident() + 1);
                }else {
                    surveyO.setSecoundRow(surveyO.getSecoundRow() + 1);
                }
                survey.setValue(surveyO);
                // TODO: Intent "MainActivity" for Guest
                startActivity(new Intent(SurveyActivity.this,AuthActivity.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
