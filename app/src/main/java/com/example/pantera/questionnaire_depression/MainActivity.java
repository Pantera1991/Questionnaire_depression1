package com.example.pantera.questionnaire_depression;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.pantera.questionnaire_depression.fragment.InformationFragment;
import com.example.pantera.questionnaire_depression.fragment.QuestionnaireFragment;
import com.example.pantera.questionnaire_depression.model.Patient;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mHeaderName;
    private TextView mHeaderUsername;
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ankiety do wypełnienia");
        setSupportActionBar(toolbar);

        //init session
        sessionManager = new SessionManager(getApplicationContext());

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationViewBottom = (NavigationView) navigationView.findViewById(R.id.navigation_drawer_bottom);
        navigationViewBottom.setNavigationItemSelectedListener(this);

        //header menu
        View navHeaderView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        mHeaderName = (TextView) navHeaderView.findViewById(R.id.textView_header_name);
        mHeaderUsername = (TextView) navHeaderView.findViewById(R.id.textView__header_username);
        setUserData();


        //init first select
        navigationView.setCheckedItem(R.id.nav_ques_fill);
        navigationView.getMenu().performIdentifierAction(R.id.nav_ques_fill, 0);
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
        String username = "Nazwa użytkownika: " + patient.getUser().getUsername();
        mHeaderName.setText(name);
        mHeaderUsername.setText(username);
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
            //super.onBackPressed();
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
                setVisibilityFab(View.VISIBLE);
                QuestionnaireFragment toFillFragment = new QuestionnaireFragment();
                manager.beginTransaction().replace(R.id.content_main, toFillFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_ques_to_send:
                toolbar.setTitle("Informacje");
                setVisibilityFab(View.GONE);
                InformationFragment toSentFragment = new InformationFragment();
                manager.beginTransaction().replace(R.id.content_main, toSentFragment).commit();
                break;
            case R.id.nav_info_dr:
                Intent intent = new Intent(MainActivity.this, InfoAboutDoctorActivity.class);
                startActivity(intent);
                item.setChecked(false);
                item.setCheckable(false);
                break;
            case R.id.nav_logout:
                sessionManager.logoutUser();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
