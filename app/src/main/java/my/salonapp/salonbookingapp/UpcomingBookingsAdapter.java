package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class UpcomingBookingsAdapter extends ArrayAdapter<UpcomingBooking>  {

    private final Activity context;
    private final ArrayList<UpcomingBooking> bookings;

    static class ViewHolder {
        public TextView tvBookingTime;
        public TextView tvStaffName;
        public TextView tvClientName;
    }

    public UpcomingBookingsAdapter(Activity context, ArrayList<UpcomingBooking> bookings) {
        super(context, R.layout.listview_upcomingbooking, bookings);
        this.context = context;
        this.bookings = bookings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        UpcomingBooking booking = bookings.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_upcomingbooking, parent, false);

            UpcomingBookingsAdapter.ViewHolder viewHolder = new UpcomingBookingsAdapter.ViewHolder();

            viewHolder.tvBookingTime = rowView.findViewById(R.id.tvBookingTime);
            viewHolder.tvStaffName = rowView.findViewById(R.id.tvStaffName);
            viewHolder.tvClientName = rowView.findViewById(R.id.tvClientName);

            rowView.setTag(viewHolder);

            viewHolder.tvBookingTime.setTag(booking);
            viewHolder.tvStaffName.setTag(booking);
            viewHolder.tvClientName.setTag(booking);
        } else {
            rowView = convertView;

            ((UpcomingBookingsAdapter.ViewHolder) rowView.getTag()).tvBookingTime.setTag(booking);
            ((UpcomingBookingsAdapter.ViewHolder) rowView.getTag()).tvStaffName.setTag(booking);
            ((UpcomingBookingsAdapter.ViewHolder) rowView.getTag()).tvClientName.setTag(booking);
        }

        UpcomingBookingsAdapter.ViewHolder holder = (UpcomingBookingsAdapter.ViewHolder) rowView.getTag();

        holder.tvBookingTime.setText(booking.getBookingTime());
        holder.tvStaffName.setText(booking.getStaffName());
        holder.tvClientName.setText(booking.getClientName());

        return rowView;
    }
}
