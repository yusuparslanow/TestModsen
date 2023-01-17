package org.example.calculator;

import lombok.Builder;
import lombok.Singular;
import org.example.parser.Coin;
import org.example.parser.CoinParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
public class CurrencyCalc {
    private static final Set<Character> specChars = Set.of('+', '-', '(', ')');
    private static final Map<Character, CalcOperation> operations = Map.of(
            '+', (var1, var2) -> {
                if (var1.getSign() != var2.getSign())
                    throw new IllegalArgumentException("Cannot add two currency with different signs.");
                return new Coin(var1.getVal() + var2.getVal(), var1.getSign(), var1.getCode());
            },
            '-', (var1, var2) -> {
                if (var1.getSign() != var2.getSign())
                    throw new IllegalArgumentException("Cannot sub two currency with different signs.");
                return new Coin(var1.getVal() - var2.getVal(), var1.getSign(), var1.getCode());
            }
    );

    @Singular
    private final List<CoinParser> parsers;

    @Singular
    private final Map<String, CustomFunction> customFunctions;

    public Coin calculate(String expr) {
        return parse(expr);
    }

    private Coin parse(String expr) {
        return parse(expr, 0);
    }

    private String readArgs(String expr, int start) {
        int index = start;
        if (expr.charAt(index++) != '(')
            throw new IllegalArgumentException("Missed '(' ");
        var buff = new StringBuilder();
        int parenthesisCount = 1;
        while (parenthesisCount > 0 && index < expr.length()) {
            var ch = expr.charAt(index++);
            if (')' == ch) {
                parenthesisCount--;
                if (parenthesisCount == 0)
                    break;
            }
            else if ('(' == ch)
                parenthesisCount++;
            buff.append(ch);
        }
        if (parenthesisCount > 0)
            throw new IllegalArgumentException("Missed ')'");
        return buff.toString();
    }

    private Coin[] parseArgs(String expr) {
        var args = expr.split(",");
        var coins = new Coin[args.length];
        for (int i = 0; i < args.length; i ++) {
            coins[i] = parse(args[i]);
        }
        return coins;
    }

    private Coin parse(String expr, int start) {
        var buff = new StringBuilder();
        Coin left = null;

        for (int i = start; i < expr.length(); i ++) {
            var ch = expr.charAt(i);
            if (' ' == ch)
                continue;
            if (specChars.contains(ch)) {
                 var token = buff.toString();
                 buff.setLength(0);
                 if (null != customFunctions) {
                     var fun = customFunctions.get(token);
                     if (null != fun) {
                         var argsExpr = readArgs(expr, i++);
                         i += argsExpr.length();
                         left = fun.eval(parseArgs(argsExpr));
                         continue;
                     }
                 }
                 var operation = operations.get(ch);
                 if (null == operation)
                     throw new IllegalArgumentException(String.format("Unknown symbol: '%c', pos: %d", ch, i));
                 if (null == left)
                    left = parse(token);
                 var right = parse(expr, ++i);
                 i = expr.length() - 1;
                 left = operation.apply(left, right);
                 continue;
            }
            buff.append(ch);
        }

        if (buff.length() > 0) {
            left = parseCoin(buff.toString());
        }
        return left;
    }

    private Coin parseCoin(String token) {
        for (var parser: parsers) {
            var coin = parser.parse(token);
            if (null != coin)
                return coin;
        }
        throw new IllegalArgumentException(String.format("Unknown token: '%s'", token));
    }
}
