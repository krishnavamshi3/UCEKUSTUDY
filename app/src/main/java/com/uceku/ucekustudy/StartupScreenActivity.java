package com.uceku.ucekustudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.uceku.ucekustudy.constants.SharedPref;

import java.io.IOException;
import java.io.InputStream;

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

        ImageView imageView = findViewById(R.id.imageView);
        loadImageFromAsset(imageView);

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

    public void loadImageFromAsset(ImageView mImage) {
        try
        {
            // get input stream
            InputStream ims = getAssets().open("feature_graphic.png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            mImage.setImageDrawable(d);
            ims .close();
        }
        catch(IOException ex)
        {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
