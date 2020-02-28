package ru.ifmo.se.s267880.computationalMath.math.exceptions;

import ru.ifmo.se.s267880.computationalMath.math.Matrix;

public class RequiredSameDimensionsException extends Exception {
    private Matrix sourceMatrix;
    private Matrix destinationMatrix;

    public RequiredSameDimensionsException(Matrix sourceMatrix, Matrix destinationMatrix) {
        super(String.format("Matrix %s require %s to have the same dimension", sourceMatrix, destinationMatrix));
        this.sourceMatrix = sourceMatrix;
        this.destinationMatrix = destinationMatrix;
    }

    public Matrix getSourceMatrix() {
        return sourceMatrix;
    }

    public Matrix getDestinationMatrix() {
        return destinationMatrix;
    }
}
