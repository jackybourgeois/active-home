package org.activehome.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class facilitate currency conversion.
 *
 * @author Jacky Bourgeois
 * @version %I%, %G%
 */
public final class CurrencyConverter {

    /**
     * Utility class.
     */
    private CurrencyConverter() {
    }

    public static double checkCurrencyRate(Currency from, Currency to) {
        try {
            URL url = new URL("http://rate-exchange.appspot.com/currency?from=" + from.name() + "&to=" + to.name());
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            return Double.valueOf(br.readLine().split(",")[1].substring(9));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    enum Currency {
        USD, EUR, GBP
    }
}
