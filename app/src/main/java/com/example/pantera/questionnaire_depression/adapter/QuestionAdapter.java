package com.example.pantera.questionnaire_depression.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.model.Question;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Pantera on 2016-12-23.
 */

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Question> listOfQuestion;
    private Context mContext;
    private SparseArray<String> groups = new SparseArray<>();
    private Set<String> titlegroup = new LinkedHashSet<>();
    private Patient patient;

    public QuestionAdapter(List<Question> listOfQuestion, Context mContext) {
        this.listOfQuestion = listOfQuestion;
        this.mContext = mContext;
        SessionManager sessionManager = new SessionManager(mContext);
        this.patient = sessionManager.getUserDetails();

        for (int i = 0; i <= getItemCount(); i++) {
            groups.put(i, String.valueOf(((char) (65 + i))));
        }

        if (!patient.isStartQuestionnaire()) {
            for (int i = 0; i < listOfQuestion.size(); i++) {
                titlegroup.add(listOfQuestion.get(i).getGroupOfQuestion().getTitle());
            }
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (patient.isStartQuestionnaire()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_question_item, parent, false);
        return new QuestionViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final QuestionViewHolderItem questionViewHolderItem = (QuestionViewHolderItem) holder;
        String title;

        RadioGroup radioGroup = questionViewHolderItem.radioGroup;
        radioGroup.removeAllViews();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int pos = holder.getAdapterPosition() + 1;
                int indexButton = radioGroup.getCheckedRadioButtonId();
                for (int x = ((pos * 4) - 4); x < (pos * 4); x++) {
                    Question q = listOfQuestion.get(x);
                    if (indexButton == q.getId()) {
                        q.setSelectOption(true);
                        listOfQuestion.set(x, q);
                    } else {
                        q.setSelectOption(false);
                    }
                }
            }
        });

        if (getItemViewType(position) == 0) {
            title = "Pytanie " + (position + 1);
            mContext.setTheme(R.style.MyRadioButton);
        } else {
            title = titlegroup.toArray()[position].toString();
            questionViewHolderItem.cardView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary1));
            questionViewHolderItem.title.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent1));
            mContext.setTheme(R.style.MyRadioButton2);
        }
        questionViewHolderItem.title.setText(title);

        for (Question q : listOfQuestion) {
            if (q.getGroupOfQuestion().getName().equals(groups.get(position))) {
                RadioButton radioButton = new RadioButton(mContext);
                radioButton.setId(q.getId());
                radioButton.setText(q.getName());
                if (getItemViewType(position) == 0) {
                    radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.text));
                } else {
                    radioButton.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                }

                if (q.getSelectOption()) {
                    radioButton.setChecked(true);
                }
                LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 10, 0, 10);
                radioGroup.addView(radioButton, layoutParams);

            }//if
            if (radioGroup.getChildCount() == 4) {
                break;
            }//if
        }//for

    }

    @Override
    public int getItemCount() {
        return listOfQuestion == null ? 0 : (listOfQuestion.size() / 4);
    }

    public Question getItem(int position) {
        return listOfQuestion.get(position);
    }

    public List<Integer> checkSelectedAllQuestion() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            boolean flag = false;
            for (int x = (((i + 1) * 4) - 4); x < ((i + 1) * 4); x++) {
                Question q = getItem(x);
                if (q.getSelectOption()) {
                    flag = true;
                }
            }
            if (!flag) {
                list.add((i + 1));
            }
        }
        return list;
    }

    private static class QuestionViewHolderItem extends RecyclerView.ViewHolder {

        RadioGroup radioGroup;
        TextView title;
        CardView cardView;

        QuestionViewHolderItem(View itemView) {
            super(itemView);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.rv_item_radio_group);
            title = (TextView) itemView.findViewById(R.id.radio_group_title);
            cardView = (CardView) itemView.findViewById(R.id.rv_question_card);
        }
    }
}
