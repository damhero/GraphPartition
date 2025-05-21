package com.example.model;

public class IndexedValue implements Comparable<IndexedValue> {
    private double value;
    private int index;

    public IndexedValue(int index, double value) {
        this.value = value;
        this.index = index;
    }

    @Override
    public int compareTo(IndexedValue o) {
        return Double.compare(this.value, o.value);
    }

    //gettery
    public double getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
