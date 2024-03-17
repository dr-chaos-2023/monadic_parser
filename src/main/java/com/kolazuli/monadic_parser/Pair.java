package com.kolazuli.monadic_parser;

/**
 * Represents a pair of two values.
 *
 * @param <A> the type of the first value
 * @param <B> the type of the second value
 */
public final class Pair<A, B> {
    private final A first;
    private final B second;
    /**
     * Constructs a new pair with the specified values.
     *
     * @param first  the first value
     * @param second the second value
     */
    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    /**
     * Returns the first value of this pair.
     *
     * @return the first value
     */
    public A getFirst() {
        return first;
    }
    /**
     * Returns the second value of this pair.
     *
     * @return the second value
     */
    public B getSecond() {
        return second;
    }
    /**
     * Returns a new pair with the specified values.
     *
     * @param <A>    the type of the first value
     * @param <B>    the type of the second value
     * @param first  the first value
     * @param second the second value
     * @return a new pair with the specified values
     */
    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }
    /**
     * Returns a string representation of this pair.
     *
     * @return a string representation of this pair
     */
    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}