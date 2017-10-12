package in.UsCoolLabs.currencyconvertor.model;

import java.util.HashMap;

/**
 * Created by ujjawal on 9/10/17.
 */

public class RateResponse {
    private long timestamp;
    private HashMap<String, Double> rates;

    public long getTimestamp() {
        return timestamp;
    }

    public HashMap<String, Double> getRates() {
        return rates;
    }
}
