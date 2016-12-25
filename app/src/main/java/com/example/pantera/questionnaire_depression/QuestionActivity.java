package com.example.pantera.questionnaire_depression;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.pantera.questionnaire_depression.adapter.QuestionAdapter;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RestClient restClient;
    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        restClient = new RestClient(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.question_recycler_view);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(QuestionActivity.this));

        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(QuestionActivity.this);
        //GridLayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(mLayoutManager);
        registerForContextMenu(mRecyclerView);


        mProgressView = findViewById(R.id.question_progress);

        loadData();

    }

    private void loadData(){
        showProgress(true);
        Call<List<Question>> call = restClient.get().questions("becka");

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                Log.d("status question", String.valueOf(response.code()));

                switch (response.code()){
                    case 200:
                        List<Question> questionList = response.body();

                        mAdapter = new QuestionAdapter(questionList, QuestionActivity.this);
                        mRecyclerView.setAdapter(mAdapter);

                        break;
                    case 404:
                        showProgress(false);
                        ServerConnectionLost.returnToLoginActivity(QuestionActivity.this);
                        break;
                }

            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                showProgress(false);
                Log.d("questions error ", t.getMessage());
                ServerConnectionLost.returnToLoginActivity(QuestionActivity.this);
            }
        });
        showProgress(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_done:
                QuestionAdapter adapter = (QuestionAdapter) mAdapter;
                int size = adapter.getItemCount();
                float sum = 0;
                boolean allQuestions = true;
//                for (int i = 0; i < size; i++) {
//                    sum += adapter.getItem(i).getRating();
//                    if (adapter.getItem(i).getRating() == 0) {
//                        allQuestions = false;
//                    }
//                }

//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                final String studentName = sharedPreferences.getString(Constants.USER_NAME, "");
//
//                if (!allQuestions) {
//                    showDialog(studentName,sum);
//                }else{
//                    sendForm(studentName,sum);
//                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}