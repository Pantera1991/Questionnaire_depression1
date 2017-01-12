package com.example.pantera.questionnaire_depression;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.pantera.questionnaire_depression.adapter.QuestionAdapter;
import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.utils.ServerConnectionLost;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StarterActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ImageButton mNextBtn;
    private Button mFinishBtn;
    private ImageView[] indicators;
    private RestClient restClient;
    private int page = 0;
    private Patient patient;
    private String cookieValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        //extras
        patient = (Patient) getIntent().getSerializableExtra("patient");
        cookieValue = getIntent().getStringExtra("cookieValue");

        restClient = new RestClient(this);
        ImageView zero = (ImageView) findViewById(R.id.intro_indicator_0);
        ImageView one = (ImageView) findViewById(R.id.intro_indicator_1);

        indicators = new ImageView[]{zero, one};

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNextBtn = (ImageButton) findViewById(R.id.intro_btn_next);
        mFinishBtn = (Button) findViewById(R.id.intro_btn_finish);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(page);
        updateIndicators(page);


        final int color1 = ContextCompat.getColor(this, R.color.colorPrimaryDark1);


        final ArgbEvaluator evaluator = new ArgbEvaluator();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int colorUpdate = (Integer) evaluator.evaluate(positionOffset, color1,
                        color1);
                mViewPager.setBackgroundColor(colorUpdate);
            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                updateIndicators(page);
                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color1);
                        break;

                }
                mNextBtn.setVisibility(position == 1 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment f = getActiveFragment(mViewPager, 1);
                if (f instanceof StartQuestionnaireFragment) {
                    StartQuestionnaireFragment sqf = (StartQuestionnaireFragment) f;
                    QuestionAdapter mAdapter = (QuestionAdapter) sqf.getAdapter();
                    List<Integer> list = mAdapter.checkSelectedAllQuestion();
                    if (list.size() > 0) {
                        String msg = "Nie zaznaczyłeś/aś wszystkich pytań \n" + list.toString().replaceAll("[\\[*\\]]", "");
                        String title = "Nie można wysłać ankiety !";
                        showDialog(msg, title);
                    } else {
                        List<Integer> listAnswers = new ArrayList<>();
                        int size = mAdapter.getItemCount() * 4;
                        float sum = 0;
                        for (int i = 0; i < size; i++) {
                            Question q = mAdapter.getItem(i);
                            if (q.getSelectOption()) {
                                sum += q.getPoints();
                                listAnswers.add(q.getId());
                            }
                        }
                        showSendDialog(sum, listAnswers);
                        Log.d("sum", String.valueOf(sum));
                    }
                }

                //  update 1st time pref
                //Utils.saveSharedSetting(PagerActivity.this, MainActivity.PREF_USER_FIRST_TIME, "false");

            }
        });
    }

    public Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return getSupportFragmentManager().findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    void updateIndicators(int position) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }

    private void showDialog(final String text, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StarterActivity.this);
        builder.setMessage(text)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showSendDialog(final float points, final List<Integer> listAnswers) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Twój wynik: " + (int) points + " punktów")
                .setCancelable(false)
                .setPositiveButton("Wyślij", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new SendTask(StarterActivity.this, points, listAnswers).execute();
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

            mDialog = new ProgressDialog(StarterActivity.this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("Wysyłanie...");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("patientID", String.valueOf(patient.getId()));
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
                SessionManager sessionManager = new SessionManager(StarterActivity.this);
                patient.setStartQuestionnaire(true);
                sessionManager.createLoginSession(patient, cookieValue);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Snackbar.make(mFinishBtn, getResources().getString(R.string.offline_serv), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            mDialog.dismiss();
        }
    }


    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_starter, container, false);
        }
    }

    public static class StartQuestionnaireFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RestClient restClient;
        private Context mContext;

        public static StartQuestionnaireFragment newInstance(int sectionNumber) {
            StartQuestionnaireFragment fragment = new StartQuestionnaireFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_starter_questions, container, false);
            mContext = rootView.getContext();
            restClient = new RestClient(rootView.getContext());
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.starter_questions_recycler_view);
            if (mRecyclerView != null) {
                mRecyclerView.setHasFixedSize(true);
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(mLayoutManager);
            registerForContextMenu(mRecyclerView);
            loadData();
            return rootView;
        }


        private void loadData() {
            //showProgress(true);
            Call<List<Question>> call = restClient.get().questions("hads");

            call.enqueue(new Callback<List<Question>>() {

                @Override
                public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                    Log.d("status question", String.valueOf(response.code()));

                    switch (response.code()) {
                        case 200:
                            List<Question> questionList = response.body();
                            mAdapter = new QuestionAdapter(questionList, mContext);
                            mRecyclerView.setAdapter(mAdapter);
                            //showProgress(false);
                            break;
                        case 404:
                            //showProgress(false);
                            ServerConnectionLost.returnToLoginActivity((Activity) mContext);
                            break;
                    }

                }

                @Override
                public void onFailure(Call<List<Question>> call, Throwable t) {
                    //showProgress(false);
                    Log.d("questions error ", t.getMessage());
                    ServerConnectionLost.returnToLoginActivity((Activity) mContext);
                }
            });

        }

        public RecyclerView.Adapter getAdapter() {
            return mAdapter;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return StartQuestionnaireFragment.newInstance(position + 1);
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
            }
            return null;
        }
    }
}
