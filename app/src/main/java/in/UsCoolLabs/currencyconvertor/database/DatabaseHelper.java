package in.UsCoolLabs.currencyconvertor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.UsCoolLabs.currencyconvertor.model.Currency;
import nl.qbusict.cupboard.convert.EntityConverter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by ujjawal on 9/10/17.
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLHelper";

    private static final String DATABASE_NAME = "currencyconverter.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper instance = null;

    private Context mContext;

    static {
        cupboard().register(Currency.class);
    }

    private DatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = c;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }

        return instance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating DB");
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }

    public void addCurrency(Currency currency) {
        cupboard().withDatabase(getWritableDatabase()).put(currency);
    }

    public void updateCurrency(Currency currency) {
        EntityConverter<Currency> currencyEntityConverter = cupboard().getEntityConverter(Currency.class);
        ContentValues newValues = new ContentValues();
        currencyEntityConverter.toValues(currency, newValues);
        cupboard().withDatabase(getWritableDatabase())
                .update(Currency.class, newValues, "WHERE name=? AND code=?", currency.getName(), currency.getCode());
    }

    public boolean isDatabaseEmpty() {
        int count = cupboard().withDatabase(getReadableDatabase()).query(Currency.class).list().size();
        Log.i(TAG, "Count: " + count);
        return (count <= 0);
    }

    public void addCurrencies(List<Currency> currencies) {

        Log.i(TAG, "Inserting Currencies");

        boolean isEmpty = isDatabaseEmpty();

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        try {
            for (Currency c : currencies) {
                if (isEmpty) {
                    addCurrency(c);
                } else {
                    updateCurrency(c);
                }
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(TAG, "Error inserting currencies " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }

    }

    public List<Currency> getCurrencies() {

        List<Currency> currencies = null;

        try {

            currencies = cupboard().withDatabase(getReadableDatabase())
                    .query(Currency.class)
                    .list();

        } catch (Exception e) {
            Log.e(TAG, "Could not fetch currencies" + e.getLocalizedMessage());
        }

        return currencies;
    }

    public List<Currency> getCurrenciesWithCode(String code) {
        String query = "%" + code + "%";
        List<Currency> currencies = null;

        try {
            currencies = cupboard().withDatabase(getReadableDatabase())
                    .query(Currency.class)
                    .withSelection("code LIKE ? OR name LIKE ?", query, query)
                    .list();
        } catch (Exception e) {
            Log.e(TAG, "Could not fetch currencies" + e.getLocalizedMessage());
        }

        return currencies;

    }

}

