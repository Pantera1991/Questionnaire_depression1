package com.example.pantera.questionnaire_depression;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.pantera.questionnaire_depression.controller.QuestionController;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.utils.Diagnosis;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private View mProgressView;
    private SessionManager sessionManager;
    private QuestionController questionController;
    private ProgressDialog mDialog;

    @Override
    protected void onStart() {
        super.onStart();
        questionController.onInit(this);
        questionController.loadQuestions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        questionController.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        QuestionnaireApplication app = ((QuestionnaireApplication) getApplication());
        questionController = app.getQuestionController();
        sessionManager = app.getSessionManager();

        mProgressView = findViewById(R.id.question_progress);
        mRecyclerView = (RecyclerView) findViewById(R.id.question_recycler_view);
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(QuestionActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        registerForContextMenu(mRecyclerView);
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

    public void loadQuestionsSuccess(List<Question> questionList){
        mAdapter = new QuestionAdapter(questionList, QuestionActivity.this, sessionManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void showProgress(final boolean show) {
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

    }

    public void showProgressDialog(){
        mDialog = new ProgressDialog(QuestionActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Wysyłanie...");
        mDialog.show();
    }

    public void hideProgressDialog(){
        mDialog.dismiss();
        mDialog = null;
    }

    public void showDialog(final String text, String title) {
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
                        questionController.sendQuestionnaire(listAnswers, points);
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

    public void showError(String msg) {
        Snackbar.make(mRecyclerView, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void successSendQuestionnaire(int answerId, float points){
        Intent data = getIntent();
        data.putExtra("idAnswer", answerId);
        data.putExtra("points", (int) points);
        Format formatter = new SimpleDateFormat("dd-MM-yyyy", new Locale("pl_PL"));
        data.putExtra("date", formatter.format(new Date()));
        setResult(Activity.RESULT_OK, data);
        finish();
    }

}


