package org.example.exchange;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FakeExchangedService implements ExchangeService {
    public Map<String, Double> getCurrencyRate() throws IOException {
        var mapper = new ObjectMapper();
        var classLoader = Thread.currentThread().getContextClassLoader();
        var inputStream = classLoader.getResourceAsStream("currency_rate.json");
        return mapper.readValue(inputStream, new TypeReference<List<Currency>>() {})
                .stream().collect(Collectors.toMap(Currency::getCurrPair, Currency::getCost));
    }
}
