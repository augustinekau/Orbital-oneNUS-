package com.twocoders.onenus_v1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;


public class MapCardDetailsFragment extends Fragment {
    ImageView mapCardDetailsImage;
    TextView mapCardDetailsName, mapCardDetailsDesc;
    Button crossButton, navButton;
    FirebaseFirestore db;
    String name, desc, url;
    LatLng coordinate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_card_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mapCardDetailsImage = getActivity().findViewById(R.id.map_card_details_image);
        mapCardDetailsName = getActivity().findViewById(R.id.map_card_details_name);
        mapCardDetailsDesc = getActivity().findViewById(R.id.map_card_details_description);
        crossButton = getActivity().findViewById(R.id.map_card_details_cross_button);
        navButton = getActivity().findViewById(R.id.map_card_details_nav_button);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();

        assert bundle != null;
        name = bundle.getString("Name");
        desc = replaceStr(bundle.getString("Desc"));
        url = bundle.getString("url");
        coordinate = bundle.getParcelable("Coordinate");

        mapCardDetailsName.setText(name);
        mapCardDetailsDesc.setText(desc);

        new DownloadImageTask(mapCardDetailsImage).execute(url);

        onClickCrossButton();
        onClickNavButton();

    }

    private String replaceStr(String str) {
        if (str.contains("_n")) {
            str = str.replace("_n", "\n");
        }
        return str;
    }

    private void onClickNavButton() {
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                db.collection("Locations").whereEqualTo("Name", name).get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                LatLng coor = null;
//
//                                for (DocumentSnapshot doc : task.getResult()) {
//                                    if (coor.longitude == ) {
//                                        coor = new LatLng(doc.getDouble("lat"), doc.getDouble("lng"));
//                                    }
//                                }
//
//                                Intent intent = new Intent(getActivity(), Navigation.class);
//                                intent.putExtra("Coordinate", coor);
//                                startActivity(intent);
//                            }
//                        });
                Intent intent = new Intent(getActivity(), Navigation.class);
                intent.putExtra("Coordinate", coordinate);
                startActivity(intent);
            }
        });
    }

    private void onClickCrossButton() {
        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getFragmentManager().findFragmentById(R.id.map_card_details_container);
                getFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlDisplay = strings[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            bmImage.setImageBitmap(bitmap);
        }
    }

}
