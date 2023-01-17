package org.example.calculator;

import org.example.parser.Coin;
import java.util.function.BiFunction;

public interface CalcOperation extends BiFunction<Coin, Coin, Coin> {

}
