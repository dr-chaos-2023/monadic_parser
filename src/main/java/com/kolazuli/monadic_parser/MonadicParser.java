package com.kolazuli.monadic_parser;
/**
 * Represents a parser that takes a string and returns a pair of the result and the remaining string.
 * This interface is designed to be used with functional programming patterns.
 *
 * @param <T> the type of the result
 */
@FunctionalInterface
public interface MonadicParser<T> {
    /**
     * Parses the input string and returns a pair containing the result and the remaining string.
     *
     * @param input the input string to parse
     * @return a pair containing the result and the remaining string
     */
    Pair<T, String> parse(String input);
}