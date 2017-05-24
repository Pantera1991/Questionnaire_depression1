package com.example.pantera.questionnaire_depression;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.fragment.InformationFragment;
import com.example.pantera.questionnaire_depression.fragment.QuestionnaireFragment;
import com.example.pantera.questionnaire_depression.model.DateResponse;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.settings.SettingsActivity;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int SEND_QUESTIONNAIRE_REQUEST = 1;
    public static final int ALARM_REQUEST = 1240;
    private TextView mHeaderName;
    private TextView mHeaderUsername;
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuestionnaireApplication app = ((QuestionnaireApplication) getApplication());
        sessionManager = app.getSessionManager();

        if(!sessionManager.isLoggedIn()){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if(!sessionManager.isStartTest()){
            startActivity(new Intent(this, StarterActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ankiety do wypełnienia");
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, QuestionActivity.class);
                startActivityForResult(i, SEND_QUESTIONNAIRE_REQUEST);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationViewBottom = (NavigationView) navigationView.findViewById(R.id.navigation_drawer_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);

        //header menu
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        mHeaderName = (TextView) navHeaderView.findViewById(R.id.textView_header_name);
        mHeaderUsername = (TextView) navHeaderView.findViewById(R.id.textView__header_username);
        setUserData();


        //initInfoCard first select
        navigationView.setCheckedItem(R.id.nav_ques_fill);
        navigationView.getMenu().performIdentifierAction(R.id.nav_ques_fill, 0);

        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d("MAIN", refreshedToken);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                new Intent("com.example.pantera.questionnaire_depression"),
                PendingIntent.FLAG_NO_CREATE) != null);
        String dateNotify = sessionManager.getDateLastSendQuestion();
        DateTime nowTime = new DateTime();
        DateTime dateTimeCheck = DateResponse.stringToDateTime(dateNotify);

        if (dateTimeCheck != null) {
            dateTimeCheck = dateTimeCheck.plus(Months.ONE);
            if (!alarmUp && !nowTime.isAfter(dateTimeCheck.getMillis())) {
                buildNotification(false);
            }
        }

    }


    public void setVisibilityFab(int visibility) {
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            fab.hide();
        } else {
            fab.show();
        }
    }


    private void setUserData() {
        Patient patient = sessionManager.getUserDetails();
        String name = patient.getName() + " " + patient.getSurname();
        String username = patient.getUser().getUsername();
        mHeaderName.setText(name);
        mHeaderUsername.setText(username);
    }

    private void buildNotification(boolean now) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enableNotify = preferences.getBoolean("notifications_alarm", false);

        if (enableNotify) {
            String timePref = preferences.getString("pref_key_time", null);
            String dateNotify = sessionManager.getDateLastSendQuestion();

            if (timePref != null && dateNotify != null) {
                String[] timeSplit = timePref.split(":");
                DateTime dateTime;
                if (now) {
                    dateTime = new DateTime().withHourOfDay(Integer.parseInt(timeSplit[0])).withMinuteOfHour(Integer.parseInt(timeSplit[1])).withSecondOfMinute(0);
                } else {
                    dateTime = DateResponse.stringToDateTime(dateNotify).withHourOfDay(Integer.parseInt(timeSplit[0])).withMinuteOfHour(Integer.parseInt(timeSplit[1])).withSecondOfMinute(0).plusMonths(1);
                }
                DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-Y HH:mm:ss");
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateTime.getMillis(), pendingIntent);
                Log.d("czas: ", dtf.print(dateTime));
            }

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                finish();
            }
        }

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager manager = getSupportFragmentManager();
        switch (id) {
            case R.id.nav_ques_fill:
                toolbar.setTitle("Ankiety");
                QuestionnaireFragment toFillFragment = new QuestionnaireFragment();
                manager.beginTransaction()
                        .add(toFillFragment, "Ankiety")
                        .replace(R.id.content_main, toFillFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_ques_to_send:
                toolbar.setTitle("Informacje");
                setVisibilityFab(View.GONE);
                InformationFragment toSentFragment = new InformationFragment();
                manager.beginTransaction()
                        .add(toSentFragment, "Informacje")
                        .replace(R.id.content_main, toSentFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_info_dr:
                Intent intent = new Intent(MainActivity.this, InfoAboutDoctorActivity.class);
                startActivity(intent);
                item.setChecked(false);
                item.setCheckable(false);
                break;
            case R.id.nav_logout:

                Intent intentAlarm = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST, intentAlarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

                sessionManager.logoutUser();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_settings:
                String dateNotify = sessionManager.getDateLastSendQuestion();
                Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                settings.putExtra("date", dateNotify);
                startActivity(settings);
                item.setChecked(false);
                item.setCheckable(false);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SEND_QUESTIONNAIRE_REQUEST) {
                buildNotification(true);
                FragmentManager manager = getSupportFragmentManager();
                Fragment fragment = manager.findFragmentByTag("Ankiety");
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
