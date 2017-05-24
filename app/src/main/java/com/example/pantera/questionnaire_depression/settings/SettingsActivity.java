package com.example.pantera.questionnaire_depression.settings;


import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;

import com.example.pantera.questionnaire_depression.AlarmReceiver;
import com.example.pantera.questionnaire_depression.MainActivity;
import com.example.pantera.questionnaire_depression.R;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingActivity";
    private static DateTime dateNotify;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            String stringValue = value.toString();
            Log.d(TAG + " pref: ", preference.toString());
            if (preference instanceof SwitchPreference) {

                SwitchPreference switchPreference = (SwitchPreference) preference;
                switchPreference.setChecked(Boolean.parseBoolean(stringValue));
                if (!questionnaireIsActive()) {
                    if (Boolean.parseBoolean(stringValue)) {
                        setAlarm(preference, null);
                    } else {
                        closeAlarm(preference);
                    }
                }

            } else if (preference instanceof TimePreference){
                Log.d("TIMEPREF", "AKCJA TIME PREF value: "+stringValue);
                if (!questionnaireIsActive()) {
                    setAlarm(preference, stringValue);
                }
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void closeAlarm(Preference preference) {
        Intent intent = new Intent(preference.getContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(preference.getContext(), MainActivity.ALARM_REQUEST, intent, 0);
        AlarmManager alarmManager = (AlarmManager) preference.getContext().getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static void setAlarm(Preference preference, String timeValue) {
        String timePref;
        if(timeValue == null){
            timePref = PreferenceManager
                    .getDefaultSharedPreferences(preference.getContext())
                    .getString("pref_key_time", "15:00");
        }else {
            timePref = timeValue;
        }


        String[] timeSplit = timePref.split(":");
        if (dateNotify != null) {
            DateTime dateTime = dateNotify
                    .withHourOfDay(Integer.parseInt(timeSplit[0]))
                    .withMinuteOfHour(Integer.parseInt(timeSplit[1]))
                    .withSecondOfMinute(0);
            dateTime = dateTime.plusMonths(1);

            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-Y HH:mm:ss");
            Intent intent = new Intent(preference.getContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(preference.getContext(), MainActivity.ALARM_REQUEST, intent, 0);
            AlarmManager alarmManager = (AlarmManager) preference.getContext().getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.getMillis(), pendingIntent);
            Log.d(TAG + " czas: ", dtf.print(dateTime));
        }

    }


    private static boolean questionnaireIsActive() {
        DateTime nowTime = new DateTime();
        DateTime dateTime = new DateTime(dateNotify);
        dateTime = dateTime.plus(Months.ONE);
        return nowTime.isAfter(dateTime.getMillis());
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), true));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        if(getIntent().getSerializableExtra("date") != null){
            String d = getIntent().getSerializableExtra("date").toString();
            String[] date = d.split("-");
            dateNotify = new DateTime().withDayOfMonth(Integer.parseInt(date[0])).withMonthOfYear(Integer.parseInt(date[1])).withYear(Integer.parseInt(date[2]));
        }


        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NotificationPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_alarm"));
            //bindPreferenceSummaryToValue(findPreference("pref_key_time"));
            findPreference("notifications_alarm").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            findPreference("pref_key_time").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }


}
