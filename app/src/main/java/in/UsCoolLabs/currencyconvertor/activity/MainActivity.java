package in.UsCoolLabs.currencyconvertor.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import in.UsCoolLabs.currencyconvertor.fragment.AboutDialogFragment;
import in.UsCoolLabs.currencyconvertor.model.CurrencyApi;
import in.UsCoolLabs.currencyconvertor.fragment.ErrorDialogFragment;
import in.UsCoolLabs.currencyconvertor.R;
import in.UsCoolLabs.currencyconvertor.adapter.CurrencyAdapter;
import in.UsCoolLabs.currencyconvertor.database.DatabaseHelper;
import in.UsCoolLabs.currencyconvertor.model.Currency;
import in.UsCoolLabs.currencyconvertor.model.RateResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";

    public static final String KEY_TIMESTAMP = "key_timestamp";

    private CurrencyApi mCurrencyApi;
    private DatabaseHelper mHelper;
    private CurrencyAdapter mCurrencyAdapter;

    private HashMap<String, String> mCurrencyMappings;
    private List<Currency> mCurrencies;
    private RateResponse mResponse;

    private EditText mCurrencyEditText;
    private Spinner mFromSpinner;
    private Spinner mToSpinner;
    private Button btnConvert;
    private ProgressBar mProgressBar;

    private String key;
    private Currency mFromCurrency;
    private Currency mToCurrency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        key = getString(R.string.key);

        mCurrencyEditText =  findViewById(R.id.edit_amount);
        mProgressBar =  findViewById(R.id.progress_loading);
        mFromSpinner = findViewById(R.id.spinner_from);
        mToSpinner = findViewById(R.id.spinner_to);
        btnConvert = findViewById(R.id.btn_convert);


        initAdapter();
        initSpinnerOnSelect();
        initBtnOnClick();

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_refresh:
                downloadData();
                return true;

            case R.id.action_about:
                AboutDialogFragment fragment = AboutDialogFragment.newInstance();
                fragment.show(getFragmentManager(), "FRAGMENT_ABOUT");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initAdapter() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://openexchangerates.org/api")
                .build();

        mCurrencyApi = adapter.create(CurrencyApi.class);

        mHelper = DatabaseHelper.getInstance(this);


        if (mHelper.isDatabaseEmpty()) {
            //Download the data
            Log.i(TAG, "Downloading data");

            downloadData();
        } else {
            //Db has data

            Log.i(TAG, "Loading data from db");

            mCurrencyAdapter = new CurrencyAdapter(this);
            mFromSpinner.setAdapter(mCurrencyAdapter);
            mToSpinner.setAdapter(mCurrencyAdapter);
        }
    }

    private void initSpinnerOnSelect() {
        mFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                mFromCurrency = (Currency) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                mToCurrency = (Currency) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initBtnOnClick() {
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrencyEditText.getText().toString().isEmpty()) {
                    createAlertDialog("Amount can't be empty", "Please enter Amount");
                } else if (mFromSpinner.getSelectedItemPosition() == 0) {
                    createAlertDialog("Base currency can't be empty", "Choose a currency from which you wan to convert");
                }
                else if(mToSpinner.getSelectedItemPosition() == 0) {
                    createAlertDialog("Convert currency can't be empty", "Choose a currency to which you want to convert");
                }
                else {
                    Double amount = Double.parseDouble(mCurrencyEditText.getText().toString());
                    double calculatedAmount = Double.parseDouble(new DecimalFormat("##.###")
                            .format(mToCurrency.getRate() * (1 / mFromCurrency.getRate()) * amount));
                    String resultStr = amount + " " + mFromCurrency.getCode() + " = " + calculatedAmount + " "+ mToCurrency.getCode();
                    TextView result =  findViewById(R.id.result);
                    result.setText(String.valueOf(resultStr));

                }
            }
        });
    }



    public void downloadData() {

        if (!isNetworkConnected()) {
            ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(
                    getString(R.string.title_error_no_network)
                    , getString(R.string.message_error_no_network));

            fragment.show(getFragmentManager(), "FRAGMENT_ERROR");
        } else {

            mProgressBar.setVisibility(View.VISIBLE);
            btnConvert.setEnabled(false);

            mCurrencyApi.getCurrencyMappings(key,new Callback<HashMap<String, String>>() {
                @Override
                public void success(HashMap<String, String> hashMaps, Response response) {
                    Log.i(TAG, "Got rates:" + hashMaps.toString());
                    mCurrencyMappings = hashMaps;

                    mCurrencyApi.getRates(key, new Callback<RateResponse>() {
                        @Override
                        public void success(RateResponse rateResponse, Response response) {
                            Log.i(TAG, "Got names: " + rateResponse.getRates().toString());

                            mResponse = rateResponse;

                            Log.i(TAG, "Timestamp: " + rateResponse.getTimestamp());

                            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                            prefs.edit()
                                    .putLong(KEY_TIMESTAMP, rateResponse.getTimestamp())
                                    .apply();

                            if (mCurrencyMappings != null) {
                                mCurrencies = Currency.generateCurrencies(mCurrencyMappings, mResponse);

                                Log.i(TAG, "Generated Currencies: " + Arrays.toString(mCurrencies.toArray()));

                                mHelper.addCurrencies(mCurrencies);
                                initAdapter();

                                mToSpinner.setAdapter(mCurrencyAdapter);

                                mProgressBar.setVisibility(View.GONE);
                                btnConvert.setEnabled(true);

                            }

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, error.getLocalizedMessage());
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, error.getLocalizedMessage());
                }
            });
        }
    }

    private void createAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
