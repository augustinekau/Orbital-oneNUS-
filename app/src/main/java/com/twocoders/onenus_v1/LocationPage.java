package com.twocoders.onenus_v1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LocationPage extends AppCompatActivity {

    AutoCompleteTextView locationSearchBar;
    ImageView searchBtn;
    FusedLocationProviderClient fusedLocationProviderClient;
   // static LocationFavouriteStatusDatabase db;
    static ArrayList<com.twocoders.onenus_v1.Location> locationList = new ArrayList<>();
    FirebaseFirestore firebaseFirestore;
    ProgressDialog pd;
//    static LatLng userLocation;

    final static String sharedPrefFavName = "FavouritedLocations";

    static SharedPreferences favouritedLocations;

    private static final String[] LOCATION = new String[]{
            "Office of Admissions/ Financial Aid",
            "Office of Campus Security",
            "Office of Student Affairs",

            "POSB",
            "OCBC",

            "Car Park 1",
            "Car Park 2",
            "Car Park 2A",

            "7-Eleven Convenience Store",
            "Bookhaven",
            "Cheers Convenience Store",

            "Runme Shaw CFA Studios",
            "Education Resource Centre",
            "NUSS Guild House",

            "Faculty of Arts and Social Sciences",
            "Faculty of Dentistry",
            "Faculty of Engineering",

            "Central Square",
            "E2 Halal Cafeteria",
            "Foodclique",

            "University Health Centre",
            "National University Hospital",

            "Engineering Auditorium",
            "Hon Sui Sen Auditorium",
            "Lecture Theatre 1",

            "Central Library",
            "Hon Sui Sen Memorial Library",
            "Medical Library",

            "Eusoff Hall",
            "Kent Ridge Hall",
            "King Edward VII Hall",

            "Cafe Delight",
            "The Tea Party",
            "Waa Cow",

            "Multipurpose Sport Halls (MPHs) and outdoor facilities",
            "Gym (UTown)",
            "Climbing Wall (UTown)",
            "University Sports Centre"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialise shared pref
        favouritedLocations = getSharedPreferences(sharedPrefFavName, MODE_PRIVATE);

        firebaseFirestore = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);

        //initialise locationList thru shared pref
        try {
            initLocationList();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //initialise fused
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        new LocateUser().execute();


        setContentView(R.layout.location);

        locationSearchBar = findViewById(R.id.locationSearchBar);
        searchBtn = findViewById(R.id.loc_search_button);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, LOCATION);
        locationSearchBar.setAdapter(adapter);

        locationSearchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                locationSearchBar.showDropDown();
            }
        });


        if (MainActivity.userLocation == null) {
            MainActivity.userLocation = new LatLng(1.296321, 103.779641);
        }


        clickSearch();
    }


    public void initLocationList() throws ExecutionException, InterruptedException {

        final Map<String, ?> allEntries = favouritedLocations.getAll();

        final ArrayList<String> tempArr = new ArrayList<>();

        locationList.clear();
        if (!allEntries.isEmpty()) {
            pd.show();
            final int lastIndex = getLastIndex();

            for (final Map.Entry<String, ?> entry : allEntries.entrySet()) {
                tempArr.add(entry.getKey());
            }

            firebaseFirestore.collection("Locations").whereIn("Name", tempArr)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            pd.dismiss();
                            LatLng coor = null;
                            com.twocoders.onenus_v1.Location loc;
                            ArrayList<com.twocoders.onenus_v1.Location> tempArr1 = new ArrayList<>();

//                            if (task.getResult().size() > favouritedLocations.getAll().size()) {
//                                double dist = Double.POSITIVE_INFINITY;
//
//                                for (DocumentSnapshot doc : task.getResult()) {
//                                    if (doc.getString("Name") == "POSB") {
//
//                                    }
//                                    LatLng dataCoor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
//
//
//                                }
//                            }
                            double dist = Double.POSITIVE_INFINITY;
                            int index = 0;

                            for (DocumentSnapshot doc : task.getResult()) {
                                if (doc.getString("Name").equals("POSB")) {
                                    LatLng dataCoor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
                                    double distBtwCoorNData = distBtwCoor(MainActivity.userLocation, dataCoor);
                                    if (dist > distBtwCoorNData) {
                                        coor = dataCoor;
                                        dist = distBtwCoorNData;
                                        loc = new com.twocoders.onenus_v1.Location(doc.getString("Name"), true, coor);
                                        if (index <= 0) {
                                            tempArr1.add(loc);
                                            index = tempArr1.size() - 1;
                                        } else {
                                            tempArr1.set(index, loc);
                                        }
                                    }
                                } else {
                                    coor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
                                    loc = new com.twocoders.onenus_v1.Location(doc.getString("Name"), true, coor);
                                    tempArr1.add(loc);
                                }
                            }



                            Map<String, ?> allFavLocations = favouritedLocations.getAll();
                            int start = 1;
                            while (start <= lastIndex) {
                                Loop:
                                for (Map.Entry<String, ?> entry : allFavLocations.entrySet()) {
                                    if (entry.getValue().equals(start)) {
                                        String name = entry.getKey();

                                        for(int i = 0; i < tempArr1.size(); i++) {
                                            com.twocoders.onenus_v1.Location location = tempArr1.get(i);
                                            if (location.getLoc().equals(name)) {
                                                locationList.add(location);
                                                tempArr1.remove(location);
                                                break Loop;
                                            }

                                        }
                                    }
                                }
                                start++;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("LocationList", locationList);
                            Fragment favouriteFragment = new LocationFavouriteFragment();
                            favouriteFragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.location_fragment_container, favouriteFragment).commit();
                        }
                    });
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("LocationList", locationList);
            Fragment favouriteFragment = new LocationFavouriteFragment();
            favouriteFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.location_fragment_container, favouriteFragment).commit();
        }
    }

    public static int getLastIndex() {
        Map<String, ?> allEntries = favouritedLocations.getAll();
        int lastIndex = 0;
        for(Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if ((Integer) entry.getValue() > lastIndex) {
                lastIndex = (Integer) entry.getValue();
            }
        }
        return lastIndex;
    }

    public void hideKeyboard(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    private void clickSearch() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input = locationSearchBar.getText().toString().trim();

                firebaseFirestore.collection("Locations").whereEqualTo("Name", input)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Bundle bundle = new Bundle();
                                boolean favourited = favouritedLocations.contains(input);

                                LatLng coor = null;
                                double dist = Double.POSITIVE_INFINITY;

                                if (task.getResult().size() > 1) {

                                    for(DocumentSnapshot doc : task.getResult()) {
                                        LatLng dataCoor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
                                        double distBtwUserNData = distBtwCoor(MainActivity.userLocation, dataCoor);

                                        if (distBtwUserNData < dist) {
                                            dist = distBtwUserNData;
                                            coor = dataCoor;
                                        }
                                    }

                                } else {

                                    for(DocumentSnapshot doc : task.getResult()) {
                                        coor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
                                    }
                                }

                                com.twocoders.onenus_v1.Location loc = new com.twocoders.onenus_v1.Location(input, favourited, coor);

                                bundle.putParcelable("Location", loc);
                                Fragment locationResultFragment = new LocationResultFragment();
                                locationResultFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.location_fragment_container, locationResultFragment).commit();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LocationPage.this, "fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                hideKeyboard(v);
            }
        });
    }

//    private static LatLng outputCoor(String category, String input) {
//        HashMap<String, LatLng>  administrativeOfficeHashMap = new HashMap<>(),
//                banksATMSHashMap = new HashMap<>(),
//                busStopsHashMap = new HashMap<>(),
//                carParksHashMap = new HashMap<>(),
//                convenienceStoresSupermarketsHashMap = new HashMap<>(),
//                culturalFacilitiesHashMap = new HashMap<>(),
//                facultiesSchoolsHashMap = new HashMap<>(),
//                foodCourtsHashMap = new HashMap<>(),
//                healthCareAmenitiesHashMap = new HashMap<>(),
//                lectureTheatresHashMap = new HashMap<>(),
//                nusLibrariesStudyAreasHashMap = new HashMap<>(),
//                printingServicesHashMap = new HashMap<>(),
//                residentialAreasHashMap = new HashMap<>(),
//                restaurantsCafesHashMap = new HashMap<>(),
//                sportsFacilitiesHashMap = new HashMap<>();
//
//        if(administrativeOfficeHashMap.isEmpty()) {
//            // Administrative Offices
//
//            // Banks/ ATMs (array > hashmap)
//            banksATMSHashMap = new HashMap<>();
//            banksATMSHashMap.put("OCBC Branch @ YIH", new LatLng(1.298620, 103.774986));
//            banksATMSHashMap.put("DBS ATM", new LatLng(1.294439, 103.774055));
//
//            // Bus Stops (only for Map filter)
//
//            // Car Parks
//            carParksHashMap = new HashMap<>();
//            carParksHashMap.put("NUS Car Park 11", new LatLng(1.294402, 103.775136));
//            carParksHashMap.put("NUS Car Park 2A", new LatLng(1.300771, 103.771666));
//
//            // Convenience Stores/ Supermarkets (array > hashmap)
//            convenienceStoresSupermarketsHashMap = new HashMap<>();
//            convenienceStoresSupermarketsHashMap.put("UTown Fairprice Express", new LatLng(1.304863, 103.772552));
//            convenienceStoresSupermarketsHashMap.put("7-11 @ YiH", new LatLng(1.298834, 103.775102));
//
//            // Cultural Facilities
//
//            // Faculties/ Schools
//
//            // Food Courts
//            foodCourtsHashMap = new HashMap<>();
//            foodCourtsHashMap.put("Deck", new LatLng(1.297186, 103.777529));
//            foodCourtsHashMap.put("HELLO", new LatLng(1.304774, 103.772660));
//            foodCourtsHashMap.put("BAAAAAAAAAAAAAAAAAAI", new LatLng(1.298834, 103.775102));
//            // Health Care Amenities
//
//            // Lecture Theatres
//
//            // NUS Libraries/ Study Areas
//
//            // Printing Services
//
//            // Residential Areas
//
//            // Restaurants/ Cafes
//            restaurantsCafesHashMap = new HashMap<>();
//            restaurantsCafesHashMap.put("Swee Heng", new LatLng(1.304774, 103.772660));
//
//            // Sports Facilities
//
//        }
//
//      //   iniHashMap();
//        switch (category) {
////            case "Administrative Offices":
////                break;
//            case "Banks/ ATMs":
////                List<String> locations = convertToSpecificLocation(input);
////                List<LatLng> latLngsList = new ArrayList<>();
////                for (String loc : locations) {
////                    latLngsList.add(banksATMSHashMap.get(loc));
////                }
////                return outputNearest(latLngsList);
//                return banksATMSHashMap.get(input);
////            case "Bus Stops":
////                // EMPTY
////                break;
////            case "Car Parks":
////                // EMPTY
////                break;
////            case "Convenience Stores/ Supermarkets":
////                break;
////            case "Cultural Facilities":
////                break;
////            case "Faculties/ Schools":
////                break;
//            case "Food Courts":
//                return foodCourtsHashMap.get(input);
////            case "Health Care Amenities":
////                break;
////            case "Lecture Theatres":
////                break;
////            case "NUS Libraries/ Study Areas":
////                break;
////            case "Printing Services":
////                break;
////            case "Residential Areas":
////                break;
////            case "Restaurants/ Cafes":
////                break;
////            case "Sports Facilities":
////                break;
//            default:
//                // Coordinate of UTown Green
//                return new LatLng(1.304826, 103.773288);
//        }
//    }



    private static double Radians(double x) {
        return x * Math.PI / 180;
    }

    private double distBtwCoor(LatLng userLocation, LatLng latLng) {
        final double RADIUS = 6378.16;

        double lon1 = userLocation.longitude;
        double lat1 = userLocation.latitude;

        double lon2 = latLng.longitude;
        double lat2 = latLng.latitude;
        double dlon = Radians(lon2 - lon1);
        double dlat = Radians(lat2 - lat1);

        double a = (Math.sin(dlat / 2) * Math.sin(dlat / 2)) + Math.cos(Radians(lat1)) * Math.cos(Radians(lat2)) * (Math.sin(dlon / 2) * Math.sin(dlon / 2));
        double angle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return angle * RADIUS;
    }

    private void locateUser() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Coordinate of UTown Green
        } else {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                assert location != null;
                MainActivity.userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
    }
    }

    private class LocateUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            locateUser();
            return null;
        }
    }

}
