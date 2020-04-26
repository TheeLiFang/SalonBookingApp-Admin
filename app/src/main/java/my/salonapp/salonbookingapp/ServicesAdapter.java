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

public class ServicesAdapter extends ArrayAdapter<Service> {

    private final Activity context;
    private final ArrayList<Service> services;

    static class ViewHolder {
        public TextView tvServiceName;
        public TextView tvServicePrice;
        public TextView tvServiceDuration;
    }

    public ServicesAdapter(Activity context, ArrayList<Service> services) {
        super(context, R.layout.listview_service, services);
        this.context = context;
        this.services = services;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        Service service = services.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_service, parent, false);

            ServicesAdapter.ViewHolder viewHolder = new ServicesAdapter.ViewHolder();

            viewHolder.tvServiceName = rowView.findViewById(R.id.tvServiceName);
            viewHolder.tvServicePrice = rowView.findViewById(R.id.tvServicePrice);
            viewHolder.tvServiceDuration = rowView.findViewById(R.id.tvServiceDuration);

            rowView.setTag(viewHolder);

            viewHolder.tvServiceName.setTag(service);
            viewHolder.tvServicePrice.setTag(service);
            viewHolder.tvServiceDuration.setTag(service);
        } else {
            rowView = convertView;
            ((ViewHolder) rowView.getTag()).tvServiceName.setTag(service);
            ((ViewHolder) rowView.getTag()).tvServicePrice.setTag(service);
            ((ViewHolder) rowView.getTag()).tvServiceDuration.setTag(service);
        }

        ServicesAdapter.ViewHolder holder = (ServicesAdapter.ViewHolder) rowView.getTag();

        holder.tvServiceName.setText(service.getServiceName());
        holder.tvServicePrice.setText(String.format("%1$s %2$.2f",
                context.getString(R.string.service_price_currency),
                service.getServicePrice()));
        holder.tvServiceDuration.setText(String.format("%1$s %2$s",
                Integer.toString(service.getServiceDuration()),
                context.getString(R.string.service_duration_minutes)));

        return rowView;
    }
}
