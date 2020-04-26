package my.salonapp.salonbookingapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class BookingServicesAdapter  extends ArrayAdapter<BookingService>  {

    private final Activity context;
    private final ArrayList<BookingService> bookings;

    static class ViewHolder {
        public TextView tvServiceName;
        public TextView tvServicePrice;
    }

    public BookingServicesAdapter(Activity context, ArrayList<BookingService> bookingServices) {
        super(context, R.layout.listview_bookingservice, bookingServices);
        this.context = context;
        this.bookings = bookingServices;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        BookingService service = bookings.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_bookingservice, parent, false);

            BookingServicesAdapter.ViewHolder viewHolder = new BookingServicesAdapter.ViewHolder();

            viewHolder.tvServiceName = rowView.findViewById(R.id.tvServiceName);
            viewHolder.tvServicePrice = rowView.findViewById(R.id.tvServicePrice);

            rowView.setTag(viewHolder);

            viewHolder.tvServiceName.setTag(service);
            viewHolder.tvServicePrice.setTag(service);
        } else {
            rowView = convertView;
            ((BookingServicesAdapter.ViewHolder) rowView.getTag()).tvServiceName.setTag(service);
            ((BookingServicesAdapter.ViewHolder) rowView.getTag()).tvServicePrice.setTag(service);
        }

        BookingServicesAdapter.ViewHolder holder = (BookingServicesAdapter.ViewHolder) rowView.getTag();

        holder.tvServiceName.setText(service.getServiceName());
        holder.tvServicePrice.setText(String.format("%1$s %2$.2f",
                context.getString(R.string.service_price_currency),
                service.getServicePrice()));

        return rowView;
    }
}
