package com.kolazuli.monadic_parser;
/**
 * Represents a function that applies a rule to an input string and a shared state object.
 *
 * @param <T> the type of the parsed result
 * @param <S> the type of the shared state
 */
@FunctionalInterface
public interface Applier<T, S> {
    /**
     * Applies the rule to the given input string and shared state object.
     *
     * @param rule the rule to apply
     * @return a pair consisting of the parsed result and the remaining input string
     */
    void apply(Rule<T, S> rule);
}