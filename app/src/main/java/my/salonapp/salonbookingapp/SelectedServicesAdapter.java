package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class SelectedServicesAdapter extends ArrayAdapter<Service> {

    private final Activity context;
    private final ArrayList<Service> services;

    static class ViewHolder {
        public TextView tvServiceName;
        public TextView tvServicePrice;
        public TextView tvServiceDuration;
    }

    public SelectedServicesAdapter(Activity context, ArrayList<Service> services) {
        super(context, R.layout.listview_selectedservices, services);
        this.context = context;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        Service service = services.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_selectedservices, parent, false);

            SelectedServicesAdapter.ViewHolder viewHolder = new SelectedServicesAdapter.ViewHolder();

            viewHolder.tvServiceName = rowView.findViewById(R.id.tvServiceName);
            viewHolder.tvServicePrice = rowView.findViewById(R.id.tvServicePrice);
            viewHolder.tvServiceDuration = rowView.findViewById(R.id.tvServiceDuration);

            rowView.setTag(viewHolder);

            viewHolder.tvServiceName.setTag(service);
            viewHolder.tvServicePrice.setTag(service);
            viewHolder.tvServiceDuration.setTag(service);
        } else {
            rowView = convertView;
            ((SelectedServicesAdapter.ViewHolder) rowView.getTag()).tvServiceName.setTag(service);
            ((SelectedServicesAdapter.ViewHolder) rowView.getTag()).tvServicePrice.setTag(service);
            ((SelectedServicesAdapter.ViewHolder) rowView.getTag()).tvServiceDuration.setTag(service);
        }

        SelectedServicesAdapter.ViewHolder holder = (SelectedServicesAdapter.ViewHolder) rowView.getTag();

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
