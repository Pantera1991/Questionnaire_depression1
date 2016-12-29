package com.example.pantera.questionnaire_depression.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.example.pantera.questionnaire_depression.model.Question;

import java.util.ArrayList;
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
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int pos = holder.getAdapterPosition() + 1;
                for (int x = ((pos * 4) - 4); x < (pos * 4); x++) {
                    //Log.d("x: ", String.valueOf(x) + "pos it: " + (i - 1));
                    Question q = listOfQuestion.get(x);
                    if (x == (i - 1)){
                        q.setSelectOption(true);
                        //Log.d("true: ",String.valueOf(x));
                    }else {
                        q.setSelectOption(false);
                    }
                }
            }
        });

        for (Question q : listOfQuestion) {
            if (q.getGroupOfQuestion().getName().equals(groups.get(position))) {
                RadioButton radioButton = new RadioButton(mContext);
                radioButton.setId(q.getId());
                radioButton.setText(q.getName());
                radioButton.setTextColor(ContextCompat.getColor(mContext,android.R.color.black));
                mContext.setTheme(R.style.AppTheme);

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

    public List<Integer> checkSelectedAllQuestion(){
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<getItemCount();i++){
            boolean flag = false;
            for (int x = (((i+1) * 4) - 4); x < ((i+1) * 4); x++) {
                if(getItem(x).getSelectOption()){
                    flag = true;
                }
            }
            if (!flag){
                list.add((i+1));
            }
        }
        return list;
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
