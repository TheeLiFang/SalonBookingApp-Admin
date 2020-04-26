package my.salonapp.salonbookingapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class TransactionHistory_Fragment extends Fragment {

    // URL
    private static final String ASPX_GET_ALL_PAYMENTS = "GetAllPayments.aspx";

    // XML nodes for transaction history list
    private static final String XML_NODE_TRANSACTION_ID = "TransactionID";
    private static final String XML_NODE_REF_NO = "RefNo";
    private static final String XML_NODE_STAFF_NAME = "StaffName";
    private static final String XML_NODE_CLIENT_ID = "ClientID";
    private static final String XML_NODE_CLIENT_NAME = "ClientName";
    private static final String XML_NODE_BOOKING_DATE = "BookingDate";
    private static final String XML_NODE_SERVICES_DESC = "ServicesDesc";
    private static final String XML_NODE_SUB_TOTAL = "SubTotal";
    // XML nodes for transaction history service list
    private static final String XML_NODE_BOOKING_SERVICE_ID = "BookingServiceID";
    private static final String XML_NODE_SERVICE_NAME = "ServiceName";
    private static final String XML_NODE_SERVICE_PRICE = "ServicePrice";
    private static final String XML_NODE_DISPLAY_ORDER = "DisplayOrder";

    // Control fields
    private View view;
    private Spinner clientSpinner;
    private ListView listView;
    private WaveSwipeRefreshLayout mSwipeRefreshLayout;

    // Variables
    private CompanyDbHelper companyDb;
    private ClientDbHelper clientDb;
    private TransactionHistoryDbHelper db;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<TransactionHistory> transactionHistories = new ArrayList<>();
    private TransactionHistory transactionHistory;
    private int clientId;

    public TransactionHistory_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.transactionhistory_layout, container, false);

        initViews();
        setListeners();
        refreshTransactionHistoryListData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Initiate Views
    private void initViews() {
        clientSpinner = view.findViewById(R.id.clientSpinner);
        clientSpinner.requestFocus();

        listView = view.findViewById(R.id.card_listView_transaction_history);

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
        final TransactionHistoryDetail_Fragment fragment = new TransactionHistoryDetail_Fragment();
        final Bundle bundle = new Bundle();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transactionHistory = transactionHistories.get(position);

                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Edit);
                bundle.putInt(Utils.Bundle_ID, transactionHistory.getTransactionId());
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.TransactionHistoryDetail_Fragment)
                        .commit();
            }
        });
    }

    // Refresh and download all the active transaction history from server
    private void refreshTransactionHistoryListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active transaction histories from server
    private void downloadTransactionHistories() {
        XMLParser parser = new XMLParser();
        companyDb = new CompanyDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_PAYMENTS, companyDb.getCompanyId());
        String xml = parser.getXmlFromUrl(url, true, null, null);
        transactionHistories = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                TransactionHistory transactionHistory;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    transactionHistory = new TransactionHistory();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    // Adding each child node to transactionHistory object
                    transactionHistory.setTransactionId(Integer.parseInt(parser.getValue(e1, XML_NODE_TRANSACTION_ID)));
                    transactionHistory.setRefNo(parser.getValue(e1, XML_NODE_REF_NO));
                    transactionHistory.setStaffName(parser.getValue(e1, XML_NODE_STAFF_NAME));
                    transactionHistory.setClientID(Integer.parseInt(parser.getValue(e1, XML_NODE_CLIENT_ID)));
                    transactionHistory.setClientName(parser.getValue(e1, XML_NODE_CLIENT_NAME));
                    transactionHistory.setBookingDate(simpleDateFormat.parse(parser.getValue(e1, XML_NODE_BOOKING_DATE)).getTime());
                    transactionHistory.setServicesDesc(parser.getValue(e1, XML_NODE_SERVICES_DESC));
                    transactionHistory.setSubTotal(Float.parseFloat(parser.getValue(e1, XML_NODE_SUB_TOTAL)));

                    NodeList nl2 = doc.getElementsByTagName(Utils.XML_NODE_ITEM);
                    Element e2;
                    ArrayList<TransactionHistoryService> transactionHistoryServices = new ArrayList<>();
                    TransactionHistoryService transactionHistoryService;

                    for (int j = 0; j < nl2.getLength(); j++) {
                        e2 = (Element) nl2.item(j);

                        transactionHistoryService = new TransactionHistoryService();

                        // Adding each child node to booking service object
                        transactionHistoryService.setTransactionId(Integer.parseInt(parser.getValue(e2, XML_NODE_TRANSACTION_ID)));
                        transactionHistoryService.setBookingServiceId(Integer.parseInt(parser.getValue(e2, XML_NODE_BOOKING_SERVICE_ID)));
                        transactionHistoryService.setServiceName(parser.getValue(e2, XML_NODE_SERVICE_NAME));
                        transactionHistoryService.setServicePrice(Float.parseFloat(parser.getValue(e2, XML_NODE_SERVICE_PRICE)));
                        transactionHistoryService.setDisplayOrder(Integer.parseInt(parser.getValue(e2, XML_NODE_DISPLAY_ORDER)));

                        if (transactionHistory.getTransactionId() == transactionHistoryService.getTransactionId()) {
                            transactionHistoryServices.add(transactionHistoryService);
                        }
                    }

                    transactionHistory.setTransactionHistoryServices(transactionHistoryServices);

                    transactionHistories.add(transactionHistory);
                }

                db = new TransactionHistoryDbHelper(getContext());
                db.deleteAllTransactionHistories();
                db.addAllTransactionHistories(transactionHistories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        transactionHistories = db.getAllTransactionHistories();
    }

    private void setSpinnerData() {
        // Add default option to display all category of services
        clients.add(new Client(0, "- All -"));
        Collections.sort(clients);

        ArrayAdapter<Client> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, clients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        clientSpinner.setAdapter(adapter);
        clientSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Client client = (Client) parent.getSelectedItem();
                clientId = client.getClientId();
                updateTransactionListData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateTransactionListData() {
        if (clientId == 0) {
            transactionHistories = db.getAllTransactionHistories();
        } else {
            transactionHistories = db.getTransactionHistoriesByClientId(clientId);
        }
        TransactionHistoriesAdapter adapter = new TransactionHistoriesAdapter(getActivity(), transactionHistories);
        listView.setAdapter(adapter);
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            if (Utils.isNetworkAvailable(getContext())) {
                downloadTransactionHistories();
                clientDb = new ClientDbHelper(getContext());
                clients = clientDb.getAllSpinnerClients();
            } else {
                db = new TransactionHistoryDbHelper(getContext());
                transactionHistories = db.getAllTransactionHistories();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);

            TransactionHistoriesAdapter adapter = new TransactionHistoriesAdapter(getActivity(), transactionHistories);
            listView.setAdapter(adapter);
            setSpinnerData();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
