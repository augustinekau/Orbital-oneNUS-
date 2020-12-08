package com.twocoders.onenus_v1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    List<Route> routesList;

    RecyclerView.RecycledViewPool recycledViewPool =  new RecyclerView.RecycledViewPool();

    public CustomAdapter(List<Route> routesList) {
        this.routesList = routesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.nav_result, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = routesList.get(position).getBusNumber();
        if(str.contains("_n")){
            str = str.replace("_n","\n");
        }

        holder.busNumberText.setText(str);
        holder.timeTakenText.setText(routesList.get(position).getTimeTaken());
        holder.numOfStopText.setText(routesList.get(position).getNumBusStop());
        holder.expandableList.setAdapter(routesList.get(position).getMyArrayAdapter());

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(holder.expandableList.getContext(),
                                                LinearLayoutManager.VERTICAL, false);
        layoutManager.setInitialPrefetchItemCount(5);
        holder.expandableList.setLayoutManager(layoutManager);
        holder.expandableList.setRecycledViewPool(recycledViewPool);

        boolean isExpanded = routesList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded? View.VISIBLE : View.GONE);

    }

    public void clear() {
        routesList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return routesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView busNumberText, numOfStopText, timeTakenText;

        RecyclerView expandableList;

        ConstraintLayout expandableLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            busNumberText = itemView.findViewById(R.id.busNumberText);
            numOfStopText = itemView.findViewById(R.id.numOfStopText);
            timeTakenText = itemView.findViewById(R.id.timeTakenText);
            expandableList = itemView.findViewById(R.id.expandableRV);

            expandableLayout = itemView.findViewById(R.id.constraintLayout);
            itemView.findViewById(R.id.linearLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Route route = routesList.get(getAdapterPosition());
                    route.setExpanded(!route.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
        }
    }
}
