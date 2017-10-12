package in.UsCoolLabs.currencyconvertor.model;

import java.util.HashMap;

import in.UsCoolLabs.currencyconvertor.model.RateResponse;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by ujjawal on 9/10/17.
 *
 */

public interface CurrencyApi {

    @GET("/latest.json")
    public void getRates(@Query("app_id") String key, Callback<RateResponse> callback);

    @GET("/currencies.json")
    public void getCurrencyMappings(@Query("app_id") String key, Callback<HashMap<String, String>> callback);
}