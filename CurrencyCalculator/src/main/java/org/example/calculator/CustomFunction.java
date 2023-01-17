package org.example.calculator;


import org.example.parser.Coin;

@FunctionalInterface
public interface CustomFunction {
    Coin eval(Coin[] coins);
}
