package com.twocoders.onenus_v1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LocationFavouriteFragment extends Fragment {

    static RecyclerView recyclerView;
    ArrayList<Location> locationList;
    TextView reminder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.location_favourite_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //retrieve locationlist
        Bundle bundle = getArguments();
        assert bundle != null;
        locationList = bundle.getParcelableArrayList("LocationList");

        recyclerView = getActivity().findViewById(R.id.location_fav_recycler_view);
        reminder = getActivity().findViewById(R.id.fav_shown_here_text_view);

        if (!locationList.isEmpty()) {
            reminder.setVisibility(View.INVISIBLE);
        }


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,  0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getLayoutPosition();
                int toPosition = target.getLayoutPosition();

                Collections.swap(locationList, fromPosition, toPosition);
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                recyclerView.getAdapter().notifyDataSetChanged();

                String from = ((TextView) viewHolder.itemView.findViewById(R.id.loc_fav_frag_result_text)).getText().toString().trim();
                String to = ((TextView) target.itemView.findViewById(R.id.loc_fav_frag_result_text)).getText().toString().trim();

                swapValue(from, to);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        //initialise itemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        LocationAdaptor locationAdaptor = new LocationAdaptor(locationList);
        recyclerView.setAdapter(locationAdaptor);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


    }

    @SuppressLint("CommitPrefEdits")
    private void swapValue(String from, String to) {
        int temp = LocationPage.favouritedLocations.getInt(from, 0);
        SharedPreferences.Editor editor = LocationPage.favouritedLocations.edit();
        editor.putInt(from, LocationPage.favouritedLocations.getInt(to, 0));
        editor.apply();
        editor.putInt(to, temp);
        editor.apply();
    }

    public static void notifyInserted() {
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }


}
