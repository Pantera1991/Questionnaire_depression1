package com.example.pantera.questionnaire_depression.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Pantera on 2017-01-07.
 */

public class TimePreference extends DialogPreference {
    private int mHour = 15;
    private int mMinute = 0;
    private TimePicker picker = null;

    private static int getHour(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[0]);
    }

    private static int getMinute(String time) {
        String[] pieces = time.split(":");
        return Integer.parseInt(pieces[1]);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.dialogPreferenceStyle);
        setPositiveButtonText("Ustaw");
        setNegativeButtonText("Anuluj");
    }

    public void setTime(int hour, int minute) {
        mHour = hour;
        mMinute = minute;
        String time = toTime(mHour, mMinute);
        persistString(time);
        notifyDependencyChange(shouldDisableDependents());
        notifyChanged();
    }

    private String toTime(int hour, int minute) {
        return String.valueOf(hour) + ":" + String.valueOf(minute);
    }

    private void updateSummary() {
        String time = String.format(Locale.getDefault(),"%02d", mHour) + ":" + String.format(Locale.getDefault(),"%02d", mMinute);
        setSummary(time);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(mHour);
            picker.setMinute(mMinute);
        }else{
            picker.setCurrentHour(mHour);
            picker.setCurrentMinute(mMinute);
        }

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            int currHour = picker.getCurrentHour();
            int currMinute = picker.getCurrentMinute();

            if (!callChangeListener(toTime(currHour, currMinute))) {
                return;
            }

            // persist
            setTime(currHour, currMinute);
            updateSummary();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time;

        if (restorePersistedValue) {
            String DEFAULT_VALUE = "15:00";
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_VALUE);
            } else {
                time = getPersistedString(DEFAULT_VALUE);
            }
        } else {
            time = defaultValue.toString();
        }

        int currHour = getHour(time);
        int currMinute = getMinute(time);
        // need to persist here for default value to work
        setTime(currHour, currMinute);
        updateSummary();
    }

    public static Date toDate(String inTime) {
        try {
            DateFormat inTimeFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
            return inTimeFormat.parse(inTime);
        } catch (ParseException e) {
            return null;
        }
    }


}