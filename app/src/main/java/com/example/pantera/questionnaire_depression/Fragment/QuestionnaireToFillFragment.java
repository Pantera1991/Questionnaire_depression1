package com.example.pantera.questionnaire_depression.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pantera.questionnaire_depression.R;

/**
 * Created by Pantera on 2016-12-26.
 */

public class QuestionnaireToFillFragment extends Fragment {

    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_to_fill_questionnarie, container, false);
        mContext = rootView.getContext();

        return rootView;
    }
}
