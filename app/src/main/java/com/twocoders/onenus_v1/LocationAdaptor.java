package com.twocoders.onenus_v1;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LocationAdaptor extends RecyclerView.Adapter<LocationAdaptor.ViewHolder> {

    ArrayList<Location> locationList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LocationAdaptor(ArrayList<Location> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_favourite_fragment_result, parent, false);

        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final boolean[] currentFav = {locationList.get(position).getCurrentFav()};
        holder.locFavFragResultText.setText(locationList.get(position).getLoc());
        holder.locFavFragResultFavouriteIcon.setImageResource(
                                                    currentFav[0]
                                                            ? R.drawable.fav_filled_icon_foreground
                                                            : R.drawable.fav_filled_icon_foreground);

        clickFavourite(holder, position, currentFav);
        clickNav(holder, position);
        clickMap(holder, position);
    }

    private void clickMap(final ViewHolder holder, final int position) {
        holder.locFavFragResultMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.getContext(),MapPage.class);
                intent.putExtra("Location", locationList.get(position));
                holder.getContext().startActivity(intent);
            }
        });
    }

    private void clickNav(final ViewHolder holder, final int position) {
        holder.locFavFragResultNavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.getContext(), Navigation.class);
                intent.putExtra("Coordinate", locationList.get(position).getCoor());
                holder.getContext().startActivity(intent);
            }
        });
    }

    private void clickFavourite(final ViewHolder holder, final int position, final boolean[] currentFav) {
        holder.locFavFragResultFavouriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = holder.locFavFragResultText.getText().toString().trim();
           //     LocationPage.db.updateFavourite(loc, !currentFav[0]);
                currentFav[0] = !currentFav[0];

                if (currentFav[0]) {
                    holder.locFavFragResultFavouriteIcon.setImageResource(R.drawable.fav_filled_icon_foreground);
                    Toast.makeText(v.getContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
                } else {
                    holder.locFavFragResultFavouriteIcon.setImageResource(R.drawable.fav_icon_foreground);
                    Toast.makeText(v.getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = LocationPage.favouritedLocations.edit();
                    editor.remove(loc);
                    editor.apply();

                    LocationPage.locationList.remove(locationList.get(position));

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return locationList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView locFavFragResultText;
        ImageView locFavFragResultFavouriteIcon, locFavFragResultMapIcon, locFavFragResultNavIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locFavFragResultText = itemView.findViewById(R.id.loc_fav_frag_result_text);
            locFavFragResultFavouriteIcon = itemView.findViewById(R.id.loc_fav_frag_result_favourite_icon);
            locFavFragResultMapIcon = itemView.findViewById(R.id.loc_fav_frag_result_map_icon);
            locFavFragResultNavIcon = itemView.findViewById(R.id.loc_fav_frag_result_nav_icon);
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
