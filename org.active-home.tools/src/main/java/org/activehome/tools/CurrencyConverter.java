package org.activehome.tools;

/*
 * #%L
 * Active Home :: Tools
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 org.active-home
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class facilitate currency conversion.
 *
 * @author Jacky Bourgeois
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
