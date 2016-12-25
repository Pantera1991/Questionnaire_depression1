package com.example.pantera.questionnaire_depression.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.R;
import com.example.pantera.questionnaire_depression.model.Question;

import java.util.List;

/**
 * Created by Pantera on 2016-12-23.
 */

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<Question> listOfQuestion;
    private Context mContext;
    private SparseArray<String> groups = new SparseArray<>();

    public QuestionAdapter(List<Question> listOfQuestion, Context mContext) {
        this.listOfQuestion = listOfQuestion;
        this.mContext = mContext;

        for (int i = 0; i <= getItemCount(); i++) {
            groups.put(i, String.valueOf(((char) (65 + i))));
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_question_irem, parent, false);
        return new QuestionViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final QuestionViewHolderItem questionViewHolderItem = (QuestionViewHolderItem) holder;
        String title = "Pytanie " + (position + 1);
        questionViewHolderItem.title.setText(title);
        RadioGroup radioGroup = questionViewHolderItem.radioGroup;
        radioGroup.removeAllViews();
        radioGroup.setTag("Tag");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("adapter pos: ", String.valueOf((holder.getAdapterPosition() + 1)) + " i:" + String.valueOf(i));
                Question q = listOfQuestion.get(i - 1);
                if (q.getSelectOption() == 0) {
                    q.setSelectOption(i);
                    notifyItemRangeChanged(0, getItemCount());
                }

            }
        });

        for (Question q : listOfQuestion) {
            if (q.getGroupOfQuestion().getName().equals(groups.get(position))) {
                RadioButton radioButton = new RadioButton(mContext);
                radioButton.setId(q.getId());
                radioButton.setText(q.getName());
                mContext.setTheme(R.style.AppTheme);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    radioButton.setTextColor(mContext.getResources().getColor(android.R.color.black));
                } else {
                    radioButton.setTextColor(mContext.getResources().getColor(android.R.color.black, null));
                }
                if (q.getSelectOption() != 0) {
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


    private static class QuestionViewHolderItem extends RecyclerView.ViewHolder {

        RadioGroup radioGroup;
        TextView title;

        QuestionViewHolderItem(View itemView) {
            super(itemView);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.rv_item_radio_group);
            title = (TextView) itemView.findViewById(R.id.radio_group_title);
        }
    }
}
