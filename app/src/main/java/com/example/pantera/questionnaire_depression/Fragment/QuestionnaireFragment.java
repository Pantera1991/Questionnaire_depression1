package com.example.pantera.questionnaire_depression.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.adapter.AnswerAdapter;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Pantera on 2016-12-26.
 */

public class QuestionnaireFragment extends Fragment {

    private static final String TAG = "QuestionnaireFrag";
    private Context mContext;
    private TextView mTvDate;
    private MainActivity mainActivity;
    private CardView cardInfo, cardQuestionnaire;
    private LinearLayout mContentLayout;
    private View mProgressView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RestClient restClient;
    private boolean disableFab = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_questionnarie, container, false);
        mContext = rootView.getContext();
        mainActivity = (MainActivity) rootView.getContext();
        mTvDate = (TextView) rootView.findViewById(R.id.tvDate);
        cardInfo = (CardView) rootView.findViewById(R.id.cardInfo);
        cardQuestionnaire = (CardView) rootView.findViewById(R.id.cardQuestionnaire);
        mContentLayout = (LinearLayout) rootView.findViewById(R.id.contentQuest);
        mProgressView = rootView.findViewById(R.id.ques_progress);

        //lista wyslanych
        restClient = new RestClient(rootView.getContext());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sent_ques_recycler_view);
        //mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(rootView.getContext()));
        if(!disableFab){
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
                    }
                    else if (!isVisible && scrollDist < -MINIMUM) {
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
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        loadData();
        return rootView;
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
                initInfoCard();
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

    public void initInfoCard() {

        final SessionManager sessionManager = new SessionManager(mContext);
        RestClient restClient = new RestClient(mContext);
        Call<Date> dateCall = restClient.get().getDateLastSendAnswer(sessionManager.getUserDetails().getId());
        dateCall.enqueue(new Callback<Date>() {
            @Override
            public void onResponse(Call<Date> call, Response<Date> response) {
                if (response.code() == 200) {
                    Date date = response.body();
                    setupView(date);
                }

            }

            @Override
            public void onFailure(Call<Date> call, Throwable t) {
                Log.e(TAG, t.toString());
                Log.d("callback", call.request().toString());
                ServerConnectionLost.returnToLoginActivity(getActivity());
            }
        });
    }

    public void setupView(Date date) {
        DateTime nowTime = new DateTime();
        DateTime dateTime = new DateTime(date);
        dateTime = dateTime.plus(Months.ONE);
        if (nowTime.isAfter(dateTime.getMillis())) {
            disableFab = false;
            cardQuestionnaire.setVisibility(View.VISIBLE);
            cardInfo.setVisibility(View.GONE);
            mainActivity.setVisibilityFab(View.VISIBLE);
        } else {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-Y");
            disableFab = true;
            mTvDate.setText(dtf.print(dateTime));
            mainActivity.setVisibilityFab(View.GONE);
            cardQuestionnaire.setVisibility(View.GONE);
            cardInfo.setVisibility(View.VISIBLE);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mContentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivity.SEND_QUESTIONNAIRE_REQUEST){
            initInfoCard();
            AnswerAdapter questionAdapter = (AnswerAdapter) mAdapter;
            Answer answer = new Answer();
            answer.setId(data.getIntExtra("idAnswer",0));
            answer.setDate(new Date(data.getStringExtra("date")));
            answer.setSumOfPoints(data.getIntExtra("points", 0));
            questionAdapter.addItem(answer);
            mRecyclerView.smoothScrollToPosition(0);
        }
    }
}
