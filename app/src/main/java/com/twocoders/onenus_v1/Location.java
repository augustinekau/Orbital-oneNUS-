package com.twocoders.onenus_v1;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Parcelable {
    String loc;
    boolean currentFav;
    LatLng coor;

    public Location(String loc, LatLng coor) {
        this.loc = loc;
        this.coor = coor;
    }

    public Location(String loc, boolean currentFav) {
        this.loc = loc;
        this.currentFav = currentFav;
    }

    public Location(String loc, boolean currentFav, LatLng coor) {
        this.loc = loc;
        this.currentFav = currentFav;
        this.coor = coor;
    }

    protected Location(Parcel in) {
        loc = in.readString();
        currentFav = in.readByte() != 0;
        coor = in.readParcelable(LatLng.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(loc);
        dest.writeByte((byte) (currentFav ? 1 : 0));
        dest.writeParcelable(coor, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public LatLng getCoor() {
        return coor;
    }

    public String getLoc() {
        return loc;
    }

    public boolean getCurrentFav() {
        return currentFav;
    }

    public void changeCurrentFav() {
        this.currentFav = !currentFav;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            Location loc = (Location) obj;
            return loc.getLoc().equals(this.getLoc());
        }
    }
}
