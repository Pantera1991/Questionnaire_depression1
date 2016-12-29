package com.example.pantera.questionnaire_depression.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.model.Answer;
import com.example.pantera.questionnaire_depression.utils.Diagnosis;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pantera on 2016-12-27.
 */

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Answer> listOfAnswer;

    public AnswerAdapter(List<Answer> listOfAnswer, Context mContext) {
        this.mContext = mContext;
        this.listOfAnswer = listOfAnswer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_answer_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Answer answer = listOfAnswer.get(position);
        MyViewHolder myHolder = (MyViewHolder) holder;
        myHolder.diagnosis.setText(mContext.getString(R.string.diagnosis));
        String strPoints = answer.getSumOfPoints()+"p";
        myHolder.colorPoints.setText(strPoints);
        myHolder.colorPoints.setBackground(generateShape(Diagnosis.getColor(answer.getSumOfPoints())));
        myHolder.diagnosisDescription.setText(Diagnosis.getDiagnose(answer.getSumOfPoints()));
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        myHolder.date.setText(dt.format(answer.getDate()));
    }

    @Override
    public int getItemCount() {
        return listOfAnswer == null ? 0 : listOfAnswer.size();
    }

    private Drawable generateShape(int color){
        Drawable drawable = ContextCompat.getDrawable(mContext,R.drawable.circles);
        drawable.setColorFilter(ContextCompat.getColor(mContext,color), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView diagnosis, diagnosisDescription, date, colorPoints;

        MyViewHolder(View itemView) {
            super(itemView);
            diagnosis = (TextView) itemView.findViewById(R.id.diagnosis_textview);
            diagnosisDescription = (TextView) itemView.findViewById(R.id.diagnosis_description_textview);
            date = (TextView) itemView.findViewById(R.id.date_textview);
            colorPoints = (TextView) itemView.findViewById(R.id.color_points);
        }
    }
}
