package com.example.pantera.questionnaire_depression.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.QuestionnaireApplication;
import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.controller.InformationController;
import com.example.pantera.questionnaire_depression.utils.Diagnosis;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Pantera on 2016-12-26.
 */

public class InformationFragment extends Fragment {

    @BindView(R.id.tv_wiki)
    TextView mTvWiki;
    @BindView(R.id.btnWiki)
    Button btnWiki;
    @BindView(R.id.sent_ques_progress)
    View mProgressView;
    @BindView(R.id.layout_rv_sent)
    View mLayoutRecyclerView;
    private View rootView;

    private Unbinder unbinder;
    private InformationController informationController;
    private MainActivity mainActivity;

    @Override
    public void onStart() {
        super.onStart();
        informationController.onInit(this);
        informationController.initWiki();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = ((MainActivity) getActivity());
        if (mainActivity.getSupportActionBar() != null) {
            mainActivity.getSupportActionBar().setTitle("Informacje");
        }
        informationController = ((QuestionnaireApplication) getActivity().getApplication()).getInformationController();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_information, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        informationController.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnInfo)
    public void dialog() {
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


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rodzaje depreji")
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("OK", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Drawable generateShape(int color) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.circles);
        drawable.setColorFilter(ContextCompat.getColor(getContext(), color), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }


    public void showProgress(final boolean show) {

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

    public void successLoadWiki(String jsonResponse) {

        JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();

        final JsonObject pages = jsonObject.getAsJsonObject("query").getAsJsonObject("pages").getAsJsonObject("25450");
        if (Build.VERSION.SDK_INT >= 24) {
            mTvWiki.setText(Html.fromHtml("<h2>" + pages.get("title").toString() + "</h2>" + pages.get("extract").toString(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            mTvWiki.setText(Html.fromHtml("<h2>" + pages.get("title").toString() + "</h2>" + pages.get("extract").toString()));
        }

        btnWiki.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = pages.get("fullurl").getAsString();
                mainActivity.openBrowser(url);
            }
        });
    }

    public void showError(String msg) {
        showProgress(false);
        Snackbar.make(mLayoutRecyclerView, msg, Snackbar.LENGTH_LONG).show();
    }
}
