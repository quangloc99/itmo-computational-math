package ru.ifmo.se.s267880.computationalMath.math.exceptions;

/**
 * @author Tran Quang Loc
 * This class is just for the sake of "exception throwing". Because the lab is simple,
 * there is not so much exception need to be thrown, although there are some of them.
 * Currently this class is will be heavily abused. If more information is needed,
 * then simply create new exception that extends this one, and add those information in.
 */
public class MathException extends Exception {
    public MathException(String message) {
        super(message);
    }
}
