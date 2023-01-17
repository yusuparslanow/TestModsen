package org.example;

import lombok.RequiredArgsConstructor;
import org.example.calculator.CurrencyCalc;
import org.example.exchange.ExchangeService;
import org.example.exchange.FakeExchangedService;
import org.example.parser.Coin;
import org.example.parser.DollarParser;
import org.example.parser.RubleParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

@RequiredArgsConstructor
public class Main {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final CurrencyCalc calc;

    public void run() throws IOException {
        try(var scanner = new Scanner(inputStream)) {
            try (var writer = new OutputStreamWriter(outputStream)) {
                writer.write("Type formula and press enter.\n");
                writer.flush();
                String line;
                while (scanner.hasNextLine() && !(line = scanner.nextLine()).equals("")) {
                    try {
                        var res = calc.calculate(line);
                        writer.write(String.format("%s = %s\n", line, res.toString()));
                    }
                    catch (Exception e) {
                        writer.write(e.getMessage() + "\n");
                    }
                    finally {
                        writer.flush();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            ExchangeService service = new FakeExchangedService();
            var currencies = service.getCurrencyRate();

            var calc = CurrencyCalc
                    .builder()
                    .parser(new DollarParser())
                    .parser(new RubleParser())
                    .customFunction("toDollar", (vars) -> {
                        var coin = vars[0];
                        if (coin.getSign() == '$')
                            return coin;
                        var rate = currencies.get(coin.getCode() + "USD");
                        return new Coin(coin.getVal() * rate, '$', "USD");
                    })
                    .customFunction("toRuble", (vars) -> {
                        var coin = vars[0];
                        if (coin.getSign() == 'p')
                            return coin;
                        var rate = currencies.get(coin.getCode() + "RUB");
                        return new Coin(coin.getVal() * rate, 'p', "RUB");
                    })
                    .build();
            new Main(System.in, System.out, calc).run();
        }
        catch (Exception e) {
            System.out.println("Exception error.");
            System.out.println(e.getMessage());
        }
    }
}