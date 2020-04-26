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

public class BookingsAdapter extends ArrayAdapter<Booking> {

    private final Activity context;
    private final ArrayList<Booking> bookings;

    static class ViewHolder {
        public TextView tvBookingDate;
        public TextView tvBookingCompany;
        public TextView tvBookingClient;
        public TextView tvBookingStaff;
    }

    public BookingsAdapter(Activity context, ArrayList<Booking> bookings) {
        super(context, R.layout.listview_booking, bookings);
        this.context = context;
        this.bookings = bookings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        Booking booking = bookings.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_booking, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvBookingDate = rowView.findViewById(R.id.tvBookingDate);
            viewHolder.tvBookingCompany = rowView.findViewById(R.id.tvBookingCompany);
            viewHolder.tvBookingClient = rowView.findViewById(R.id.tvBookingClient);
            viewHolder.tvBookingStaff = rowView.findViewById(R.id.tvBookingStaff);

            rowView.setTag(viewHolder);

            viewHolder.tvBookingDate.setTag(booking);
            viewHolder.tvBookingCompany.setTag(booking);
            viewHolder.tvBookingClient.setTag(booking);
            viewHolder.tvBookingStaff.setTag(booking);
        } else {
            rowView = convertView;

            ((ViewHolder) rowView.getTag()).tvBookingDate.setTag(booking);
            ((ViewHolder) rowView.getTag()).tvBookingCompany.setTag(booking);
            ((ViewHolder) rowView.getTag()).tvBookingClient.setTag(booking);
            ((ViewHolder) rowView.getTag()).tvBookingStaff.setTag(booking);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvBookingDate.setText(simpleDateFormat.format(booking.getBookingDate()));
        holder.tvBookingCompany.setText(booking.getCompanyName());
        holder.tvBookingClient.setText(booking.getClientName());
        holder.tvBookingStaff.setText(booking.getStaffName());

        return rowView;
    }
}
