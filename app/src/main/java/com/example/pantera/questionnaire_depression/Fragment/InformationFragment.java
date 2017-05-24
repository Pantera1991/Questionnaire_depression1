package com.example.pantera.questionnaire_depression.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.api.RestApi;
import com.example.pantera.questionnaire_depression.utils.Diagnosis;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pantera on 2016-12-26.
 */

public class InformationFragment extends Fragment {

    private static final String TAG = "SentQuestionFragment";
    private Context mContext;
    private TextView mTvWiki;
    private Button btnWiki;
    private View mProgressView;
    private View mLayoutRecyclerView;
    private View rootView;

    @Override
    public void onStart() {
        super.onStart();
        initWiki();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_information, container, false);
        mContext = rootView.getContext();


        mProgressView = rootView.findViewById(R.id.sent_ques_progress);
        mLayoutRecyclerView = rootView.findViewById(R.id.layout_rv_sent);
        Button buttonInfo = (Button) rootView.findViewById(R.id.btnInfo);
        buttonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

        btnWiki = (Button) rootView.findViewById(R.id.btnWiki);
        mTvWiki = (TextView) rootView.findViewById(R.id.tv_wiki);
        return rootView;
    }

    private void initWiki() {
        showProgress(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://pl.wikipedia.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi service = retrofit.create(RestApi.class);
        service.getWikiDefinition().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String jsonResponse = response.body().string();
                        JSONObject root = new JSONObject(jsonResponse);
                        final JSONObject pages = root.getJSONObject("query").getJSONObject("pages").getJSONObject("25450");
                        mTvWiki.setText(Html.fromHtml("<h2>" + pages.getString("title") + "</h2>" + pages.getString("extract")));
                        btnWiki.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                try {
                                    intent.setData(Uri.parse(pages.getString("fullurl")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }
                        });
                        showProgress(false);
                    } else {
                        Log.d("json", response.message());
                        showProgress(false);
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    showProgress(false);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, t.toString());
                Log.d("callback", call.request().toString());
                showProgress(false);
            }
        });
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
        t1.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((0))), null, null, null);
        t2.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((12))), null, null, null);
        t3.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((27))), null, null, null);
        t4.setCompoundDrawablesRelativeWithIntrinsicBounds(generateShape(Diagnosis.getColor((50))), null, null, null);


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


    private void showProgress(final boolean show) {

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

    }
}
