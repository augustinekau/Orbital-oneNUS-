package com.twocoders.onenus_v1;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ExpandableAdapter extends RecyclerView.Adapter<ListVH> {
    ArrayList<String> arrayRoute;

    public ExpandableAdapter(ArrayList<String> arrayRoute) {
        this.arrayRoute = arrayRoute;
    }

    @NonNull
    @Override
    public ListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nav_expanded_result, parent, false);

        ListVH viewHolder = new ListVH(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListVH holder, int position) {
        String str = arrayRoute.get(position);
        if(str.contains("_n")){
            str = str.replace("_n","\n");
        }
        holder.textView.setText(str);

        if (str.contains("Start")) {
            holder.image.setImageResource(R.mipmap.ic_thick_top);
            holder.textView.setTypeface(null, Typeface.BOLD);
        } else if (str.contains("Arrive")) {
            holder.image.setImageResource(R.mipmap.ic_thick_bottom);
            holder.textView.setTypeface(null, Typeface.BOLD);
        } else if (str.contains(" min")) {
            holder.image.setImageResource(R.mipmap.ic_thick_connector);
            holder.textView.setTypeface(null, Typeface.ITALIC);
        } else if (str.contains(" to ")) {
            holder.image.setImageResource(R.mipmap.ic_thick_middle);
            holder.textView.setTypeface(null, Typeface.BOLD);
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return arrayRoute.size();
    }
}
