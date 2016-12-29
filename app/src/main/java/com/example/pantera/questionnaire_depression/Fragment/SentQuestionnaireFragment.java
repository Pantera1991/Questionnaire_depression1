package com.example.pantera.questionnaire_depression.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.SimpleDividerItemDecoration;
import com.example.pantera.questionnaire_depression.adapter.AnswerAdapter;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.utils.Diagnosis;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2016-12-26.
 */

public class SentQuestionnaireFragment extends Fragment {

    private static final String TAG = "SentQuestionFragment";
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RestClient restClient;
    private View mProgressView;
    private View mLayoutRecyclerView;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sent_questionnaries, container, false);
        mContext = rootView.getContext();

        restClient = new RestClient(rootView.getContext());
        mProgressView = rootView.findViewById(R.id.sent_ques_progress);
        mLayoutRecyclerView = rootView.findViewById(R.id.layout_rv_sent);
        Button buttonInfo = (Button) rootView.findViewById(R.id.btnInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sent_ques_recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(rootView.getContext()));
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        loadData();

        return rootView;
    }


    private void dialog() {
        LayoutInflater inflater = getLayoutInflater(Bundle.EMPTY);
        View layout = inflater.inflate(R.layout.dialog_info_diagnosis, (ViewGroup) rootView.findViewById(R.id.root_dialog_layout));
        TextView t1 = (TextView) layout.findViewById(R.id.dialog_diagnosis_1);
        TextView t2 = (TextView) layout.findViewById(R.id.dialog_diagnosis_2);
        TextView t3 = (TextView) layout.findViewById(R.id.dialog_diagnosis_3);
        TextView t4 = (TextView) layout.findViewById(R.id.dialog_diagnosis_4);
        t1.setText(Diagnosis.BEZ_DEPRESJI.toString().replace("_", " ").toLowerCase());
        t2.setText(Diagnosis.ŁAGODNA_DEPRESJA.toString().replace("_", " ").toLowerCase());
        t3.setText(Diagnosis.UMIARKOWANIE_CIĘŻKA_DEPRESJA.toString().replace("_", " ").toLowerCase());
        t4.setText(Diagnosis.BARDZO_CIĘŻKA_DEPRESJA.toString().replace("_", " ").toLowerCase());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            t1.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((0))), null, null, null);
            t2.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((12))), null, null, null);
            t3.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((27))), null, null, null);
            t4.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((50))), null, null, null);
        } else {
            t1.setCompoundDrawables(generateShape(Diagnosis.getColor((0))), null, null, null);
            t2.setCompoundDrawables(generateShape(Diagnosis.getColor((12))), null, null, null);
            t3.setCompoundDrawables(generateShape(Diagnosis.getColor((27))), null, null, null);
            t4.setCompoundDrawables(generateShape(Diagnosis.getColor((50))), null, null, null);
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Rodzaje depreji")
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("OK", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Drawable generateShape(int color) {
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.circles);
        drawable.setColorFilter(ContextCompat.getColor(mContext, color), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    private void loadData() {
        showProgress(true);
        SessionManager sessionManager = new SessionManager(mContext);
        int id = sessionManager.getUserDetails().getId();
        Call<List<Answer>> call = restClient.get().getAnswers(id);

        call.enqueue(new Callback<List<Answer>>() {
            @Override
            public void onResponse(Call<List<Answer>> call, Response<List<Answer>> response) {
                Log.d("status", String.valueOf(response.code()));
                mAdapter = new AnswerAdapter(response.body(), mContext);
                mRecyclerView.setAdapter(mAdapter);
                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<Answer>> call, Throwable t) {
                Log.e(TAG, t.toString());
                Log.d("callback", call.request().toString());
                ServerConnectionLost.returnToLoginActivity(getActivity());
                showProgress(false);

            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLayoutRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLayoutRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLayoutRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLayoutRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
