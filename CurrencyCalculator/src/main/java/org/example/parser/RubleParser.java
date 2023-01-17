package org.example.parser;

public class RubleParser implements CoinParser{
    @Override
    public Coin parse(String token) {
        if (token.charAt(token.length() - 1) != 'p'  && token.charAt(token.length() - 1) !=  'p')
            return null;
        return new Coin(Double.parseDouble(token.substring(0, token.length()-1)), 'p', "RUB");
    }
}
