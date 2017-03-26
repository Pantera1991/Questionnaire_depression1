package com.example.pantera.questionnaire_depression;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Doctor;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoAboutDoctorActivity extends AppCompatActivity {
    private TextView email;
    private TextView phone;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View mProgressView;
    private View mContentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_about_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInfo);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        initData();
    }

    private void initData(){
        showProgress(true);
        RestClient restClient = new RestClient(InfoAboutDoctorActivity.this);
        SessionManager sessionManager = new SessionManager(InfoAboutDoctorActivity.this);
        int patientId = sessionManager.getUserDetails().getId();

        Call<Doctor> call = restClient.get().getInformationAboutDoctor(patientId);

        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {

                Doctor doctor = response.body();
                String phoneCode = "+48 "+doctor.getPhone();
                phone.setText(phoneCode);
                email.setText(doctor.getUser().getUsername());
                collapsingToolbarLayout.setTitle("dr. "+doctor.getName()+" "+doctor.getSurname());
                showProgress(false);
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
                Log.d("callback",call.request().toString());
                Log.d("informationError",t.getMessage());
                showProgress(false);
                ServerConnectionLost.returnToLoginActivity(InfoAboutDoctorActivity.this);

            }
        });
    }

    private void showProgress(final boolean show) {
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
}
