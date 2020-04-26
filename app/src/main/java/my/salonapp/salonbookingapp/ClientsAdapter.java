package my.salonapp.salonbookingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;

public class ClientsAdapter extends ArrayAdapter<Client> {

    private final Activity context;
    private final ArrayList<Client> clients;

    static class ViewHolder {
        public TextView tvClientName;
        public TextView tvClientEmail;
        public TextView tvClientPhone;
    }

    public ClientsAdapter(Activity context, ArrayList<Client> clients) {
        super(context, R.layout.listview_client, clients);
        this.context = context;
        this.clients = clients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        Client client = clients.get(position);

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.listview_client, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvClientName = rowView.findViewById(R.id.tvClientName);
            viewHolder.tvClientEmail = rowView.findViewById(R.id.tvClientEmail);
            viewHolder.tvClientPhone = rowView.findViewById(R.id.tvClientPhone);

            rowView.setTag(viewHolder);

            viewHolder.tvClientName.setTag(client);
            viewHolder.tvClientEmail.setTag(client);
            viewHolder.tvClientPhone.setTag(client);
        } else {
            rowView = convertView;

            ((ViewHolder) rowView.getTag()).tvClientName.setTag(client);
            ((ViewHolder) rowView.getTag()).tvClientEmail.setTag(client);
            ((ViewHolder) rowView.getTag()).tvClientPhone.setTag(client);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        holder.tvClientName.setText(client.getClientName());
        holder.tvClientEmail.setText(client.getClientEmail());
        holder.tvClientPhone.setText(client.getClientPhone());

        return rowView;
    }
}
