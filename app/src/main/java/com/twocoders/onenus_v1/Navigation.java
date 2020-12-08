package com.twocoders.onenus_v1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Navigation extends AppCompatActivity {

    List<Route> routeList = new ArrayList<>();
    CustomAdapter adapter;
    RecyclerView mRecyclerView;
    ProgressDialog pd;
    FirebaseFirestore db;
    ImageView nearestLocBtn, searchButton;
    FusedLocationProviderClient fusedLocationProviderClient;
    AutoCompleteTextView editTextStart, editTextEnd;
    TextView disclaimer;
    LocationRequest locationRequest;
    Location currentLoc;


    private static final String[] BUSSTOPS = new String[]{
            "UHC", "Opp UHC",
            "KR MRT", "Opp KR MRT",
            "UHall", "Opp UHall",
            "Science ( LT 27, S 17 )",
            "YiH", "Opp YiH",
            "Raffles Hall", "Museum",
            "CLB", "IT",
            "Eusoff Hall ( Ventus )", "FASS ( LT 13 )",
            "Temasek Hall ( Opp NUSS )", "AS 5",
            "Biz 2", "Opp HSSML",
            "TCOMS", "Opp TCOMS",
            "PGP Hse 7", "PGP Hse 15",
            "PGP Terminal ( PGP )", "PGP R",
            "The Jap Sch", "EA",
            "Botanic Garden MRT", "College Green",
            "OTH Building",
            "UTown",
            "Kent Vale",
            "KR Bus Terminal",
            "Com 2"
    };

    private static boolean contains(String[] arr, String string) {
        for (String s : arr) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }

    protected String outputGroupName(String member) {
        String[][] groupArray = new String[][]{
                {"UHC", "UHC", "Opp UHC"},
                {"KR MRT", "KR MRT", "Opp KR MRT"},
                {"UHall", "UHall", "Opp UHall"},
                {"Science", "Science ( LT 27, S 17 )"},
                {"YiH", "YiH", "Opp YiH"},
                {"Raffles Hall", "Raffles Hall", "Museum"},
                {"CLB", "CLB", "IT"},
                {"Eusoff/ FASS", "Eusoff Hall ( Ventus )", "FASS ( LT 13 )"},
                {"Temasek", "Temasek Hall ( Opp NUSS )", "AS 5"},
                {"Biz", "Biz 2", "Opp HSSML"},
                {"TCOMS", "TCOMS", "Opp TCOMS"},
                {"PGP Hse", "PGP Hse 7", "PGP Hse 15"},
                {"PGP Terminal", "PGP Terminal ( PGP )", "PGP R"},
                {"Jap Sch", "The Jap Sch", "EA"},
                {"Botanic", "Botanic Garden MRT", "College Green"},
                {"OTH Building", "OTH Building"},
                {"UTown", "UTown"},
                {"KV", "Kent Vale"},
                {"KR Bus Terminal", "KR Bus Terminal"},
                {"Com 2", "Com 2"}
        };

        for (String[] strings : groupArray) {
            if (contains(strings, member)) {
                return strings[0];
            }
        }
        //!!!!
        return "eRrOr";
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        disclaimer = findViewById(R.id.disclaimer_text_view);

        editTextStart = findViewById(R.id.startSearchBar);
        editTextEnd = findViewById(R.id.endSearchBar);
        final ArrayAdapter<String> adapterStart
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, BUSSTOPS);
        final ArrayAdapter<String> adapterEnd
                = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, BUSSTOPS);

        editTextStart.setAdapter(adapterStart);
        editTextEnd.setAdapter(adapterEnd);

        editTextStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextStart.showDropDown();
            }
        });

        editTextEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editTextEnd.showDropDown();
            }
        });

        mRecyclerView = findViewById(R.id.recycleView);
        pd = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();
        searchButton = findViewById(R.id.imageButton);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        ImageView clearBtn = findViewById(R.id.clear_button);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextStart.setText("");
                editTextEnd.setText("");
                clearData();
                disclaimer.setVisibility(View.VISIBLE);
                hideKeyboard(v);
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startText = outputGroupName(editTextStart.getText().toString().trim());
                String endText = outputGroupName(editTextEnd.getText().toString().trim());

                // hides the keyboard after clicking search button
                hideKeyboard(v);
                disclaimer.setVisibility(View.INVISIBLE);
                searchData(startText, endText);
            }
        });

        nearestLocBtn = findViewById(R.id.nearest_location_button);
        nearestLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outputNearestBusStop();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Bundle bundle = getIntent().getExtras();
        performActionFromLocPage(bundle);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            } else {
                for (Location location : locationResult.getLocations()) {
                    currentLoc = location;
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        startLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdate();
    }

    private void stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Navigation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 45);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }



    private void performActionFromLocPage(Bundle bundle) {
        if (bundle != null) {
            LatLng dest = (LatLng) bundle.get("Coordinate");
            assert dest != null;
            final String busStopNearestToDest = BusStops.outputNearest(dest);

            if (ActivityCompat.checkSelfPermission(Navigation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        assert location != null;
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        editTextStart.setText(BusStops.outputNearest(userLocation));

                        editTextEnd.setText(busStopNearestToDest);

                        searchButton.performClick();
                    }
                });
            } else {
                ActivityCompat.requestPermissions(Navigation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        }
    }

    private void outputNearestBusStop() {
        if (ActivityCompat.checkSelfPermission(Navigation.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    Location location = task.getResult();
//                    assert location != null;
//                    LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//                    editTextStart.setText(BusStops.outputNearest(userLocation));
//                }
//            });
            LatLng latLng = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
            editTextStart.setText(BusStops.outputNearest(latLng));
        } else {
            ActivityCompat.requestPermissions(Navigation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void clearData() {
        if(mRecyclerView.getAdapter() != null) {
            ((CustomAdapter) mRecyclerView.getAdapter()).clear();
            Toast.makeText(Navigation.this, "Cleared", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchData(final String startText, String endText) {
        pd.setTitle("Wait la!");
        pd.setMessage("Training your patience in progress...");
        pd.show();

        db.collectionGroup("Route").whereEqualTo("Start", startText)
                .whereEqualTo("End", endText).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                    routeList.clear();


                    for(DocumentSnapshot doc : task.getResult()) {
                        ArrayList<String> routes = (ArrayList<String>) doc.get("Route");
                        ExpandableAdapter myExpandableAdapter = new ExpandableAdapter(routes);

                        //Toast.makeText(Navigation.this, Arrays.toString(routes.toArray()), Toast.LENGTH_SHORT).show();

                        Route route = new Route(doc.getString("Buses"),
                                    doc.getString("Time Taken"), doc.getString("numBusStop"),
                                                           myExpandableAdapter);
                        routeList.add(route);
                    }
                    adapter = new CustomAdapter(routeList);
                    mRecyclerView.setAdapter(adapter);
                }
            });
    }

    private static class BusStops {
        final static double RADIUS = 6378.16;
        final static List<BusStops> arr = new ArrayList<>();
        final static BusStops UHCCoor = new BusStops(new LatLng(1.298944, 103.776102), "UHC");
        final static BusStops OppUHCCoor = new BusStops(new LatLng(1.298778, 103.775603), "Opp UHC");
        final static BusStops KRMRTCoor = new BusStops(new LatLng(1.294818, 103.784464), "KR MRT");
        final static BusStops OppKRMRTCoor = new BusStops(new LatLng(1.294920, 103.784603), "Opp KR MRT");
        final static BusStops UHallCoor = new BusStops(new LatLng(1.297230, 103.778674), "UHall");
        final static BusStops OppUHallCoor = new BusStops(new LatLng(1.297529, 103.778122), "Opp UHall");
        final static BusStops LT27Coor = new BusStops(new LatLng(1.297443, 103.780995), "Science ( LT 27, S 17 )");
        final static BusStops S17Coor = new BusStops(new LatLng(1.297513, 103.781328), "Science ( LT 27, S 17 )");
        final static BusStops YiHCoor = new BusStops(new LatLng(1.298899, 103.774380), "YiH");
        final static BusStops OppYiHCoor = new BusStops(new LatLng(1.298977, 103.774192), "Opp YiH");
        final static BusStops RafflesHallCoor = new BusStops(new LatLng(1.300973, 103.772701), "Raffles Hall");
        final static BusStops MuseumCoor = new BusStops(new LatLng(1.301080, 103.773701), "Museum");
        final static BusStops CLBCoor = new BusStops(new LatLng(1.296599, 103.772513), "CLB");
        final static BusStops ITCoor = new BusStops(new LatLng(1.297208, 103.772698), "IT");
        final static BusStops VentusCoor = new BusStops(new LatLng(1.295362, 103.770610), "Eusoff Hall ( Ventus )");
        final static BusStops LT13Coor = new BusStops(new LatLng(1.294888, 103.770588), "FASS ( LT 13 )");
        final static BusStops AS5Coor = new BusStops(new LatLng(1.293469, 103.771947), "AS 5");
        final static BusStops OppNUSSCoor = new BusStops(new LatLng(1.293289, 103.772483), "Temasek Hall ( Opp NUSS )");
        final static BusStops Biz2Coor = new BusStops(new LatLng(1.293309, 103.775112), "Biz 2");
        final static BusStops OppHSSMLCoor = new BusStops(new LatLng(1.292824, 103.774973), "Opp HSSML");
        final static BusStops TCOMSCoor = new BusStops(new LatLng(1.293763, 103.776446), "TCOMS");
        final static BusStops OppTCOMSCoor = new BusStops(new LatLng(1.293836, 103.776981), "Opp TCOMS");
        final static BusStops PGPHse7Coor = new BusStops(new LatLng(1.293173, 103.777815), "PGP Hse 7");
        final static BusStops PGPHse15Coor = new BusStops(new LatLng(1.293110, 103.777806), "PGP Hse 15");
        final static BusStops PGPCoor = new BusStops(new LatLng(1.291820, 103.780482), "PGP Terminal ( PGP )");
        final static BusStops PGPRCoor = new BusStops(new LatLng(1.291003, 103.781022), "PGP R");
        final static BusStops TheJapSchCoor = new BusStops(new LatLng(1.300760, 103.769922), "The Jap Sch");
        final static BusStops EACoor = new BusStops(new LatLng(1.300599, 103.770171), "EA");
        final static BusStops BGMRTCoor = new BusStops(new LatLng(1.322748, 103.815151), "Botanic Garden MRT");
        final static BusStops CollegeGreenCoor = new BusStops(new LatLng(1.323344, 103.816294), "College Green");
        final static BusStops OTHBuildingCoor = new BusStops(new LatLng(1.319891, 103.817782), "OTH Building");
        final static BusStops UTownCoor = new BusStops(new LatLng(1.303644, 103.774635), "UTown");
        final static BusStops KVCoor = new BusStops(new LatLng(1.302125, 103.769115), "Kent Vale");
        final static BusStops KRBusTerminalCoor = new BusStops(new LatLng(1.294285, 103.769801), "KR Bus Terminal");
        final static BusStops Com2Coor = new BusStops(new LatLng(1.294350, 103.773793), "Com 2");
        final LatLng latLng;
        final String name;

        public BusStops(LatLng latLng, String name) {
            this.latLng = latLng;
            this.name = name;
        }

        private static void initArr() {
            arr.clear();
            arr.add(UHCCoor);
            arr.add(OppUHCCoor);
            arr.add(KRMRTCoor);
            arr.add(OppKRMRTCoor);
            arr.add(UHallCoor);
            arr.add(OppUHallCoor);
            arr.add(LT27Coor);
            arr.add(S17Coor);
            arr.add(YiHCoor);
            arr.add(OppYiHCoor);
            arr.add(RafflesHallCoor);
            arr.add(MuseumCoor);
            arr.add(CLBCoor);
            arr.add(ITCoor);
            arr.add(VentusCoor);
            arr.add(LT13Coor);
            arr.add(AS5Coor);
            arr.add(OppNUSSCoor);
            arr.add(Biz2Coor);
            arr.add(OppHSSMLCoor);
            arr.add(TCOMSCoor);
            arr.add(OppTCOMSCoor);
            arr.add(PGPHse7Coor);
            arr.add(PGPHse15Coor);
            arr.add(PGPCoor);
            arr.add(PGPRCoor);
            arr.add(TheJapSchCoor);
            arr.add(EACoor);
            arr.add(BGMRTCoor);
            arr.add(CollegeGreenCoor);
            arr.add(OTHBuildingCoor);
            arr.add(UTownCoor);
            arr.add(KVCoor);
            arr.add(KRBusTerminalCoor);
            arr.add(Com2Coor);
        }

        private static double Radians(double x) {
            return x * Math.PI / 180;
        }

        public LatLng getLatLng() {
            return latLng;
        }

        public static String outputNearest(LatLng userLocation) {
            initArr();
            double shortest = Double.POSITIVE_INFINITY;
            BusStops nearestBusStop = null;
            double lon1 = userLocation.longitude;
            double lat1 = userLocation.latitude;
            for (BusStops busStops : arr) {
                double lon2 = busStops.getLatLng().longitude;
                double lat2 = busStops.getLatLng().latitude;
                double dlon = Radians(lon2 - lon1);
                double dlat = Radians(lat2 - lat1);

                double a = (Math.sin(dlat / 2) * Math.sin(dlat / 2)) + Math.cos(Radians(lat1)) * Math.cos(Radians(lat2)) * (Math.sin(dlon / 2) * Math.sin(dlon / 2));
                double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double dist = angle * RADIUS;
                if (shortest > dist) {
                    shortest = dist;
                    nearestBusStop = busStops;
                }
            }
            assert nearestBusStop != null;
            return nearestBusStop.getName();
        }

        public String getName() {
            return name;
        }
    }
}
