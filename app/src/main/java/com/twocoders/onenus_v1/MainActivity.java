package com.twocoders.onenus_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button navButton, mapButton, locationButton, backgroundButton;
    ProgressBar weekBar = null;
    TextView weekNum;
    FirebaseFirestore db;
    static LatLng userLocation;
    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initialise fused
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }

        setContentView(R.layout.activity_main);

        //Buttons
        navButton = findViewById(R.id.nav_button);
        mapButton = findViewById(R.id.map_button);
        locationButton = findViewById(R.id.loc_button);
        backgroundButton = findViewById(R.id.main_bg);
        weekBar = findViewById(R.id.week_bar);
        weekNum = findViewById(R.id.week_num);
        db = FirebaseFirestore.getInstance();

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNav();
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLocation();
            }
        });

        // make background not clickable
        backgroundButton.setClickable(false);

        setProgressAndWeekNum();
    }

    private void setProgressAndWeekNum() {
        Calendar currentTime = Calendar.getInstance();
        final int weekOfTheYear = currentTime.get(Calendar.WEEK_OF_YEAR);

        db.collection("Academic Calendar").whereEqualTo("WeekOfYear", weekOfTheYear).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String weekNumber = "";
                        long max = 18;
                        long progress = 0;
                        for (DocumentSnapshot doc : task.getResult()) {
                            weekNumber = doc.getString("Week");
                            max = (long) doc.get("Max");
                            progress = (long) doc.get("index");
                        }

                        if (weekNumber.equals("")) {
                            weekNumber = "Holiday";
                        }

                        weekNum.setText(weekNumber);
                        weekBar.setMax((int) max);
                        weekBar.setProgress((int) progress);
                    }
                });
    }

    public void openNav() {
        Intent intent = new Intent(this, Navigation.class);
        startActivity(intent);
    }

    public void openMap() {
        Intent intent = new Intent(this, MapPage.class);
        startActivity(intent);
    }

    public void openLocation() {
        Intent intent = new Intent(this, LocationPage.class);
        startActivity(intent);
    }
}