package org.example.parser;

public class DollarParser implements CoinParser{
    @Override
    public Coin parse(String token) {
        if (token.charAt(0) != '$')
            return null;
        return new Coin(Double.parseDouble(token.substring(1)), '$', "USD");
    }
}
