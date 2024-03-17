package com.kolazuli.monadic_parser;

/**
 * Represents a rule with input, result, remaining input, and shared state.
 *
 * @param <T> the type of the parsed result
 * @param <S> the type of the shared state
 */
public class Rule<T, S> {
    private String input;
    private T result;
    private String remaining;
    private S state;

    /**
     * Constructs a new Rule with the given input, result, remaining input, and
     * shared state.
     *
     * @param input     the input string
     * @param result    the parsed result
     * @param remaining the remaining input string after parsing
     * @param state     the shared state
     */
    private Rule(String input, T result, String remaining, S state) {
        this.input = input;
        this.result = result;
        this.remaining = remaining;
        this.state = state;
    }

    /**
     * Returns the input string that was parsed.
     *
     * @return the input string
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the parsed result.
     *
     * @return the parsed result
     */
    public T getResult() {
        return result;
    }

    /**
     * Returns the remaining input string after parsing.
     *
     * @return the remaining input string
     */
    public String getRemaining() {
        return remaining;
    }

    /**
     * Returns the shared state.
     *
     * @return the shared state
     */
    public S getState() {
        return state;
    }

    /**
     * Returns a string representation of the Rule object.
     *
     * @return a string representation of the Rule
     */
    @Override
    public String toString() {
        return "Rule{" +
                "input='" + input + '\'' +
                ", result=" + result +
                ", remaining='" + remaining + '\'' +
                ", state=" + state +
                '}';
    }

    /**
     * Builder class for constructing Rule objects.
     *
     * @param <T> the type of the parsed result
     * @param <S> the type of the shared state
     */
    public static class Builder<T, S> {
        private String input;
        private T result;
        private String remaining;
        private S state;

        /**
         * Sets the input string for the Rule.
         *
         * @param input the input string
         * @return this Builder instance
         */
        public Builder<T, S> input(String input) {
            this.input = input;
            return this;
        }

        /**
         * Sets the parsed result for the Rule.
         *
         * @param result the parsed result
         * @return this Builder instance
         */
        public Builder<T, S> result(T result) {
            this.result = result;
            return this;
        }

        /**
         * Sets the remaining input string for the Rule.
         *
         * @param remaining the remaining input string
         * @return this Builder instance
         */
        public Builder<T, S> remaining(String remaining) {
            this.remaining = remaining;
            return this;
        }

        /**
         * Sets the shared state for the Rule.
         *
         * @param state the shared state
         * @return this Builder instance
         */
        public Builder<T, S> state(S state) {
            this.state = state;
            return this;
        }

        /**
         * Builds a new Rule object with the provided input, result, remaining input,
         * and shared state.
         *
         * @return a new Rule object
         */
        public Rule<T, S> build() {
            return new Rule<>(input, result, remaining, state);
        }
    }
}