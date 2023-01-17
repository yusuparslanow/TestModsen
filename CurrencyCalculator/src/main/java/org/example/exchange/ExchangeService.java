package org.example.exchange;

import java.io.IOException;
import java.util.Map;

public interface ExchangeService {
    Map<String, Double> getCurrencyRate() throws IOException;
}
