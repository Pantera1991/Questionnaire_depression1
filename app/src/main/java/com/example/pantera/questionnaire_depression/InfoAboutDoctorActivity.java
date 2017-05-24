package com.example.pantera.questionnaire_depression;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.controller.DoctorController;
import com.example.pantera.questionnaire_depression.model.Doctor;

public class InfoAboutDoctorActivity extends AppCompatActivity {
    private DoctorController doctorController;
    private TextView email;
    private TextView phone;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View mProgressView;
    private View mContentView;


    @Override
    protected void onStart() {
        super.onStart();
        doctorController.onInit(this);
        doctorController.loadDetails();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doctorController.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_about_doctor);
        doctorController = ((QuestionnaireApplication) getApplication()).getDoctorController();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInfo);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressView = findViewById(R.id.doctor_info__progress);
        mContentView = findViewById(R.id.doctor_info__content);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayoutInfo);
        collapsingToolbarLayout.setTitle("");
        email = (TextView) findViewById(R.id.tvNumber2);
        phone = (TextView) findViewById(R.id.tvNumber1);

        ImageButton imageButton = (ImageButton) findViewById(R.id.smsButton1);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.setData(Uri.parse("sms:" + phone.getText()));
                startActivity(smsIntent);
            }
        });
    }

    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        mContentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void showError(String msg) {
        Snackbar.make(mContentView, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void successLoadDetails(Doctor doctor) {
        String phoneWithCode = "+48 "+doctor.getPhone();
        phone.setText(phoneWithCode);
        email.setText(doctor.getUser().getUsername());
        collapsingToolbarLayout.setTitle("dr. "+doctor.getName()+" "+doctor.getSurname());
    }
}
