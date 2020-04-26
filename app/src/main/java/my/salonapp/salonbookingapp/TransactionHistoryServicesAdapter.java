package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class TransactionHistoryServicesAdapter extends ArrayAdapter<TransactionHistoryService>  {

    private final Activity context;
    private final ArrayList<TransactionHistoryService> transactionHistoryServices;

    static class ViewHolder {
        public TextView tvTransactionHistoryServiceName;
        public TextView tvTransactionHistoryServicePrice;
    }

    public TransactionHistoryServicesAdapter(Activity context, ArrayList<TransactionHistoryService> transactionHistoryServices) {
        super(context, R.layout.listview_transactionhistoryservice, transactionHistoryServices);
        this.context = context;
        this.transactionHistoryServices = transactionHistoryServices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        TransactionHistoryService service = transactionHistoryServices.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_transactionhistoryservice, parent, false);

            TransactionHistoryServicesAdapter.ViewHolder viewHolder = new TransactionHistoryServicesAdapter.ViewHolder();

            viewHolder.tvTransactionHistoryServiceName = rowView.findViewById(R.id.tvTransactionHistoryServiceName);
            viewHolder.tvTransactionHistoryServicePrice = rowView.findViewById(R.id.tvTransactionHistoryServicePrice);

            rowView.setTag(viewHolder);

            viewHolder.tvTransactionHistoryServiceName.setTag(service);
            viewHolder.tvTransactionHistoryServicePrice.setTag(service);
        } else {
            rowView = convertView;
            ((TransactionHistoryServicesAdapter.ViewHolder) rowView.getTag()).tvTransactionHistoryServiceName.setTag(service);
            ((TransactionHistoryServicesAdapter.ViewHolder) rowView.getTag()).tvTransactionHistoryServicePrice.setTag(service);
        }

        TransactionHistoryServicesAdapter.ViewHolder holder = (TransactionHistoryServicesAdapter.ViewHolder) rowView.getTag();

        holder.tvTransactionHistoryServiceName.setText(service.getServiceName());
        holder.tvTransactionHistoryServicePrice.setText(String.format("%1$s %2$.2f",
                context.getString(R.string.service_price_currency),
                service.getServicePrice()));

        return rowView;
    }
}
