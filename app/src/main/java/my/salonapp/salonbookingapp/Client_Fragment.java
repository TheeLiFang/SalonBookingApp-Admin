package my.salonapp.salonbookingapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class Client_Fragment extends Fragment {

    // URL
    private static final String ASPX_GET_ALL_CLIENTS = "GetAllClients.aspx";

    // XML nodes for client list
    private static final String XML_NODE_CLIENT_ID = "ClientID";
    private static final String XML_NODE_CLIENT_NAME = "ClientName";
    private static final String XML_NODE_CLIENT_EMAIL = "ClientEmail";
    private static final String XML_NODE_CLIENT_PHONE = "ClientPhone";
    private static final String XML_NODE_CLIENT_ALLERGIC_REMARK = "AllergicRemark";
    private static final String XML_NODE_CLIENT_REMARK = "Remark";

    // Control fields
    private View view;
    private ListView listView;
    private FloatingActionButton addNewClientBtn;
    private WaveSwipeRefreshLayout mSwipeRefreshLayout;

    // Variables
    private ClientDbHelper db;
    private ArrayList<Client> clients = new ArrayList<>();
    private Client client;

    public Client_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.client_layout, container, false);

        initViews();
        setListeners();
        showRedirectMessage();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Initiate Views
    private void initViews() {
        listView = view.findViewById(R.id.card_listView_client);
        addNewClientBtn = view.findViewById(R.id.addNewClientBtn);

        mSwipeRefreshLayout = view.findViewById(R.id.main_swipe);
        mSwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
        mSwipeRefreshLayout.setWaveColor(ContextCompat.getColor(getContext(), R.color.violet));
        mSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }, 1500);
            }
        });
    }

    private void setListeners() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final ClientDetail_Fragment fragment = new ClientDetail_Fragment();
        final Bundle bundle = new Bundle();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                client = clients.get(position);

                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Edit);
                bundle.putInt(Utils.Bundle_ID, client.getClientId());
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.ClientDetail_Fragment)
                        .commit();
            }
        });

        addNewClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.ClientDetail_Fragment)
                        .commit();
            }
        });
    }

    private void showRedirectMessage() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Task task = (Utils.Task) bundle.getSerializable(Utils.Bundle_Task);
            Boolean status = bundle.getBoolean(Utils.Bundle_Status);

            if (task == Utils.Task.Submit) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_add_new_client));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_add_new_client));
                }
            } else if (task == Utils.Task.Update) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_update_client));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_update_client));
                }
            }
        }

        refreshClientListData();
    }

    // Refresh and download all the active clients from server
    private void refreshClientListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active clients from server
    private void downloadClients() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_CLIENTS);
        String xml = parser.getXmlFromUrl(url, true, null, null);
        clients = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Client client;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    client = new Client();

                    // Adding each child node to client object
                    client.setClientId(Integer.parseInt(parser.getValue(e1, XML_NODE_CLIENT_ID)));
                    client.setClientName(parser.getValue(e1, XML_NODE_CLIENT_NAME));
                    client.setClientEmail(parser.getValue(e1, XML_NODE_CLIENT_EMAIL));
                    client.setClientPhone(parser.getValue(e1, XML_NODE_CLIENT_PHONE));
                    client.setClientAllergicRemark(parser.getValue(e1, XML_NODE_CLIENT_ALLERGIC_REMARK));
                    client.setClientRemark(parser.getValue(e1, XML_NODE_CLIENT_REMARK));

                    clients.add(client);
                }

                db = new ClientDbHelper(getContext());
                db.deleteAllClients();
                db.addAllClients(clients);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        clients = db.getAllClients();
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            if (Utils.isNetworkAvailable(getContext())) {
                downloadClients();
            } else {
                db = new ClientDbHelper(getContext());
                clients = db.getAllClients();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            ClientsAdapter adapter = new ClientsAdapter(getActivity(), clients);
            listView.setAdapter(adapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
