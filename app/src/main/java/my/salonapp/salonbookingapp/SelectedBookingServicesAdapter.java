package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class SelectedBookingServicesAdapter extends ArrayAdapter<BookingService> {

    private final Activity context;
    private final ArrayList<BookingService> services;

    static class ViewHolder {
        public TextView tvServiceName;
        public TextView tvServicePrice;
        public TextView tvServiceDuration;
    }

    public SelectedBookingServicesAdapter(Activity context, ArrayList<BookingService> services) {
        super(context, R.layout.listview_selectedservices, services);
        this.context = context;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        BookingService service = services.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_selectedservices, parent, false);

            SelectedBookingServicesAdapter.ViewHolder viewHolder = new SelectedBookingServicesAdapter.ViewHolder();

            viewHolder.tvServiceName = rowView.findViewById(R.id.tvServiceName);
            viewHolder.tvServicePrice = rowView.findViewById(R.id.tvServicePrice);
            viewHolder.tvServiceDuration = rowView.findViewById(R.id.tvServiceDuration);

            rowView.setTag(viewHolder);

            viewHolder.tvServiceName.setTag(service);
            viewHolder.tvServicePrice.setTag(service);
            viewHolder.tvServiceDuration.setTag(service);
        } else {
            rowView = convertView;
            ((ServicesAdapter.ViewHolder) rowView.getTag()).tvServiceName.setTag(service);
            ((ServicesAdapter.ViewHolder) rowView.getTag()).tvServicePrice.setTag(service);
            ((ServicesAdapter.ViewHolder) rowView.getTag()).tvServiceDuration.setTag(service);
        }

        SelectedBookingServicesAdapter.ViewHolder holder = (SelectedBookingServicesAdapter.ViewHolder) rowView.getTag();

        holder.tvServiceName.setText(service.getServiceName());
        holder.tvServicePrice.setText(String.format("%1$s %2$.2f",
                context.getString(R.string.service_price_currency),
                service.getServicePrice()));
        holder.tvServiceDuration.setText(String.format("%1$s %2$s",
                Integer.toString(service.getServiceDuration()),
                context.getString(R.string.service_duration_minutes_short_form)));

        return rowView;
    }
}
