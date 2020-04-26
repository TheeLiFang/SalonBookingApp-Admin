package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class StaffsAdapter extends ArrayAdapter<Staff> {

    private final Activity context;
    private final ArrayList<Staff> staffs;

    static class ViewHolder {
        public TextView tvStaffName;
        public TextView tvStaffEmail;
        public TextView tvStaffPhone;
    }

    public StaffsAdapter(Activity context, ArrayList<Staff> staffs) {
        super(context, R.layout.listview_staff, staffs);
        this.context = context;
        this.staffs = staffs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        Staff staff = staffs.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_staff, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvStaffName = rowView.findViewById(R.id.tvStaffName);
            viewHolder.tvStaffEmail = rowView.findViewById(R.id.tvStaffEmail);
            viewHolder.tvStaffPhone = rowView.findViewById(R.id.tvStaffPhone);

            rowView.setTag(viewHolder);

            viewHolder.tvStaffName.setTag(staff);
            viewHolder.tvStaffEmail.setTag(staff);
            viewHolder.tvStaffPhone.setTag(staff);
        } else {
            rowView = convertView;

            ((ViewHolder) rowView.getTag()).tvStaffName.setTag(staff);
            ((ViewHolder) rowView.getTag()).tvStaffEmail.setTag(staff);
            ((ViewHolder) rowView.getTag()).tvStaffPhone.setTag(staff);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.tvStaffName.setText(staff.getStaffName());
        holder.tvStaffEmail.setText(staff.getStaffEmail());
        holder.tvStaffPhone.setText(staff.getStaffPhone());

        return rowView;
    }
}
