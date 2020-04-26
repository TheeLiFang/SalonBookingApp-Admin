package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionHistoriesAdapter extends ArrayAdapter<TransactionHistory> {

    private final Activity context;
    private final ArrayList<TransactionHistory> transactionHistories;

    static class ViewHolder {
        public TextView tvTransactionHistoryDate;
        public TextView tvTransactionHistoryRefNo;
        public TextView tvTransactionHistoryClient;
        public TextView tvTransactionHistoryStaff;
    }

    public TransactionHistoriesAdapter(Activity context, ArrayList<TransactionHistory> transactionHistories) {
        super(context, R.layout.listview_transactionhistory, transactionHistories);
        this.context = context;
        this.transactionHistories = transactionHistories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        TransactionHistory transactionHistory = transactionHistories.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_transactionhistory, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvTransactionHistoryDate = rowView.findViewById(R.id.tvTransactionHistoryDate);
            viewHolder.tvTransactionHistoryRefNo = rowView.findViewById(R.id.tvTransactionHistoryRefNo);
            viewHolder.tvTransactionHistoryClient = rowView.findViewById(R.id.tvTransactionHistoryClient);
            viewHolder.tvTransactionHistoryStaff = rowView.findViewById(R.id.tvTransactionHistoryStaff);

            rowView.setTag(viewHolder);

            viewHolder.tvTransactionHistoryDate.setTag(transactionHistory);
            viewHolder.tvTransactionHistoryRefNo.setTag(transactionHistory);
            viewHolder.tvTransactionHistoryClient.setTag(transactionHistory);
            viewHolder.tvTransactionHistoryStaff.setTag(transactionHistory);
        } else {
            rowView = convertView;

            ((ViewHolder) rowView.getTag()).tvTransactionHistoryDate.setTag(transactionHistory);
            ((ViewHolder) rowView.getTag()).tvTransactionHistoryRefNo.setTag(transactionHistory);
            ((ViewHolder) rowView.getTag()).tvTransactionHistoryClient.setTag(transactionHistory);
            ((ViewHolder) rowView.getTag()).tvTransactionHistoryStaff.setTag(transactionHistory);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvTransactionHistoryDate.setText(simpleDateFormat.format(transactionHistory.getBookingDate()));
        holder.tvTransactionHistoryRefNo.setText(transactionHistory.getRefNo());
        holder.tvTransactionHistoryClient.setText(transactionHistory.getClientName());
        holder.tvTransactionHistoryStaff.setText(transactionHistory.getStaffName());

        return rowView;
    }
}
