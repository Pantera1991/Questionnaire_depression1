package com.example.pantera.questionnaire_depression;

import android.animation.ArgbEvaluator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.pantera.questionnaire_depression.controller.StarterController;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class StarterActivity extends AppCompatActivity {

    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.intro_btn_next) ImageButton mNextBtn;
    @BindView(R.id.intro_btn_finish) Button mFinishBtn;
    @BindViews({R.id.intro_indicator_0, R.id.intro_indicator_1}) ImageView[] indicators;
    private int page = 0;
    private StarterController starterController;
    private ProgressDialog mDialog;

    @Override
    protected void onStart() {
        super.onStart();
        starterController.onInit(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        starterController.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        ButterKnife.bind(this);
        QuestionnaireApplication app = ((QuestionnaireApplication) getApplication());
        starterController = app.getStarterController();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
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
    }

    @OnClick(R.id.intro_btn_next)
    public void nextButton(View view){
        page += 1;
        mViewPager.setCurrentItem(page, true);
    }

    @OnClick(R.id.intro_btn_finish)
    public void finishButton(){
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
                        starterController.sendStarterTest(listAnswers);
                    }
                })
                .setNegativeButton("Anuluj", null);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showProgressDialog(){
        mDialog = new ProgressDialog(StarterActivity.this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("Wysyłanie...");
        mDialog.show();
    }

    public void hideProgressDialog(){
        mDialog.dismiss();
        mDialog = null;
    }

    public void showError(final String msg){
        Snackbar.make(mFinishBtn, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void sendOk(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
        @BindView(R.id.starter_questions_recycler_view) RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private SessionManager sessionManager;
        private StarterController starterController;
        private Unbinder unbinder;

        @Override
        public void onStart() {
            super.onStart();
            starterController.onInit(this);
            starterController.loadStartQuestions();
        }

        @Override
        public void onStop() {
            super.onStop();
            starterController.onStop();
        }

        public static StartQuestionnaireFragment newInstance(int sectionNumber) {
            StartQuestionnaireFragment fragment = new StartQuestionnaireFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            QuestionnaireApplication app = ((QuestionnaireApplication)getActivity().getApplication());
            starterController = app.getStarterController();
            sessionManager = app.getSessionManager();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_starter_questions, container, false);
            unbinder = ButterKnife.bind(this,rootView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            registerForContextMenu(mRecyclerView);
            return rootView;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unbinder.unbind();
        }

        public void successLoadData(List<Question> questionList) {
            mAdapter = new QuestionAdapter(questionList, getContext(), sessionManager);
            mRecyclerView.setAdapter(mAdapter);
        }

        public RecyclerView.Adapter getAdapter() {
            return mAdapter;
        }

        public void showError(String msg){
            Snackbar.make(mRecyclerView, msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

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
