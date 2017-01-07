package com.example.pantera.questionnaire_depression;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import com.example.pantera.questionnaire_depression.utils.Diagnosis;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
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

        restClient = new RestClient(this);

        mProgressView = findViewById(R.id.question_progress);

        mRecyclerView = (RecyclerView) findViewById(R.id.question_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(QuestionActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        registerForContextMenu(mRecyclerView);

        loadData();
    }

    private void loadData() {
        showProgress(true);
        Call<List<Question>> call = restClient.get().questions("becka");

        call.enqueue(new Callback<List<Question>>() {

            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                Log.d("status question", String.valueOf(response.code()));

                switch (response.code()) {
                    case 200:
                        List<Question> questionList = response.body();
                        mAdapter = new QuestionAdapter(questionList, QuestionActivity.this);
                        mRecyclerView.setAdapter(mAdapter);
                        showProgress(false);
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
                List<Integer> list = adapter.checkSelectedAllQuestion();
                if (list.size() > 0) {
                    String msg = "Nie zaznaczyłeś/aś wszystkich pytań \n" + list.toString().replaceAll("[\\[*\\]]", "");
                    String title = "Nie można wysłać ankiety !";
                    showDialog(msg, title);
                } else {
                    List<Integer> listAnswers = new ArrayList<>();
                    int size = adapter.getItemCount() * 4;
                    float sum = 0;
                    for (int i = 0; i < size; i++) {
                        Question q = adapter.getItem(i);
                        if (q.getSelectOption()) {
                            sum += q.getPoints();
                            listAnswers.add(q.getId());
                        }
                    }
                    showSendDialog(sum, listAnswers);
                    Log.d("sum", String.valueOf(sum));
                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }

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

    private void showDialog(final String text, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
        builder.setMessage(text)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSendDialog(final float points, final List<Integer> listAnswers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Twój wynik: " + (int) points + " punktów" + "\nDiagnoza: " + Diagnosis.getDiagnose((int) points))
                .setCancelable(false)
                .setPositiveButton("Wyślij", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new SendTask(QuestionActivity.this, points, listAnswers).execute();
                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }



    class SendTask extends AsyncTask<Object, Void, Boolean> {
        Context context;
        ProgressDialog mDialog;
        List<Integer> listAnswers;
        float points;
        private int answerId;

        SendTask(Context context, float points, List<Integer> listAnswers) {
            this.context = context;
            this.listAnswers = listAnswers;
            this.points = points;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(QuestionActivity.this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Wysyłanie...");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {
                SessionManager sessionManager = new SessionManager(QuestionActivity.this);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("patientID", String.valueOf(sessionManager.getUserDetails().getId()));
                JSONArray answers = new JSONArray();
                for (int i = 0; i < listAnswers.size(); i++) {
                    answers.put(i, listAnswers.get(i));
                }
                jsonObject.put("answers", answers);
                Call<ResponseBody> call = restClient.get().sendAnswer(jsonObject);
                Response<ResponseBody> exec = call.execute();
                answerId = Integer.parseInt(exec.body().string());
                return exec.code() == 200;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {

                Intent data = getIntent();
                data.putExtra("idAnswer", answerId);
                data.putExtra("points", (int) points);
                data.putExtra("date", new Date().toString());
                setResult(Activity.RESULT_OK, data);
                finish();
            } else {
                Snackbar.make(mRecyclerView, getResources().getString(R.string.offline_serv), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }


            mDialog.dismiss();
        }
    }
}


