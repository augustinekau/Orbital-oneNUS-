//package com.twocoders.onenus_v1;
//
//import android.content.Context;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.twocoders.onenus_v1.R;
//
//public class ViewHolder extends RecyclerView.ViewHolder {
//
//    TextView busNumberText, numOfStopText, timeTakenText;
//
//    RecyclerView expandableList;
//
//    ConstraintLayout expandableLayout;
//
//   // RecyclerView.LayoutManager layoutManager;
//
//    public ViewHolder(@NonNull View itemView) {
//        super(itemView);
//        busNumberText = itemView.findViewById(R.id.busNumberText);
//        numOfStopText = itemView.findViewById(R.id.numOfStopText);
//        timeTakenText = itemView.findViewById(R.id.timeTakenText);
//        expandableList = itemView.findViewById(R.id.expandableRV);
//
//        expandableLayout = itemView.findViewById(R.id.constraintLayout);
//        itemView.findViewById(R.id.linearLayout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Route route =
//            }
//        });
//    }
//
//
////    TextView busNumberText, timeTakenText, numOfStopText;
////    View mView;
////
////    public ViewHolder(@NonNull View itemView) {
////        super(itemView);
////
////        mView = itemView;
////
////        //item click
////        itemView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                mClickListener.onItemClick(v, getAdapterPosition());
////            }
////        });
////
////        //initialise view with model_layout
////        busNumberText = itemView.findViewById(R.id.busNumberText);
////        timeTakenText = itemView.findViewById(R.id.timeTakenText);
////        numOfStopText = itemView.findViewById(R.id.numOfStopText);
////    }
////
////    private ViewHolder.ClickListener mClickListener;
////    //interface for click listener
////    public interface ClickListener {
////        void onItemClick(View view, int position);
////        void onItemLongClick(View view, int position);
////    }
////
////    public void setOnClickListener(ViewHolder.ClickListener clickListener) {
////        mClickListener = clickListener;
////    }
//}