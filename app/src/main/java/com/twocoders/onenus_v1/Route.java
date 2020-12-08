package com.twocoders.onenus_v1;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class Route {
    String busNumber, timeTaken, numBusStop;
    ExpandableAdapter myArrayAdapter;
    private boolean expanded;

    public Route(String busNumber, String timeTaken, String numBusStop, ExpandableAdapter myArrayAdapter) {
        this.busNumber = busNumber;
        this.timeTaken = timeTaken;
        this.numBusStop = numBusStop;
        this.myArrayAdapter = myArrayAdapter;
        this.expanded = false;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ExpandableAdapter getMyArrayAdapter() {
        return myArrayAdapter;
    }


    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getNumBusStop() {
        return numBusStop;
    }

    public void setNumBusStop(String numBusStop) {
        this.numBusStop = numBusStop;
    }

}
