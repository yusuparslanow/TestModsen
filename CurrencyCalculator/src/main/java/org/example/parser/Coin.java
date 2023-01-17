package org.example.parser;

import lombok.Data;

@Data
public class Coin implements Comparable<Coin> {
    private final double val;
    private final char sign;
    private final String code;

    @Override
    public int compareTo(Coin coin) {
        if (this.sign != coin.sign)
            throw new IllegalArgumentException("Cannot compare coins with different sign.");
        return Double.compare(val, coin.getVal());
    }

    @Override
    public String toString() {
        return String.format("%.2f %c", val, sign);
    }
}
