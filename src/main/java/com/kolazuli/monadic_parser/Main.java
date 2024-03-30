package com.kolazuli.monadic_parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        //MonadicParser<List<CharSequence>> calculatorParser = Parser.seqAt(Parser.numberParser(),
        //        Parser.operatorParser(), Parser.numberParser());
        //List<Integer> calculator = new ArrayList<>();
        //MonadicParser<List<CharSequence>> parser = Parser.matcher(calculatorParser, r -> {
        //    List<CharSequence> list = r.getResult();
        //    if (list.size() != 3) {
        //        return;
        //    }
        //    Integer x = Integer.parseInt(list.get(0).toString());
        //    Integer y = Integer.parseInt(list.get(2).toString());
        //    List<Integer> state = r.getState();
        //    switch (list.get(1).charAt(0)) {
        //        case '+':
        //            state.add(x + y);
        //            break;
        //        case '-':
        //            state.add(x - y);
        //            break;
        //        case '*':
        //            state.add(x * y);
        //            break;
        //        case '/':
        //            state.add(x / y);
        //            break;
        //        case '%':
        //            state.add(x % y);
        //            break;
        //        default:
        //            throw new IllegalArgumentException("Unexpected operator");
        //    }
        //}, r -> {
        //    if (!r.getRemaining().isEmpty()) {
        //        throw new IllegalArgumentException("Could not parse the input.");
        //    }
        //    System.out.println(r.getState());
        //}, calculator);
        //Scanner scanner = new Scanner();
        //MonadicParser<List<List<CharSequence>>> calculatorParserWithNewLine = Parser
        //        .oneOrMore(Parser.choice(parser, Parser.pack(Parser.newLineParser())));
        //MonadicParser<List<List<CharSequence>>> tracker = Parser.track(scanner, calculatorParserWithNewLine);
        //System.out.println(tracker.parse("1 + 1\n 8 * 8\n    878 * 677\n"));
        //System.out.println(scanner);
        //System.out.println(calculator);
        Map<String, Integer> map = new HashMap<>();
        map.put("+", 1);
        map.put("-", 2);
        map.put("*", 3);
        map.put("/", 3);
        MonadicParser<Integer> parser = Parser.operatorPrecedenceParser(map);
        System.out.println(parser.parse("+"));
    }
}