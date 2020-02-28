package ru.ifmo.se.s267880.computationalMath.math;

import org.jetbrains.annotations.NotNull;

public class Vector {
    private double[] data;

    public Vector(int size) {
        data = new double[size];
    }

    public Vector(@NotNull Vector other) {
        this(other.getSize());
        System.arraycopy(other.data, 0, data, 0, data.length);
    }

    public Vector copy() {
        return new Vector(this);
    }

    // TODO: basic operation like add, subtract, scale
    //       I currently don't add them since the 1st lab does not require.

    public int getSize() {
        return data.length;
    }

    public void set(int index, double value) {
        data[index] = value;
    }

    public double get(int index) {
        return data[index];
    }

    @Override
    public String toString() {
        return String.format("Vector(size = %d)", getSize());
    }
}
