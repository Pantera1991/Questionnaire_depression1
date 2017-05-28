package com.example.pantera.questionnaire_depression;

import android.app.Application;

import com.example.pantera.questionnaire_depression.api.RestClient;
import com.example.pantera.questionnaire_depression.api.WikiClient;
import com.example.pantera.questionnaire_depression.controller.DoctorController;
import com.example.pantera.questionnaire_depression.controller.InformationController;
import com.example.pantera.questionnaire_depression.controller.LoginController;
import com.example.pantera.questionnaire_depression.controller.QuestionController;
import com.example.pantera.questionnaire_depression.controller.QuestionnaireController;
import com.example.pantera.questionnaire_depression.controller.StarterController;
import com.example.pantera.questionnaire_depression.utils.SessionManager;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Pantera on 2016-12-22.
 */

public class QuestionnaireApplication extends Application {

    private LoginController loginController;
    private StarterController starterController;
    private QuestionController questionController;
    private DoctorController doctorController;
    private QuestionnaireController questionnaireController;
    private RestClient restClient;
    private SessionManager sessionManager;
    private InformationController informationController;

    @Override
    public void onCreate() {
        super.onCreate();
        //Stetho.initializeWithDefaults(this);
        // Create an InitializerBuilder
//        Stetho.InitializerBuilder initializerBuilder =
//                Stetho.newInitializerBuilder(this);
//        // Enable Chrome DevTools
//        initializerBuilder.enableWebKitInspector(
//                Stetho.defaultInspectorModulesProvider(this)
//        );
//
//        // Enable command line interface
//        initializerBuilder.enableDumpapp(
//                Stetho.defaultDumperPluginsProvider(this)
//        );
//
//        // Use the InitializerBuilder to generate an Initializer
//        Stetho.Initializer initializer = initializerBuilder.build();
//
//        // Initialize Stetho with the Initializer
//        Stetho.initialize(initializer);

        // initInfoCard joda time
        JodaTimeAndroid.init(this);
        WikiClient wikiClient = new WikiClient();
        restClient = new RestClient(this);
        sessionManager = new SessionManager(getSharedPreferences(SessionManager.PREF_NAME, MODE_PRIVATE));
        loginController = new LoginController(restClient, sessionManager);
        starterController = new StarterController(restClient, sessionManager);
        questionController = new QuestionController(restClient, sessionManager);
        doctorController = new DoctorController(restClient, sessionManager);
        questionnaireController = new QuestionnaireController(restClient, sessionManager);
        informationController = new InformationController(wikiClient);
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public StarterController getStarterController() {
        return starterController;
    }

    public QuestionController getQuestionController() {
        return questionController;
    }

    public DoctorController getDoctorController() {
        return doctorController;
    }

    public QuestionnaireController getQuestionnaireController() {
        return questionnaireController;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public InformationController getInformationController() {
        return informationController;
    }

    public RestClient getRestClient() {
        return restClient;
    }
}
