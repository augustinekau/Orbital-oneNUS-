package com.twocoders.onenus_v1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.collect.MapMaker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.FormatFlagsConversionMismatchException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

// TEMPLATE IS HERE --------------------------------------------
//switch (category) {
//        case "Administrative Offices":
//        break;
//        case "Banks/ ATMs":
//        break;
//        case "Bus Stops":
//        break;
//        case "Car Parks":
//        break;
//        case "Convenience Stores/ Supermarkets":
//        break;
//        case "Cultural Facilities":
//        break;
//        case "Faculties/ Schools":
//        break;
//        case "Food Courts":
//        break;
//        case "Health Care Amenities":
//        break;
//        case "Lecture Theatres":
//        break;
//        case "NUS Libraries/ Study Areas":
//        break;
//        case "Printing Services":
//        break;
//        case "Residential Areas":
//        break;
//        case "Restaurants/ Cafes":
//        break;
//        case "Sports Facilities":
//        break;
//default:
//        break;
//        }

public class MapPage extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    BottomSheetBehavior bottomSheetBehavior;
    ConstraintLayout bottomSheetConst;
    SupportMapFragment supportMapFragment;
    Switch administrativeOfficeFilter, banksATMsFilter,
            busStopsFilter, carParksFilter,
            convenienceStoresSupermarketsFilter, culturalFacilitiesFilter,
            facultiesSchoolsFilter, foodCourtsFilter,
            healthCareAmenitiesFilter, lectureTheatresFilter,
            nusLibrariesStudyAreasFilter, printingServicesFilter,
            residentialAreasFilter, restaurantsCafesFilter,
            sportsFacilitiesFilter;
    List<String> currentFilterState = new ArrayList<>();
    FirebaseFirestore db;
    Bundle bundle;
    private final static int MY_PERMISSION_FINE_LOCATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(R.style.MyMapTheme);
        setContentView(R.layout.map);

        //initialise firebase
        db = FirebaseFirestore.getInstance();

        //initialise bottom sheet
        bottomSheetConst = findViewById(R.id.bottom_sheet_filter);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetConst);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        //initialise map
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        initSwitches();
        onCheckedListener();

        bundle = getIntent().getExtras();


    }

    private void performActionFromLoc(final Bundle bundle) {
        if (bundle != null) {
            map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    Location location = (Location) bundle.get("Location");
                    assert location != null;

                    String name = location.getLoc();
                    LatLng coor = location.getCoor();

                    final Marker marker = map.addMarker(new MarkerOptions().title(name).icon(bitmapDescriptorFromAsset(getApplicationContext(), R.drawable.pick_heart_shape_foreground, "0o0o")).position(coor));
                    marker.showInfoWindow();
                    db.collection("Locations").whereEqualTo("Name", marker.getTitle()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    String name = "", desc = "", url = "";
//                                    for(DocumentSnapshot doc : task.getResult()) {
//                                        name = doc.getString("Name");
//                                        desc = doc.getString("Desc");
//                                        url = doc.getString("url");
//                                    }
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("Name", name);
//                                    bundle.putString("Desc", desc);
//                                    bundle.putString("url", url);
//
//                                    Fragment fragment = new MapCardDetailsFragment();
//                                    fragment.setArguments(bundle);
//                                    getSupportFragmentManager().beginTransaction().replace(R.id.map_card_details_container, fragment).commit();
                                    String name = "", desc = "", url = "";

                                    if (task.getResult().size() > 1) {
                                        LatLng markerCoor = marker.getPosition();
                                        for(DocumentSnapshot doc : task.getResult()) {
                                            LatLng dataCoor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
                                            if (markerCoor.latitude == dataCoor.latitude && markerCoor.longitude == dataCoor.longitude) {
                                                name = doc.getString("Name");
                                                desc = doc.getString("Desc");
                                                url = doc.getString("url");
                                            }
                                        }
                                    } else {
                                        for(DocumentSnapshot doc : task.getResult()) {
                                            name = doc.getString("Name");
                                            desc = doc.getString("Desc");
                                            url = doc.getString("url");
                                        }
                                    }
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Name", name);
                                    bundle.putString("Desc", desc);
                                    bundle.putString("url", url);
                                    bundle.putParcelable("Coordinate", marker.getPosition());
                                    Fragment fragment = new MapCardDetailsFragment();
                                    fragment.setArguments(bundle);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.map_card_details_container, fragment).commit();
                                }
                            });
                }
            });
        }
    }


    private void initSwitches() {
        administrativeOfficeFilter = findViewById(R.id.filter_Administrative_Offices);
        banksATMsFilter = findViewById(R.id.filter_Banks_ATMs);
        busStopsFilter = findViewById(R.id.filter_Bus_Stops);
        carParksFilter = findViewById(R.id.filter_Car_Parks);
        convenienceStoresSupermarketsFilter = findViewById(R.id.filter_Convenience_Store_Supermarkets);
        culturalFacilitiesFilter = findViewById(R.id.filter_Cultural_Facilities);
        facultiesSchoolsFilter = findViewById(R.id.filter_Faculties_Schools);
        foodCourtsFilter = findViewById(R.id.filter_Food_Courts);
        healthCareAmenitiesFilter = findViewById(R.id.filter_Health_Care_Amenities);
        lectureTheatresFilter = findViewById(R.id.filter_Lecture_Theatres);
        nusLibrariesStudyAreasFilter = findViewById(R.id.filter_NUS_Libraries_Study_Areas);
        printingServicesFilter = findViewById(R.id.filter_Printing_Services);
        residentialAreasFilter = findViewById(R.id.filter_Residential_Areas);
        restaurantsCafesFilter = findViewById(R.id.filter_Restaurants_Cafes);
        sportsFacilitiesFilter = findViewById(R.id.filter_Sports_Facilities);
    }

    private final HashMap<String, ArrayList<Marker>> markerMap = new HashMap<>();

    private void onCheckedListener() {
        administrativeOfficeFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) administrativeOfficeFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        banksATMsFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) banksATMsFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        busStopsFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) busStopsFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        carParksFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) carParksFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        convenienceStoresSupermarketsFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) convenienceStoresSupermarketsFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        culturalFacilitiesFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) culturalFacilitiesFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        facultiesSchoolsFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) facultiesSchoolsFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        foodCourtsFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) foodCourtsFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        healthCareAmenitiesFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) healthCareAmenitiesFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        lectureTheatresFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) lectureTheatresFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        nusLibrariesStudyAreasFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) nusLibrariesStudyAreasFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        printingServicesFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) printingServicesFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        residentialAreasFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) residentialAreasFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        restaurantsCafesFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) restaurantsCafesFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
        sportsFacilitiesFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String str = replaceStr((String) sportsFacilitiesFilter.getText());
                
                if (isChecked) {
                    currentFilterState.add(str);
                    markerMap.put(str, new ArrayList<Marker>());
                    showAllMarkers(currentFilterState);
                } else {
                    currentFilterState.remove(str);
                    ArrayList<Marker> arr = markerMap.get(str);
                    assert arr != null;
                    for (Marker marker : arr) {
                        marker.remove();
                    }
                }
            }
        });
    }

    private String replaceStr(String str) {
        if (str.contains("\n")) {
            str = str.replace("\n", "_n");
        }
        if (str.contains("/")) {
            str = str.replace("/", "_");
        }
        return str;
    }

    private void showAllMarkers(List<String> currentFilterState) {
        for (final String str : currentFilterState) {
            db.collection("Locations").whereEqualTo("Cat", str).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                LatLng coor = new LatLng(doc.getDouble("lat"),
                                        doc.getDouble("lng"));

                                Marker marker = map.addMarker(new MarkerOptions()
                                        .title(doc.getString("Name"))
                                        .icon(bitmapDescriptorFromAsset(getApplicationContext(), R.drawable.xin_map_pointer_icon_foreground, str))
                                        .position(coor));
                                ArrayList<Marker> arr = markerMap.get(str);
                                assert arr != null;
                                arr.add(marker);
                                markerMap.put(str, arr);
                            }
                        }
                    });
        }
    }

    private BitmapDescriptor bitmapDescriptorFromAsset(Context context, int assetId, String cat) {
        Drawable drawable = new ScaleDrawable(ContextCompat.getDrawable(context, assetId), 0, 50, 50).getDrawable();
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        switch (cat) {
            case "Administrative Offices":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(230, 25, 75));
                break;
            case "Banks_ ATMs":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(128, 128, 0));
                break;
            case "Bus Stops":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(255, 255, 25));
                break;
            case "Car Parks":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(0, 130, 200));
                break;
            case "Convenience Stores_ _nSupermarkets":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(245, 130, 48));
                break;
            case "Cultural Facilities":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(145, 30, 180));
                break;
            case "Faculties_ Schools":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(70, 240, 240));
                break;
            case "Food Courts":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(210, 245, 60));
                break;
            case "Health Care Amenities":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(250, 190, 212));
                break;
            case "Lecture Theatres":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(0, 128, 128));
                break;
            case "NUS Libraries":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(220, 190, 255));
                break;
            case "Printing Services":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(170, 110, 40));
                break;
            case "Residential Areas":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(120, 0, 0));
                break;
            case "Restaurants_ Cafes":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(170, 255, 195));
                break;
            case "Sports Facilities":
                DrawableCompat.setTint(wrappedDrawable, Color.rgb(0, 0, 128));
                break;
            default:
                break;
        }
        drawable.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100,
                100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng btmLeft = new LatLng(1.292939, 103.768466);
        LatLng topRight = new LatLng(1.304825, 103.785917);
        LatLngBounds nusBounds = new LatLngBounds(btmLeft, topRight);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(nusBounds, 1500, 1500, 1));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        } else {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setPadding(0,150,0,120);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return false;

            }
        });

        performActionFromLoc(bundle);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                db.collection("Locations").whereEqualTo("Name", marker.getTitle()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String name = "", desc = "", url = "";

                                if (task.getResult().size() > 1) {
                                    LatLng markerCoor = marker.getPosition();
                                    for(DocumentSnapshot doc : task.getResult()) {
                                        LatLng dataCoor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
                                        if (markerCoor.latitude == dataCoor.latitude && markerCoor.longitude == dataCoor.longitude) {
                                            name = doc.getString("Name");
                                            desc = doc.getString("Desc");
                                            url = doc.getString("url");
                                        }
                                    }
                                } else {
                                    for(DocumentSnapshot doc : task.getResult()) {
                                        name = doc.getString("Name");
                                        desc = doc.getString("Desc");
                                        url = doc.getString("url");
                                    }
                                }
                                Bundle bundle = new Bundle();
                                bundle.putString("Name", name);
                                bundle.putString("Desc", desc);
                                bundle.putString("url", url);
                                bundle.putParcelable("Coordinate", marker.getPosition());
                                Fragment fragment = new MapCardDetailsFragment();
                                fragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.map_card_details_container, fragment).commit();
                            }
                        });
            }
        });
//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                if (getSupportFragmentManager().findFragmentById(R.id.map_card_details_container) != null)  {
//                    getSupportFragmentManager().beginTransaction().remove().commit()
//                }
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                       Toast.makeText(this, "User location not granted", Toast.LENGTH_SHORT).show();
                    }
                    map.setMyLocationEnabled(true);
                }
                break;
        }
    }


}
