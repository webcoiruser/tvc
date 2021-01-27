package com.pickanddrop.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pickanddrop.R;
import com.pickanddrop.fragment.Splash;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;

import java.util.Locale;


public class SplashActivity extends AppCompatActivity implements AppConstants {
    private AppSession appSession;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        context = this;
        appSession = new AppSession(context);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashActivity.this, instanceIdResult -> {
            String  notiificationId = instanceIdResult.getToken();
            appSession.setFCMToken(notiificationId);
            Log.e("newToken", notiificationId);

        });

//        try {
//            getSupportActionBar().hide();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        setLanguage(ENGLISH);

//        Intent intentReset = getIntent();
//        String action = intentReset.getAction();
//        Uri data = intentReset.getData();
//        System.out.println("intent.getData-->" + data);
//        if(data != null) {
//            String uriString = data.toString();
//            appSession.setResetId(uriString);
//        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container_splash, new Splash()).commit();
    }

    public void popFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }

    public void popAllFragment(){
        int count = this.getSupportFragmentManager().getBackStackEntryCount();
        Log.i(getClass().getName(), "fragment count before " + count);
        for (int i = 0; i < count; ++i) {
            this.getSupportFragmentManager().popBackStack();
        }
    }

    public void setLanguage(String language) {
        appSession.setLanguage(language);
//      String languageToLoad1 = language;
        Locale locale1 = new Locale(language);
        Locale.setDefault(locale1);
        Configuration config1 = new Configuration();
        config1.locale = locale1;
        getResources().updateConfiguration(config1, getResources().getDisplayMetrics());
    }

}
