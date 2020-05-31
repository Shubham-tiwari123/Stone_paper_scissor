package com.example.project;

public class StoreValue {

    private float distance;
    private int label;

    StoreValue(float distance, int label) {
        this.distance = distance;
        this.label = label;
    }

    float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }
}
