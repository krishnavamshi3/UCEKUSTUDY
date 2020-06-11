package com.uceku.ucekustudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.uceku.ucekustudy.constants.SharedPref;

public class StartupScreenActivity extends AppCompatActivity {
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_screen);

        handler = new Handler();
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPref = getSharedPreferences(SharedPref.getSharedPrefFileName(), Context.MODE_PRIVATE);
                int selectedDepartementId = sharedPref.getInt(SharedPref.spKeyLastSelectedDepartmentId, -1);
                int selectedSemesterId = sharedPref.getInt(SharedPref.spKeyLastSelectedSemesterId, -1);
                if (selectedDepartementId == -1) {
                    Routes.routeMainActivity(StartupScreenActivity.this);
                } else {
                    Routes.routeSelectedBranchActivity(StartupScreenActivity.this, selectedDepartementId, selectedSemesterId);
                }

                StartupScreenActivity.this.finish();
            }
        }, 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
