package com.example.pantera.questionnaire_depression.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.QuestionnaireApplication;
import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.adapter.AnswerAdapter;
import com.example.pantera.questionnaire_depression.controller.QuestionnaireController;
import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.model.DateResponse;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pantera on 2016-12-26.
 */

public class QuestionnaireFragment extends Fragment {

    private Context mContext;
    private TextView mTvDate, mTvLabel,mTvWaitForClassified;
    private MainActivity mainActivity;
    private CardView cardInfo, cardQuestionnaire;
    private LinearLayout mContentLayout, mWaitingContent;
    private View mProgressView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean disableFab = true;
    private SessionManager sessionManager;
    private QuestionnaireController questionnaireController;

    @Override
    public void onStart() {
        super.onStart();
        questionnaireController.onInit(this);
        questionnaireController.loadQuestionnaire();
    }

    @Override
    public void onStop() {
        super.onStop();
        questionnaireController.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuestionnaireApplication app = ((QuestionnaireApplication) getActivity().getApplication());
        questionnaireController = app.getQuestionnaireController();
        sessionManager = app.getSessionManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_questionnarie, container, false);
        mContext = rootView.getContext();
        mainActivity = (MainActivity) rootView.getContext();
        mTvDate = (TextView) rootView.findViewById(R.id.tvDate);
        mTvLabel = (TextView) rootView.findViewById(R.id.labelText);
        cardInfo = (CardView) rootView.findViewById(R.id.cardInfo);
        cardQuestionnaire = (CardView) rootView.findViewById(R.id.cardQuestionnaire);
        mContentLayout = (LinearLayout) rootView.findViewById(R.id.contentQuest);
        mWaitingContent = (LinearLayout) rootView.findViewById(R.id.waitForClassified);
        mTvWaitForClassified = (TextView) rootView.findViewById(R.id.waitForClassifiedTv);
        mProgressView = rootView.findViewById(R.id.ques_progress);

        //lista wyslanych
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sent_ques_recycler_view);

        if (!disableFab) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean isVisible = true;
                int scrollDist = 0;
                static final float MINIMUM = 25;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (isVisible && scrollDist > MINIMUM) {
                        mainActivity.setVisibilityFab(View.GONE);
                        scrollDist = 0;
                        isVisible = false;
                    } else if (!isVisible && scrollDist < -MINIMUM) {
                        mainActivity.setVisibilityFab(View.VISIBLE);
                        scrollDist = 0;
                        isVisible = true;
                    }
                    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                        scrollDist += dy;
                    }
                }
            });
        }

        //ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;
    }


    public void setupView() {

        String date = sessionManager.getDateLastSendQuestion();
        int classified = sessionManager.getPref().getInt(SessionManager.KEY_CLASSIFIED, 2);
        switch (classified) {
            case 0:
                disableFab = true;
                mainActivity.setVisibilityFab(View.GONE);
                cardQuestionnaire.setVisibility(View.GONE);
                cardInfo.setVisibility(View.GONE);
                mWaitingContent.setVisibility(View.VISIBLE);
                mTvWaitForClassified.setText(R.string.title_discard_classified);
                break;
            case 1:
                DateTime nowTime = new DateTime();
                DateTime dateTime = DateResponse.stringToDateTime(date);
                if(dateTime != null){
                    dateTime = dateTime.plus(Months.ONE);
                }

                if (dateTime == null || nowTime.isAfter(dateTime.getMillis())) {
                    disableFab = false;
                    cardQuestionnaire.setVisibility(View.VISIBLE);
                    cardInfo.setVisibility(View.GONE);
                    mainActivity.setVisibilityFab(View.VISIBLE);
                    mWaitingContent.setVisibility(View.GONE);
                } else {
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-Y");
                    disableFab = true;
                    mTvDate.setText(dtf.print(dateTime));
                    mTvLabel.setText("Nastęone badanie zostanie \naktywowane:");
                    mainActivity.setVisibilityFab(View.GONE);
                    cardQuestionnaire.setVisibility(View.GONE);
                    cardInfo.setVisibility(View.VISIBLE);
                    mWaitingContent.setVisibility(View.GONE);
                }
                break;
            case 2:
                disableFab = true;
                mainActivity.setVisibilityFab(View.GONE);
                cardQuestionnaire.setVisibility(View.GONE);
                cardInfo.setVisibility(View.GONE);
                mWaitingContent.setVisibility(View.VISIBLE);
                mTvWaitForClassified.setText(R.string.title_wait_classified);
                break;
        }

    }


    public void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        mContentLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.SEND_QUESTIONNAIRE_REQUEST) {
            sessionManager.updateValue(SessionManager.KEY_LAST_SEND_QUESTION, data.getStringExtra("date"));
            setupView();
            AnswerAdapter questionAdapter = (AnswerAdapter) mAdapter;
            Answer answer = new Answer();
            answer.setId(data.getIntExtra("idAnswer", 0));
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", new Locale("pl_PL"));
            Date date = null;
            try {
                date = format.parse(data.getStringExtra("date"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            answer.setDate(date);
            answer.setSumOfPoints(data.getIntExtra("points", 0));
            questionAdapter.addItem(answer);
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void successLoadQuestionnaire(List<Answer> list) {
        mAdapter = new AnswerAdapter(list, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showError(String msg) {
        Snackbar.make(mRecyclerView, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
