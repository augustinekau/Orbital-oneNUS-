//package com.twocoders.onenus_v1;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//public class MapInfoAdapter implements GoogleMap.InfoWindowAdapter {
//
//    private View view;
//    private Context context;
//    ImageView mapDetailsImage;
//    TextView mapDetailsDescription, mapDetailsName;
//    FirebaseFirestore db;
//    GoogleMap map;
//
//    public MapInfoAdapter(Context context, GoogleMap map) {
//        this.context = context;
//        this.map = map;
//        view = LayoutInflater.from(context).inflate(R.layout.map_detail_pop_up, null);
//
//    }
//
//    private void initWindow(final Marker marker, final View view) {
//        //Description
//
////        db = FirebaseFirestore.getInstance();
////        db.collection("Food Courts").whereEqualTo("Name", marker.getTitle()).get()
////                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                    @Override
////                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                        if (task.isSuccessful()) {
////                            TextView mapDetailsName = view.findViewById(R.id.map_details_name);
////                            for (DocumentSnapshot doc : task.getResult()) {
////                                String str = doc.getString("Name");
////                                mapDetailsName.setText(str);
////                                map.addMarker(new MarkerOptions().position(new LatLng(1.298778, 103.775603)).title(str));
////                            }
////
////                        } else {
////                            Toast.makeText(view.getContext(), "ERROR", Toast.LENGTH_SHORT).show();
////                        }
////
////                    }
////                });
//
//    }
//
//
//
//    @Override
//    public View getInfoWindow(Marker marker) {
//        initWindow(marker, view);
//        return view;
//    }
//
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        TextView mapDetailsName = view.findViewById(R.id.map_details_name);
//        new FetchData(marker, view, db).execute();
//        return view;
//    }
//
//    private static class FetchData extends AsyncTask<Void, Void, Void> {
//        private Marker marker;
//        private View view;
//        private FirebaseFirestore db;
//        private static String name;
//
//        public FetchData(Marker marker, View view, FirebaseFirestore db) {
//            this.marker = marker;
//            this.view = view;
//            this.db = db;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            db = FirebaseFirestore.getInstance();
//            db.collection("Food Courts").whereEqualTo("Name", marker.getTitle()).get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//
//                                for (DocumentSnapshot doc : task.getResult()) {
//                                    String str = doc.getString("Name");
//                                    FetchData.name = str;
//                                }
//                            } else {
//                                Toast.makeText(view.getContext(), "ERROR", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            TextView mapDetailsName = view.findViewById(R.id.map_details_name);
//            mapDetailsName.setText(FetchData.name);
//            marker.showInfoWindow();
//        }
//    }
//}
