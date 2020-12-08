package com.twocoders.onenus_v1;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListVH extends RecyclerView.ViewHolder {
    ImageView image;
    TextView textView;

    public ListVH(@NonNull View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image_dot);
        textView = itemView.findViewById(R.id.instruction_text);
    }
}