package com.twocoders.onenus_v1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class LocationResultFragment extends Fragment implements OnMapReadyCallback {

    private static final int MY_PERMISSION_FINE_LOCATION = 102;
    GoogleMap map;
    SupportMapFragment supportMapFragment;
    TextView locationResultNameTextView;
    Double latitude, longitude;
    String name;
    ImageView locationResultFavIcon, locationResultNavIcon;
    boolean currentFav;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_result_fragment, null, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        locationResultNameTextView = Objects.requireNonNull(getActivity()).findViewById(R.id.location_result_name_text_view);
        //initialise map
        super.onActivityCreated(savedInstanceState);
        supportMapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.loc_result_frag_map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

        //initialising fav icon
        locationResultFavIcon = Objects.requireNonNull(getActivity()).findViewById(R.id.location_result_fav_icon);
        locationResultNavIcon = Objects.requireNonNull(getActivity()).findViewById(R.id.location_result_nav_icon);

        onClickFav();
        onClickNav();

        //extract data
        assert getArguments() != null;
        Bundle bundle = getArguments();

        Location loc = (Location) bundle.get("Location");
        name = loc.getLoc();
        latitude = loc.getCoor().latitude;
        longitude = loc.getCoor().longitude;
        currentFav = loc.getCurrentFav();

        //set heart accordingly
        if (currentFav) {
            locationResultFavIcon.setImageDrawable(getResources().getDrawable(R.drawable.fav_filled_icon_foreground));
        } else {
            locationResultFavIcon.setImageDrawable(getResources().getDrawable(R.drawable.fav_icon_foreground));
        }

        //set data
        locationResultNameTextView.setText(name);


    }

    private void onClickNav() {
        locationResultNavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Navigation.class);
                LatLng loc = new LatLng(latitude, longitude);
                intent.putExtra("Coordinate", loc);
                startActivity(intent);
            }
        });
    }

    private void onClickFav() {
        locationResultFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = locationResultNameTextView.getText().toString().trim();
//                LocationPage.db.updateFavourite(loc, !currentFav);
                Location location = new Location(loc, currentFav);
                currentFav = !currentFav;
                location.changeCurrentFav();
                SharedPreferences sharedPreferences = LocationPage.favouritedLocations;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (currentFav) {
                    locationResultFavIcon.setImageDrawable(getResources().getDrawable(R.drawable.fav_filled_icon_foreground));
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
                    if (!sharedPreferences.contains(loc)) {
                        int index = LocationPage.getLastIndex();
                        editor.putInt(loc, index + 1);
                        editor.apply();
                    }

                } else {
                    locationResultFavIcon.setImageDrawable(getResources().getDrawable(R.drawable.fav_icon_foreground));
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();

                    editor.remove(loc);
                    editor.apply();
                }
                LocationPage.locationList.clear();
                LocationFavouriteFragment.notifyInserted();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng coor = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions()
                .position(coor)
                .title(name)
                .icon(bitmapDescriptorFromAsset(Objects.requireNonNull(getActivity()).getApplicationContext())));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coor, 17));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_FINE_LOCATION);
            }
        } else {
            map.setMyLocationEnabled(true);
        }
        map.getUiSettings().setZoomControlsEnabled(true);

    }

    private BitmapDescriptor bitmapDescriptorFromAsset(Context context) {
        Drawable drawable = new ScaleDrawable(ContextCompat.getDrawable(context, R.drawable.xin_map_pointer_icon_foreground), 0, 50, 50).getDrawable();
        assert drawable != null;
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, Color.rgb(230, 25, 75));
        drawable.setBounds(0, 0, 100, 100);
        Bitmap bitmap = Bitmap.createBitmap(100,
                100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }
}
