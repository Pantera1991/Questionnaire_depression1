package com.example.pantera.questionnaire_depression.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Doctor;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2016-12-27.
 */

public class InfoAboutDrFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info_dr, container, false);
        Context mContext = rootView.getContext();

        //View mInformationView = rootView.findViewById(R.id.informationView);
        //mProgressView = rootView.findViewById(R.id.information_progress);

        final TextView email = (TextView) rootView.findViewById(R.id.tvNumber2);
        final TextView phone = (TextView) rootView.findViewById(R.id.tvNumber1);
        final MainActivity mainActivity = ((MainActivity) getActivity());
        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.smsButton1);
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


        RestClient restClient = new RestClient(rootView.getContext());
        SessionManager sessionManager = new SessionManager(mContext);
        int patientId = sessionManager.getUserDetails().getId();

        Call<Doctor> call = restClient.get().getInformationAboutDoctor(patientId);
        //showProgress(true);
        call.enqueue(new Callback<Doctor>() {
            @Override
            public void onResponse(Call<Doctor> call, Response<Doctor> response) {

                Doctor doctor = response.body();
                String phoneCode = "+48 "+doctor.getPhone();
                phone.setText(phoneCode);
                email.setText(doctor.getUser().getUsername());
                mainActivity.setCollapsingToolbarLayoutTitle("dr. "+doctor.getName()+" "+doctor.getSurname());
                //showProgress(false);
            }

            @Override
            public void onFailure(Call<Doctor> call, Throwable t) {
                Log.d("callback",call.request().toString());
                Log.d("informationError",t.getMessage());
                //showProgress(false);
                ServerConnectionLost.returnToLoginActivity(getActivity());

            }
        });

        return rootView;
    }

}


