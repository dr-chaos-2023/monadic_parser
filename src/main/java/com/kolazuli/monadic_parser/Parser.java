package com.kolazuli.monadic_parser;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Provides utility methods for creating parsers that match specific patterns in
 * strings.
 */
public class Parser {
    /**
     * Global locker object, this lock is used to allow only one thread
     * per app of parser to execute the track method.
     */
    private static final Object lock = new Object();

    /**
     * Creates a parser that matches a single character.
     *
     * @param c the character to match
     * @return a MonadicParser that matches the specified character
     */
    public static MonadicParser<CharSequence> charParser(char c) {
        return s -> s.isEmpty() || s.charAt(0) != c ? Pair.of(null, s) : Pair.of(String.valueOf(c), s.substring(1));
    }

    /**
     * Creates a parser that matches a single digit.
     *
     * @return a MonadicParser that matches a single digit
     */
    public static MonadicParser<CharSequence> digitParser() {
        return s -> s.isEmpty() || !Character.isDigit(s.charAt(0)) ? Pair.of(null, s)
                : Pair.of(String.valueOf(s.charAt(0)), s.substring(1));
    }

    /**
     * Creates a parser that matches a single lowercase letter from the input
     * string.
     *
     * @return a MonadicParser that parses a single lowercase letter from the input
     *         string.
     *         If the input string is empty or the first character is not a
     *         lowercase letter,
     *         the parser returns a pair with null as the first element and the
     *         original string as the second element.
     *         Otherwise, it returns a pair with the first character as the first
     *         element and the rest of the string as the second element.
     */
    public static MonadicParser<CharSequence> lowerLetterParser() {
        return s -> s.isEmpty() || !Character.isLetter(s.charAt(0)) || !Character.isLowerCase(s.charAt(0))
                ? Pair.of(null, s)
                : Pair.of(String.valueOf(s.charAt(0)), s.substring(1));
    }

    /**
     * Creates a parser that matches a single uppercase letter from the input
     * string.
     *
     * @return a MonadicParser that parses a single uppercase letter from the input
     *         string.
     *         If the input string is empty or the first character is not an
     *         uppercase letter,
     *         the parser returns a pair with null as the first element and the
     *         original string as the second element.
     *         Otherwise, it returns a pair with the first character as the first
     *         element and the rest of the string as the second element.
     */
    public static MonadicParser<CharSequence> upperLetterParser() {
        return s -> s.isEmpty() || !Character.isLetter(s.charAt(0)) || !Character.isUpperCase(s.charAt(0))
                ? Pair.of(null, s)
                : Pair.of(String.valueOf(s.charAt(0)), s.substring(1));
    }

    /**
     * Creates a parser that matches an alphabetic character.
     *
     * @return a MonadicParser that matches an alphabetic character
     */
    public static MonadicParser<CharSequence> letterParser() {
        return s -> s.isEmpty() || !Character.isLetter(s.charAt(0)) ? Pair.of(null, s)
                : Pair.of(String.valueOf(s.charAt(0)), s.substring(1));
    }

    /**
     * Creates a parser that matches an alphanumeric character.
     *
     * @return a MonadicParser that matches an alphanumeric character
     */
    public static MonadicParser<CharSequence> letterOrDigitParser() {
        return s -> s.isEmpty() || !Character.isLetterOrDigit(s.charAt(0)) ? Pair.of(null, s)
                : Pair.of(String.valueOf(s.charAt(0)), s.substring(1));
    }

    /**
     * Creates a parser that matches a specific string.
     *
     * @param target the string to match
     * @return a MonadicParser that matches the specified string
     */
    public static MonadicParser<CharSequence> textParser(String target) {
        return s -> !s.startsWith(target) ? Pair.of(null, s) : Pair.of(target, s.substring(target.length()));
    }

    /**
     * Creates a parser that matches the exact target string.
     *
     * @param target the string to match exactly
     * @return a MonadicParser that matches the exact target string
     */
    public static MonadicParser<CharSequence> textExactParser(String target) {
        return s -> !s.equals(target) ? Pair.of(null, s) : Pair.of(target, "");
    }

    /**
     * Creates a parser that matches the target string if it is at the end of the
     * input string.
     *
     * @param target the string to match at the end of the input
     * @return a MonadicParser that matches the target string if it is at the end of
     *         the input string
     */
    public static MonadicParser<CharSequence> endsWithTextParser(String target) {
        return s -> !s.endsWith(target) ? Pair.of(null, s)
                : Pair.of(target, s.substring(0, s.length() - target.length()));
    }

    /**
     * Creates a parser that matches a number (integer or floating-point).
     *
     * @return a MonadicParser that matches a number.
     */
    public static MonadicParser<CharSequence> numberParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            StringBuilder sign = new StringBuilder();
            if ("+-".indexOf(input.charAt(0)) != -1) {
                sign.append(input.charAt(0));
                input = input.substring(1);
            }
            StringBuilder integerPart = new StringBuilder();
            while (!input.isEmpty() && Character.isDigit(input.charAt(0))) {
                integerPart.append(input.charAt(0));
                input = input.substring(1);
            }
            StringBuilder decimalPart = new StringBuilder();
            if (!input.isEmpty() && input.charAt(0) == '.') {
                input = input.substring(1);
                while (!input.isEmpty() && Character.isDigit(input.charAt(0))) {
                    decimalPart.append(input.charAt(0));
                    input = input.substring(1);
                }
            }
            StringBuilder exponentPart = new StringBuilder();
            if (!input.isEmpty() && (input.charAt(0) == 'e' || input.charAt(0) == 'E')) {
                input = input.substring(1);
                if (!input.isEmpty() && "+-".indexOf(input.charAt(0)) != -1) {
                    exponentPart.append(input.charAt(0));
                    input = input.substring(1);
                }
                while (!input.isEmpty() && Character.isDigit(input.charAt(0))) {
                    exponentPart.append(input.charAt(0));
                    input = input.substring(1);
                }
            }
            String numberStr = sign.toString() + integerPart.toString() +
                    (decimalPart.length() > 0 ? "." + decimalPart.toString() : "") +
                    (exponentPart.length() > 0 ? "e" + exponentPart.toString() : "");
            return Pair.of(numberStr.isEmpty() ? null : numberStr, input);
        };
    }

    /**
     * Creates a parser that matches any newline sequence.
     *
     * @return a MonadicParser that matches any newline sequence.
     */
    public static MonadicParser<CharSequence> newLineParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) == '\n' || input.charAt(0) == '\r') {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Returns a parser that matches any whitespace character (space or tab).
     *
     * @return a MonadicParser that matches any whitespace character (space or tab).
     */
    public static MonadicParser<CharSequence> spaceParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) == ' ' || input.charAt(0) == '\t') {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Returns a parser that matches any arithmetic operator.
     *
     * @return a MonadicParser that matches any arithmetic operator.
     */
    public static MonadicParser<CharSequence> operatorParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            String operators = "+*-/%";
            if (operators.indexOf(input.charAt(0)) != -1) {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Parser that matches a C identifier.
     *
     * @return a MonadicParser that matches a C identifier.
     */
    public static MonadicParser<CharSequence> cIdentifierParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if ((Character.isLetter(input.charAt(0)) || input.charAt(0) == '_') &&
                    input.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '_')) {
                return Pair.of(input, "");
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Parser that matches any single character.
     *
     * @return a MonadicParser that matches any single character.
     */
    public static MonadicParser<CharSequence> anyCharParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
        };
    }

    /**
     * Parser that matches any single character within a specified range.
     *
     * @param start the start character of the range
     * @param end   the end character of the range
     * @return a MonadicParser that matches any single character within the
     *         specified range
     */
    public static MonadicParser<CharSequence> inRangeParser(char start, char end) {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) >= start && input.charAt(0) <= end) {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Parser that matches any single character not within a specified range.
     *
     * @param start the start character of the range
     * @param end   the end character of the range
     * @return a MonadicParser that matches any single character not within the
     *         specified range
     */
    public static MonadicParser<CharSequence> notInRangeParser(char start, char end) {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) < start || input.charAt(0) > end) {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Returns a parser that matches any exponentiation character (e.g., **).
     *
     * @return a MonadicParser that matches any exponentiation character.
     */
    public static MonadicParser<CharSequence> exponentiationParser() {
        return input -> {
            if (input == null || input.length() < 2) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) == '*' && input.charAt(1) == '*') {
                return Pair.of("**", input.substring(2));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Returns a parser that matches any parenthesis character (opening or closing).
     *
     * @return a MonadicParser that matches any parenthesis character.
     */
    public static MonadicParser<CharSequence> parenthesisParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) == '(' || input.charAt(0) == ')') {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Returns a parser that matches any square bracket character (opening or
     * closing).
     *
     * @return a MonadicParser that matches any square bracket character.
     */
    public static MonadicParser<CharSequence> squareBracketsParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) == '[' || input.charAt(0) == ']') {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Returns a parser that matches any bracket character (opening or closing).
     *
     * @return a MonadicParser that matches any bracket character.
     */
    public static MonadicParser<CharSequence> bracketsParser() {
        return input -> {
            if (input == null || input.isEmpty()) {
                return Pair.of(null, input);
            }
            if (input.charAt(0) == '{' || input.charAt(0) == '}') {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Creates a parser that matches operators based on their precedence.
     *
     * @param operatorMap A map where keys are operator strings and values are their
     *                    precedence levels.
     * @return A parser that matches operators based on their precedence.
     */
    public static MonadicParser<Integer> operatorPrecedenceParser(Map<String, Integer> operatorMap) {
        return input -> {
            for (Map.Entry<String, Integer> entry : operatorMap.entrySet()) {
                String operator = entry.getKey();
                MonadicParser<CharSequence> parser = textExactParser(operator);
                Pair<CharSequence, String> result = parser.parse(input);
                if (result.getFirst() != null) {
                    return Pair.of(entry.getValue(), result.getSecond());
                }
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Applies a parser to the input string and calls the on_success function if the
     * parse succeeds,
     * or the on_failure function if the parse fails.
     *
     * @param <T>         the type of the parsed result
     * @param <S>         the type of the shared state
     * @param parser      the parser to apply
     * @param onSuccess   the function to call if the parse succeeds
     * @param onFailure   the function to call if the parse fails
     * @param sharedState the shared state between all the matchers
     * @return a function that takes an input string and applies the parser,
     *         calling the appropriate function based on the parse result
     */
    public static <T, S> MonadicParser<T> matcher(
            MonadicParser<T> parser,
            Applier<T, S> onSuccess,
            Applier<T, S> onFailure,
            S sharedState) {
        return input -> {
            Pair<T, String> result = parser.parse(input);
            Rule<T, S> rule = new Rule.Builder<T, S>()
                    .input(input)
                    .result(result.getFirst())
                    .remaining(result.getSecond())
                    .state(sharedState)
                    .build();
            if (result.getFirst() != null) {
                onSuccess.apply(rule);
            } else {
                onFailure.apply(rule);
            }
            return result;
        };
    }

    /**
     * Tracks the position and line/column information of a parser as it processes
     * an input string.
     *
     * @param <T>     the type of the parsed result
     * @param scanner the scanner object to update with the parser's progress
     * @param parser  the parser to apply to the input string
     * @return a function that takes an input string and applies the parser,
     *         updating the scanner's position and line/column information
     */
    public static <T> MonadicParser<T> track(Scanner scanner, MonadicParser<T> parser) {
        return input -> {
            if (input.isEmpty()) {
                return Pair.of(null, input);
            }
            Pair<T, String> pair = parser.parse(input);
            int position = input.length() - pair.getSecond().length();
            char last = input.charAt(position - 1);
            int line = 0;
            int column = 0;
            for (int index = 0; index < position; index++) {
                if (input.charAt(index) == '\n' || input.charAt(index) == '\r') {
                    line++;
                    column = 0;
                }
                column++;
            }
            synchronized (lock) {
                scanner.setLine(line);
                scanner.setLast(last);
                scanner.setColumn(column);
                scanner.setPosition(position);
            }
            return pair;
        };
    }

    /**
     * Combines two parsers sequentially.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser
     * @param p2  the second parser
     * @return a new parser that applies the first parser and then the second parser
     */
    public static <T> MonadicParser<T> seq(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            if (result1.getFirst() == null) {
                return result1;
            }
            return p2.parse(result1.getSecond());
        };
    }

    /**
     * Combines two parsers with a choice operation.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser
     * @param p2  the second parser
     * @return a new parser that applies the first parser and, if it fails, applies
     *         the second parser
     */
    public static <T> MonadicParser<T> choice(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            if (result1.getFirst() != null) {
                return result1;
            }
            return p2.parse(input);
        };
    }

    /**
     * Applies the given parser to the input string zero or more times,
     * collecting the results into a list.
     *
     * @param p the parser to apply
     * @return a new parser that applies the given parser zero or more times
     */
    public static <T> MonadicParser<List<T>> zeroOrMore(MonadicParser<T> p) {
        return input -> {
            List<T> results = new ArrayList<>();
            String rest = input;
            while (true) {
                Pair<T, String> resultPair = p.parse(rest);
                T result = resultPair.getFirst();
                String newRest = resultPair.getSecond();
                if (result != null && !newRest.equals(rest)) {
                    results.add(result);
                    rest = newRest;
                } else {
                    break;
                }
            }
            return Pair.of(results, rest);
        };
    }

    /**
     * Applies the given parser to the input string one or more times,
     * collecting the results into a list.
     *
     * @param p the parser to apply
     * @return a new parser that applies the given parser one or more times
     */
    public static <T> MonadicParser<List<T>> oneOrMore(MonadicParser<T> p) {
        return input -> {
            List<T> results = new ArrayList<>();
            String rest = input;
            while (true) {
                Pair<T, String> resultPair = p.parse(rest);
                T result = resultPair.getFirst();
                String newRest = resultPair.getSecond();
                if (result != null && !newRest.equals(rest)) {
                    results.add(result);
                    rest = newRest;
                } else {
                    if (!results.isEmpty()) {
                        return Pair.of(results, rest);
                    } else {
                        return Pair.of(new ArrayList<>(), input);
                    }
                }
            }
        };
    }

    /**
     * Performs a lookahead with a parser.
     *
     * This function applies the given parser to the input string without consuming
     * any input.
     * It's useful for checking if a certain pattern exists without advancing the
     * parser's position.
     *
     * @param <T> the type of the parsed result
     * @param p   the parser to look ahead with
     * @return a new parser that represents the lookahead operation
     */
    public static <T> MonadicParser<T> lookahead(MonadicParser<T> p) {
        return input -> {
            Pair<T, String> result = p.parse(input);
            return Pair.of(result.getFirst(), input); // Return the result without consuming input
        };
    }

    /**
     * Negates the result of a parser.
     *
     * This function applies the given parser to the input string and negates the
     * result.
     * It succeeds if the parser fails and fails if the parser succeeds.
     *
     * @param <T> the type of the parsed result
     * @param p   the parser to negate
     * @return a new parser that represents the negation of the given parser
     */
    public static <T> MonadicParser<CharSequence> negate(MonadicParser<T> p) {
        return input -> {
            Pair<T, String> result = p.parse(input);
            if (result.getFirst() == null) {
                return Pair.of(String.valueOf(input.charAt(0)), input.substring(1));
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Combines two parsers with an "and" operation.
     *
     * This function applies the first parser to the input string. If the first
     * parser succeeds,
     * it then applies the second parser to the remaining string. The combined
     * parser succeeds if both
     * parsers succeed.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser to apply
     * @param p2  the second parser to apply if the first parser succeeds
     * @return a new parser that represents the "and" operation between the two
     *         input parsers
     */
    public static <T> MonadicParser<T> and(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            if (result1.getFirst() == null) {
                return Pair.of(null, input);
            }
            Pair<T, String> result2 = p2.parse(result1.getSecond());
            if (result2.getFirst() == null) {
                return Pair.of(null, input);
            }
            return result2;
        };
    }

    /**
     * Combines two parsers with an "or" operation.
     *
     * This function attempts to apply the first parser to the input string. If the
     * first parser fails,
     * it then attempts to apply the second parser to the original input string. The
     * combined parser succeeds
     * if either parser succeeds.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser to try
     * @param p2  the second parser to try if the first parser fails
     * @return a new parser that represents the "or" operation between the two input
     *         parsers
     */
    public static <T> MonadicParser<T> or(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            if (result1.getFirst() != null) {
                return result1;
            }
            return p2.parse(input);
        };
    }

    /**
     * Combines two parsers with an "exclusive or" (XOR) operation.
     *
     * This function applies both parsers to the input string. It succeeds if
     * exactly one of the parsers succeeds,
     * and fails if both parsers succeed or both fail.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser to apply
     * @param p2  the second parser to apply
     * @return a new parser that represents the "exclusive or" operation between the
     *         two input parsers
     */
    public static <T> MonadicParser<T> xor(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            Pair<T, String> result2 = p2.parse(input);
            boolean success1 = result1.getFirst() != null;
            boolean success2 = result2.getFirst() != null;
            if (success1 == success2) {
                return Pair.of(null, input);
            }
            return success1 ? result1 : result2;
        };
    }

    /**
     * Combines two parsers sequentially and keeps the result of the first parser.
     *
     * This function applies the first parser to the input string, and if it
     * succeeds, applies the second parser to the remaining string.
     * The combined parser succeeds if both parsers succeed, and it returns the
     * result of the first parser.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser to apply
     * @param p2  the second parser to apply if the first parser succeeds
     * @return a new parser that represents the sequential combination of the two
     *         input parsers, keeping the result of the first parser
     */
    public static <T> MonadicParser<T> seqKeepFirst(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            if (result1.getFirst() == null) {
                return Pair.of(null, input);
            }
            Pair<T, String> result2 = p2.parse(result1.getSecond());
            return Pair.of(result1.getFirst(), result2.getSecond());
        };
    }

    /**
     * Combines two parsers with a choice operation and keeps the result of the
     * first successful parser.
     *
     * This function applies the first parser to the input string, and if it fails,
     * applies the second parser to the original input string.
     * The combined parser succeeds if either parser succeeds, and it returns the
     * result of the first successful parser.
     *
     * @param <T> the type of the parsed result
     * @param p1  the first parser to try
     * @param p2  the second parser to try if the first parser fails
     * @return a new parser that represents the choice operation between the two
     *         input parsers, keeping the result of the first successful parser
     */
    public static <T> MonadicParser<T> choiceKeepFirst(MonadicParser<T> p1, MonadicParser<T> p2) {
        return input -> {
            Pair<T, String> result1 = p1.parse(input);
            if (result1.getFirst() != null) {
                return result1;
            }
            Pair<T, String> result2 = p2.parse(input);
            return result2;
        };
    }

    /**
     * Applies a parser a specific number of times.
     *
     * This function applies the given parser to the input string a specific number
     * of times, collecting the results into a list.
     *
     * @param <T> the type of the parsed result
     * @param p   the parser to apply
     * @param n   the number of times to apply the parser
     * @return a new parser that represents the application of the given parser a
     *         specific number of times
     */
    public static <T> MonadicParser<List<T>> n(MonadicParser<T> p, int n) {
        return input -> {
            List<T> results = new ArrayList<>();
            String rest = input;
            int counter = 0;
            while (counter < n && !rest.isEmpty()) {
                Pair<T, String> resultPair = p.parse(rest);
                T result = resultPair.getFirst();
                rest = resultPair.getSecond();
                if (result != null) {
                    results.add(result);
                }
                counter++;
            }
            return Pair.of(results, rest);
        };
    }

    /**
     * Parser that matches a string between two specified strings.
     *
     * @param start the start string to match
     * @param end   the end string to match
     * @return a parser that matches a string between the specified start and end
     *         strings
     */
    public static MonadicParser<CharSequence> between(String start, String end) {
        return input -> {
            int startIndex = input.indexOf(start);
            int endIndex = input.indexOf(end, startIndex + start.length());
            if (startIndex != -1 && endIndex != -1 && startIndex != endIndex - 1) {
                return Pair.of(
                        input.substring(startIndex + start.length(), endIndex),
                        input.substring(endIndex + end.length()));
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Creates a parser that captures the content between two specified parsers.
     *
     * This method is a convenience method that delegates to the
     * {@link #captureBetween(MonadicParser, MonadicParser)} method.
     * It takes two monadic parsers as arguments, `start` and `end`, and returns a
     * parser that captures the content
     * between the matches of these parsers.
     *
     * @param start the parser that matches the start of the content to capture
     * @param end   the parser that matches the end of the content to capture
     * @return a parser that captures the content between the specified start and
     *         end parsers
     * @see #captureBetween(MonadicParser, MonadicParser)
     */
    public static MonadicParser<CharSequence> between(MonadicParser<CharSequence> start,
            MonadicParser<CharSequence> end) {
        return captureBetween(start, end);
    }

    /**
     * Parser that matches a string between two specified strings, allowing for
     * multiple occurrences of the start string.
     *
     * This function finds the first occurrence of the start string and then
     * continues to find the next occurrence
     * of the start string, skipping over any intermediate occurrences. It then
     * finds the first occurrence of the end string
     * after the last occurrence of the start string. The parser succeeds if it
     * finds both the start and end strings
     * and returns the substring between them.
     *
     * @param start the start string to match
     * @param end   the end string to match
     * @return a parser that matches a string between the specified start and end
     *         strings, allowing for multiple occurrences of the start string
     */
    public static MonadicParser<CharSequence> betweenStar(String start, String end) {
        return input -> {
            int startIndex = input.indexOf(start);
            if (startIndex == -1) {
                return Pair.of(null, input);
            }
            while (startIndex + start.length() < input.length()
                    && input.startsWith(start, startIndex + start.length())) {
                startIndex += start.length();
            }
            int endIndex = input.indexOf(end, startIndex + start.length());
            if (endIndex != -1 && startIndex != endIndex - 1) {
                return Pair.of(
                        input.substring(startIndex + start.length(), endIndex),
                        input.substring(endIndex + end.length()));
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Parser that matches a string that is not between two specified strings.
     *
     * @param start the start string to match
     * @param end   the end string to match
     * @return a parser that matches a string that is not between the specified
     *         start and end strings
     */
    public static MonadicParser<CharSequence> notBetween(String start, String end) {
        return input -> {
            StringBuilder result = new StringBuilder();
            while (true) {
                int startIndex = input.indexOf(start);
                int endIndex = input.indexOf(end, startIndex + start.length());
                if (startIndex != -1 && endIndex != -1) {
                    String beforeStart = input.substring(0, startIndex);
                    String afterEnd = input.substring(endIndex + end.length());
                    result.append(beforeStart);
                    input = afterEnd;
                } else {
                    result.append(input);
                    break;
                }
            }
            return Pair.of(result.toString(), "");
        };
    }

    /**
     * Creates a parser that ignores the content between two specified parsers.
     *
     * This method is a convenience method that delegates to the
     * {@link #ignoreBetween(MonadicParser, MonadicParser)} method.
     * It takes two monadic parsers as arguments, `start` and `end`, and returns a
     * parser that ignores the content
     * between the matches of these parsers.
     *
     * @param start the parser that matches the start of the content to ignore
     * @param end   the parser that matches the end of the content to ignore
     * @return a parser that ignores the content between the specified start and end
     *         parsers
     * @see #ignoreBetween(MonadicParser, MonadicParser)
     */
    public static MonadicParser<CharSequence> notBetween(MonadicParser<CharSequence> start,
            MonadicParser<CharSequence> end) {
        return ignoreBetween(start, end);
    }

    /**
     * Parser that matches a pattern 'x' if it is followed by another pattern 't'.
     *
     * This function takes a parser `p` and a string `next`. It returns a new parser
     * that, when called with an input string,
     * applies the parser `p` to the input string. If the parser `p` succeeds and
     * the remaining string starts with `next`,
     * the new parser returns the result of `p` and the remaining string after
     * `next`. Otherwise, it returns `None` and the original input string.
     *
     * @param <T>  the type of the parsed result
     * @param p    the parser to apply first
     * @param next the string that must follow the pattern matched by `p`
     * @return a new parser that represents the "after" operation
     */
    public static <T> MonadicParser<T> after(MonadicParser<T> p, String next) {
        return input -> {
            Pair<T, String> result = lookahead(p).parse(input);
            String resultStr = result.getFirst().toString();
            if (result.getFirst() != null && input.substring(resultStr.length()).startsWith(next)) {
                return Pair.of(result.getFirst(), result.getSecond().substring(resultStr.length()));
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Parser that matches a pattern 'x' if it is preceded by another pattern 't'.
     *
     * This function takes a parser `p` and a string `previous`. It returns a new
     * parser that, when called with an input string,
     * checks if the input string starts with `previous`. If it does, the new parser
     * applies the parser `p` to the substring of the input string that follows
     * `previous`.
     * If the input string does not start with `previous`, the new parser returns
     * `None` and the original input string.
     *
     * @param <T>      the type of the parsed result
     * @param p        the parser to apply after the `previous` string
     * @param previous the string that must precede the pattern matched by `p`
     * @return a new parser that represents the "before" operation
     */
    public static <T> MonadicParser<T> before(MonadicParser<T> p, String previous) {
        return input -> {
            if (input.startsWith(previous)) {
                return p.parse(input.substring(previous.length()));
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Parser that ignores the result of the given parser.
     *
     * @param <T> the type of the parsed result
     * @param p   the parser to ignore the result of
     * @return a new parser that represents the "ignore" operation
     */
    public static <T> MonadicParser<T> ignore(MonadicParser<T> p) {
        return input -> {
            Pair<T, String> result = p.parse(input);
            return Pair.of(null, result.getSecond());
        };
    }

    /**
     * Parser that ignores the content between two specified strings.
     *
     * @param start the start string to match
     * @param end   the end string to match
     * @return a parser that ignores the content between the specified start and end
     *         strings
     */
    public static MonadicParser<CharSequence> ignoreBetween(String start, String end) {
        return input -> {
            int startIndex = input.indexOf(start);
            int endIndex = input.indexOf(end, startIndex + start.length());
            if (startIndex != -1 && endIndex != -1 && startIndex != endIndex - 1) {
                return Pair.of(null, input.substring(endIndex + end.length()));
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Creates a parser that ignores the content between two specified parsers.
     *
     * This method takes two monadic parsers as arguments. It applies the first
     * parser `start` to the input string
     * to find the start of the content to ignore. Then, it applies the second
     * parser `end` to find the end of the content to ignore.
     * The parser returned by this method will consume the input from the start to
     * the end of the content to ignore,
     * but it will not return any meaningful result. Instead, it will return a pair
     * with `null` as the first element
     * and the remaining string after the end of the content to ignore as the second
     * element.
     *
     * @param start the parser that matches the start of the content to ignore
     * @param end   the parser that matches the end of the content to ignore
     * @return a parser that ignores the content between the specified start and end
     *         parsers
     */
    public static MonadicParser<CharSequence> ignoreBetween(MonadicParser<CharSequence> start,
            MonadicParser<CharSequence> end) {
        return input -> {
            StringBuilder builder = new StringBuilder();
            Pair<CharSequence, String> findStart = until(start).parse(input);
            if (findStart.getFirst() == null) {
                return Pair.of(null, input);
            }
            int counter = 0;
            builder.append(findStart.getFirst().subSequence(0, findStart.getFirst().length() - 1));
            counter = builder.length();
            Pair<CharSequence, String> findRealStart = loop(start).parse(input.substring(counter));
            counter += findRealStart.getFirst().length();
            Pair<CharSequence, String> inBetween = until(end).parse(input.substring(counter));
            counter += inBetween.getFirst().length();
            Pair<CharSequence, String> atEnd = loop(end).parse(input.substring(counter));
            builder.append(atEnd.getSecond());
            return Pair.of(null, builder.toString());
        };
    }

    /**
     * Parser that captures the content between two specified strings.
     *
     * @param start the start string to match
     * @param end   the end string to match
     * @return a parser that captures the content between the specified start and
     *         end strings
     */
    public static MonadicParser<CharSequence> captureBetween(String start, String end) {
        return input -> {
            int startIndex = input.indexOf(start);
            int endIndex = input.indexOf(end, startIndex + start.length());
            if (startIndex != -1 && endIndex != -1) {
                String result = input.substring(startIndex + start.length(), endIndex);
                return Pair.of(result, input.substring(endIndex + end.length()));
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Creates a parser that captures the content between two specified parsers.
     *
     * This method takes two monadic parsers as arguments. It applies the first
     * parser `start` to the input string
     * to find the start of the content to capture. Then, it applies the second
     * parser `end` to find the end of the content to capture.
     * The parser returned by this method will consume the input from the start to
     * the end of the content to capture,
     * and it will return a pair with `null` as the first element and the captured
     * content as the second element.
     *
     * @param start the parser that matches the start of the content to capture
     * @param end   the parser that matches the end of the content to capture
     * @return a parser that captures the content between the specified start and
     *         end parsers
     */
    public static MonadicParser<CharSequence> captureBetween(MonadicParser<CharSequence> start,
            MonadicParser<CharSequence> end) {
        return input -> {
            StringBuilder builder = new StringBuilder();
            Pair<CharSequence, String> findStart = until(start).parse(input);
            if (findStart.getFirst() == null) {
                return Pair.of(null, input);
            }
            int counter = findStart.getFirst().length();
            Pair<CharSequence, String> findRealStart = loop(start).parse(input.substring(counter));
            if (findRealStart.getFirst() != null) {
                counter += findRealStart.getFirst().length();
            }
            Pair<CharSequence, String> inBetween = loop(negate(end)).parse(input.substring(counter));
            if (inBetween.getFirst() != null) {
                counter += inBetween.getFirst().length();
                builder.append(inBetween.getFirst());
            }
            return Pair.of(null, builder.toString());
        };
    }

    /**
     * Performs a lookahead with a parser for a specified number of characters
     * without consuming any input.
     *
     * @param n the number of characters to peek ahead
     * @return a parser that returns the first `n` characters of the input without
     *         consuming any input
     */
    public static MonadicParser<CharSequence> peek(int n) {
        return input -> {
            if (input.length() >= n) {
                return Pair.of(input.substring(0, n), input);
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Returns a parser that advances the input string by a specified number of
     * characters.
     *
     * @param n the number of characters to advance in the input string
     * @return a parser that advances the input string by `n` characters
     */
    public static MonadicParser<CharSequence> goTo(int n) {
        return input -> {
            if (n < input.length()) {
                return Pair.of(null, input.substring(n));
            } else {
                return Pair.of(null, "");
            }
        };
    }

    /**
     * Applies a parser only if a specified condition is met.
     *
     * @param p         the parser to apply
     * @param condition a function that takes the result of the parser and the
     *                  remaining string, and returns a boolean value
     * @return a parser that applies the given parser and checks the condition
     */
    public static <T> MonadicParser<T> ifCondition(MonadicParser<T> p, Predicate<Pair<T, String>> condition) {
        return input -> {
            Pair<T, String> result = p.parse(input);
            if (condition.test(result)) {
                return result;
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Applies a parser only if a specified condition is met.
     *
     * @param p         the parser to apply
     * @param condition a function that takes the input string and returns a boolean
     *                  value
     * @return a parser that applies the given parser and checks the condition
     */
    public static <T> MonadicParser<T> applyIf(MonadicParser<T> p, Predicate<String> condition) {
        return input -> {
            if (condition.test(input)) {
                return p.parse(input);
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Applies a parser only if a specified condition is not met.
     *
     * @param p         the parser to apply
     * @param condition a function that takes the result of the parser and the
     *                  remaining string, and returns a boolean value
     * @return a parser that applies the given parser and checks the condition
     */
    public static <T> MonadicParser<T> elseCondition(MonadicParser<T> p, Predicate<Pair<T, String>> condition) {
        return input -> {
            Pair<T, String> result = p.parse(input);
            if (!condition.test(result)) {
                return result;
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Applies a parser only if a specified condition is not met.
     *
     * @param p         the parser to apply
     * @param condition a function that takes the input string and returns a boolean
     *                  value
     * @return a parser that applies the given parser and checks the condition
     */
    public static <T> MonadicParser<T> applyElse(MonadicParser<T> p, Predicate<String> condition) {
        return input -> {
            if (!condition.test(input)) {
                return p.parse(input);
            } else {
                return Pair.of(null, input);
            }
        };
    }

    /**
     * Parser that matches characters until a specified parser succeeds.
     *
     * @param p the parser to apply
     * @return a parser that matches characters until the specified parser succeeds
     */
    public static MonadicParser<CharSequence> until(MonadicParser<CharSequence> p) {
        return input -> {
            StringBuilder finalResult = new StringBuilder();
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = negate(p).parse(rest);
                if (result.getFirst() != null) {
                    finalResult.append(result.getFirst());
                    rest = result.getSecond();
                } else {
                    Pair<CharSequence, String> endResult = p.parse(rest);
                    return Pair.of(finalResult.toString() + endResult.getFirst(), endResult.getSecond());
                }
            }
            return Pair.of(null, "");
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * succeed.
     *
     * @param parsers a variable number of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         succeed
     */
    public static MonadicParser<CharSequence> untilN(MonadicParser<CharSequence>... parsers) {
        if (parsers.length == 0) {
            throw new IllegalArgumentException("Please provide at least one parser");
        }
        return input -> {
            if (parsers.length == 2) {
                return until(choice(parsers[0], parsers[1])).parse(input);
            } else {
                MonadicParser<CharSequence> finalExpression = parsers[0];
                for (int index = 1; index < parsers.length; index++) {
                    finalExpression = choice(finalExpression, parsers[index]);
                }
                return until(finalExpression).parse(input);
            }
        };
    }

    /**
     * Parser that matches characters until a specified parser succeeds,
     * returning the result of the specified parser.
     *
     * @param p the parser to apply
     * @return a parser that matches characters until the specified parser succeeds
     *         and returns the result of the specified parser
     */
    public static MonadicParser<CharSequence> untilAt(MonadicParser<CharSequence> p) {
        return input -> {
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = negate(p).parse(rest);
                if (result.getFirst() == null) {
                    Pair<CharSequence, String> endResult = p.parse(rest);
                    return endResult;
                }
                rest = result.getSecond();
            }
            return Pair.of(null, "");
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * succeed,
     * returning the result of the first successful parser.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         succeed
     *         and returns the result of the first successful parser
     */
    public static MonadicParser<CharSequence> untilAtN(MonadicParser<CharSequence>... parsers) {
        if (parsers.length == 0) {
            throw new IllegalArgumentException("Please provide at least one parser");
        }
        return input -> {
            if (parsers.length == 2) {
                return untilAt(choice(parsers[0], parsers[1])).parse(input);
            } else {
                MonadicParser<CharSequence> finalExpression = parsers[0];
                for (int index = 1; index < parsers.length; index++) {
                    finalExpression = choice(finalExpression, parsers[index]);
                }
                return untilAt(finalExpression).parse(input);
            }
        };
    }

    /**
     * Parser that matches characters until a specified parser succeeds,
     * without consuming the input matched by the specified parser.
     *
     * @param p the parser to apply
     * @return a parser that matches characters until the specified parser succeeds,
     *         without consuming the input matched by the specified parser
     */
    public static MonadicParser<CharSequence> untilLookahead(MonadicParser<CharSequence> p) {
        return input -> {
            StringBuilder finalResult = new StringBuilder();
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = negate(p).parse(rest);
                if (result.getFirst() != null) {
                    finalResult.append(result.getFirst());
                    rest = result.getSecond();
                } else {
                    Pair<CharSequence, String> endResult = p.parse(rest);
                    return Pair.of(finalResult.toString() + endResult.getFirst(), input);
                }
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * succeed,
     * without consuming the input matched by the specified parser,
     * and returns the result of the first successful parser.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         succeed,
     *         without consuming the input matched by the specified parser,
     *         and returns the result of the first successful parser
     */
    public static MonadicParser<CharSequence> untilLookaheadN(MonadicParser<CharSequence>... parsers) {
        if (parsers.length == 0) {
            throw new IllegalArgumentException("Please provide at least one parser");
        }
        return input -> {
            if (parsers.length == 2) {
                return untilLookahead(choice(parsers[0], parsers[1])).parse(input);
            } else {
                MonadicParser<CharSequence> finalExpression = parsers[0];
                for (int index = 1; index < parsers.length; index++) {
                    finalExpression = choice(finalExpression, parsers[index]);
                }
                return untilLookahead(finalExpression).parse(input);
            }
        };
    }

    /**
     * Parser that matches characters until a specified parser succeeds,
     * without consuming the input matched by the specified parser,
     * and returns the result of the specified parser.
     *
     * @param p the parser to apply
     * @return a parser that matches characters until the specified parser succeeds,
     *         without consuming the input matched by the specified parser,
     *         and returns the result of the specified parser
     */
    public static MonadicParser<CharSequence> untilLookaheadAt(MonadicParser<CharSequence> p) {
        return input -> {
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = negate(p).parse(rest);
                if (result.getFirst() == null) {
                    Pair<CharSequence, String> endResult = p.parse(rest);
                    return Pair.of(endResult.getFirst(), input);
                }
                rest = result.getSecond();
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * succeed,
     * without consuming the input matched by the specified parser,
     * and returns the result of the first successful parser.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         succeed,
     *         without consuming the input matched by the specified parser,
     *         and returns the result of the first successful parser
     */
    public static MonadicParser<CharSequence> untilLookaheadAtN(MonadicParser<CharSequence>... parsers) {
        if (parsers.length == 0) {
            throw new IllegalArgumentException("Please provide at least one parser");
        }
        return input -> {
            if (parsers.length == 2) {
                return untilLookaheadAt(choice(parsers[0], parsers[1])).parse(input);
            } else {
                MonadicParser<CharSequence> finalExpression = parsers[0];
                for (int index = 1; index < parsers.length; index++) {
                    finalExpression = choice(finalExpression, parsers[index]);
                }
                return untilLookaheadAt(finalExpression).parse(input);
            }
        };
    }

    /**
     * Parser that repeatedly applies a specified parser until it fails.
     *
     * @param p the parser to apply
     * @return a parser that repeatedly applies the specified parser until it fails
     */
    public static MonadicParser<CharSequence> loop(MonadicParser<CharSequence> p) {
        return input -> {
            StringBuilder finalResult = new StringBuilder();
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = p.parse(rest);
                if (result.getFirst() != null) {
                    finalResult.append(result.getFirst());
                    rest = result.getSecond();
                } else {
                    return Pair.of(finalResult.toString(), rest);
                }
            }
            return Pair.of(null, "");
        };
    }

    /**
     * Parser that repeatedly applies a specified parser until it fails,
     * returning the result of the specified parser.
     *
     * @param p the parser to apply
     * @return a parser that repeatedly applies the specified parser until it fails,
     *         and returns the result of the negation of the specified parser
     */
    public static MonadicParser<CharSequence> loopAt(MonadicParser<CharSequence> p) {
        return input -> {
            CharSequence finalResult = null;
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = p.parse(rest);
                if (result.getFirst() != null) {
                    finalResult = result.getFirst();
                    rest = result.getSecond();
                } else {
                    return Pair.of(finalResult, rest);
                }
            }
            return Pair.of(null, "");
        };
    }

    /**
     * Parser that repeatedly applies a specified parser until it fails,
     * without consuming the input matched by the specified parser.
     *
     * @param p the parser to apply
     * @return a parser that repeatedly applies the specified parser until it fails,
     *         without consuming the input matched by the specified parser
     */
    public static MonadicParser<CharSequence> loopLookahead(MonadicParser<CharSequence> p) {
        return input -> {
            StringBuilder finalResult = new StringBuilder();
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = p.parse(rest);
                if (result.getFirst() != null) {
                    finalResult.append(result.getFirst());
                    rest = result.getSecond();
                } else {
                    return Pair.of(finalResult.toString(), input);
                }
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Parser that repeatedly applies a specified parser until it fails,
     * without consuming the input matched by the specified parser,
     * and returns the result of the specified parser.
     *
     * @param p the parser to apply
     * @return a parser that matches characters until the specified parser succeeds,
     *         without consuming the input matched by the specified parser,
     *         and returns the result of the specified parser
     */
    public static MonadicParser<CharSequence> loopLookaheadAt(MonadicParser<CharSequence> p) {
        return input -> {
            CharSequence finalResult = null;
            String rest = input;
            while (!rest.isEmpty()) {
                Pair<CharSequence, String> result = p.parse(rest);
                if (result.getFirst() != null) {
                    finalResult = result.getFirst();
                    rest = result.getSecond();
                } else {
                    return Pair.of(finalResult, input);
                }
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * fail.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         fail,
     *         returning the combined result of all parsers that were applied before
     *         the first failure
     */
    public static MonadicParser<List<CharSequence>> seq(MonadicParser<CharSequence>... parsers) {
        return input -> {
            List<CharSequence> results = new ArrayList<>();
            String rest = input;
            for (MonadicParser<CharSequence> parser : parsers) {
                Pair<CharSequence, String> result = parser.parse(rest);
                if (result.getFirst() != null) {
                    results.add(result.getFirst());
                    rest = result.getSecond();
                } else {
                    break;
                }
            }
            return Pair.of(results, rest);
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * fail,
     * returning the result of the negation of the first failing parser.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         fail,
     *         returning the result of the negation of the first failing parser
     */
    public static MonadicParser<List<CharSequence>> seqAt(MonadicParser<CharSequence>... parsers) {
        return input -> {
            List<CharSequence> results = new ArrayList<>();
            String rest = input;
            for (MonadicParser<CharSequence> parser : parsers) {
                Pair<CharSequence, String> result = untilAt(parser).parse(rest);
                if (result.getFirst() != null) {
                    results.add(result.getFirst());
                    rest = result.getSecond();
                } else {
                    break;
                }
            }
            return Pair.of(results, rest);
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * fail,
     * without consuming the input matched by the specified parser,
     * and returns the result of the first successful parser.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         fail,
     *         without consuming the input matched by the specified parser,
     *         and returns the result of the first successful parser
     */
    public static MonadicParser<List<CharSequence>> seqLookahead(MonadicParser<CharSequence>... parsers) {
        return input -> {
            List<CharSequence> results = new ArrayList<>();
            String rest = input;
            for (MonadicParser<CharSequence> parser : parsers) {
                Pair<CharSequence, String> result = parser.parse(rest);
                if (result.getFirst() != null) {
                    results.add(result.getFirst());
                } else {
                    break;
                }
                rest = result.getSecond();
            }
            return Pair.of(results, input);
        };
    }

    /**
     * Applies a sequence of parsers to the input string until any of the parsers
     * fail,
     * without consuming the input matched by the specified parser,
     * and returns the result of the first successful parser.
     *
     * @param parsers a list of parsers to apply to the input string
     * @return a parser that matches characters until any of the specified parsers
     *         fail,
     *         without consuming the input matched by the specified parser,
     *         and returns the result of the first successful parser
     */
    public static MonadicParser<List<CharSequence>> seqLookaheadAt(MonadicParser<CharSequence>... parsers) {
        return input -> {
            List<CharSequence> results = new ArrayList<>();
            String rest = input;
            for (MonadicParser<CharSequence> parser : parsers) {
                Pair<CharSequence, String> result = untilAt(parser).parse(rest);
                if (result.getFirst() != null) {
                    results.add(result.getFirst());
                } else {
                    break;
                }
                rest = result.getSecond();
            }
            return Pair.of(results, input);
        };
    }

    /**
     * Creates a parser that matches a pattern if it is preceded by another pattern.
     *
     * This method takes two monadic parsers as arguments. It applies the second
     * parser `previous` to the input string.
     * If the second parser succeeds, it then applies the first parser `p` to the
     * remaining string after the second parser's match.
     * If the first parser also succeeds, the method returns the result of the first
     * parser and the remaining string after the first parser's match.
     * Otherwise, it returns a pair with `null` as the first element and the
     * original input string as the second element.
     *
     * @param <T>      the type of the result produced by the parsers
     * @param p        the first parser to apply, which is applied to the remaining
     *                 string after the second parser's match
     * @param previous the second parser to apply, which is applied to the input
     *                 string first
     * @return a new monadic parser that represents the "before" operation. This
     *         parser matches a pattern if it is preceded by another pattern.
     */
    public static <T> MonadicParser<T> before(MonadicParser<T> p, MonadicParser<T> previous) {
        return input -> {
            Pair<T, String> previousResult = previous.parse(input);
            if (previousResult.getFirst() != null) {
                Pair<T, String> result = p.parse(input.substring(previousResult.getSecond().length()));
                if (result.getFirst() != null) {
                    return result;
                }
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Creates a parser that matches a pattern if it is followed by another pattern.
     *
     * This method takes two monadic parsers as arguments. It applies the first
     * parser `p` to the input string.
     * If the first parser succeeds, it then applies the second parser `after` to
     * the remaining string after the first parser's match.
     * If the second parser also succeeds, the method returns a pair with the result
     * of the first parser and the remaining string after the second parser's match.
     * Otherwise, it returns a pair with `null` as the first element and the
     * original input string as the second element.
     *
     * @param <T>   the type of the result produced by the parsers
     * @param p     the first parser to apply, which is applied to the input string
     *              first
     * @param after the second parser to apply, which is applied to the remaining
     *              string after the first parser's match
     * @return a new monadic parser that represents the "after" operation. This
     *         parser matches a pattern if it is followed by another pattern.
     */
    public static <T> MonadicParser<T> after(MonadicParser<T> p, MonadicParser<T> after) {
        return input -> {
            Pair<T, String> result = p.parse(input);
            if (result.getFirst() != null) {
                Pair<T, String> afterResult = after.parse(result.getSecond());
                if (afterResult.getFirst() != null) {
                    return Pair.of(result.getFirst(), afterResult.getSecond());
                }
            }
            return Pair.of(null, input);
        };
    }

    /**
     * Creates a parser that wraps the result of the given parser in a list.
     *
     * @param <T> the type of the parsed result
     * @param p   the parser to wrap the result in a list
     * @return a new parser that applies the given parser and wraps the result in a
     *         list
     */
    public static <T> MonadicParser<List<T>> pack(MonadicParser<T> p) {
        return input -> {
            Pair<T, String> pair = p.parse(input);
            if (pair.getFirst() == null) {
                return Pair.of(new ArrayList<T>(), pair.getSecond());
            }
            List<T> results = new ArrayList<>();
            results.add(pair.getFirst());
            return Pair.of(results, pair.getSecond());
        };
    }

    /**
     * Creates a parser that unpacks the result of the given parser from a list.
     *
     * @param <T> the type of the parsed result
     * @param p   the parser that produces a list of results
     * @return a new parser that applies the given parser and unpacks the result
     *         from a list
     * @throws IllegalArgumentException if the list contains more than one element
     */
    public static <T> MonadicParser<T> unpack(MonadicParser<List<T>> p) throws IllegalArgumentException {
        return input -> {
            Pair<List<T>, String> pair = p.parse(input);
            List<T> results = pair.getFirst();
            if (results == null || results.isEmpty()) {
                return Pair.of(null, pair.getSecond());
            } else if (results.size() == 1) {
                return Pair.of(results.get(0), pair.getSecond());
            } else {
                throw new IllegalArgumentException(
                        "Expected a list with at most one element, but found " + results.size() + " elements.");
            }
        };
    }
}