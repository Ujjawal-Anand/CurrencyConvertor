package in.UsCoolLabs.currencyconvertor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.UsCoolLabs.currencyconvertor.R;
import in.UsCoolLabs.currencyconvertor.database.DatabaseHelper;
import in.UsCoolLabs.currencyconvertor.model.Currency;

/**
 * Created by ujjawal on 9/10/17.
 *
 */

public class CurrencyAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Currency> currencies;


    private class ViewHolder {
        public TextView currencyNameView;
    }

    public CurrencyAdapter(Context context) {
        this.context = context;
        DatabaseHelper helper = DatabaseHelper.getInstance(context);
        this.currencies = helper.getCurrencies();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public Object getItem(int position) {
        return currencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_currency_list, parent, false);
            holder = new ViewHolder();
            holder.currencyNameView = convertView.findViewById(R.id.text_currency_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Currency currency = currencies.get(position);
        if(position == 0) {
            holder.currencyNameView.setText("Choose Currency");
        } else {
            holder.currencyNameView.setText(currency.getName());
        }
        return convertView;

    }
}

