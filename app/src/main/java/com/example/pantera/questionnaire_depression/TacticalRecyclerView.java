package com.example.pantera.questionnaire_depression;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Pantera on 2017-05-25.
 */

public class TacticalRecyclerView extends RecyclerView {

    private View lostConnectionView;
    private View errorServerView;

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            Adapter adapter = getAdapter();
            boolean hasData = adapter.getItemCount() > 0;
            setVisibility(hasData ? VISIBLE : GONE);
            //emptyView.setVisibility(hasData ? GONE : VISIBLE);
        }
    };

    public TacticalRecyclerView(Context context) {
        super(context);
    }

    public TacticalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TacticalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
    }


}
