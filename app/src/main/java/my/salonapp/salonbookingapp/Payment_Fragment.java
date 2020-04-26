package my.salonapp.salonbookingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.getbase.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Payment_Fragment extends Fragment implements View.OnClickListener {

    // URL
    private static final String ASPX_GET_ALL_CLIENTS_FOR_PAYMENT = "GetAllClientsForPayment.aspx";
    private static final String ASPX_GET_ALL_BOOKINGS = "GetAllBookingsForPayment.aspx";
    private static final String ASPX_SUBMIT_PAYMENT = "SubmitPayment.aspx";

    // XML nodes for client list
    private static final String XML_NODE_CLIENT_ID = "ClientID";
    public static final String XML_NODE_CLIENT_NAME = "ClientName";

    // XML nodes for booking list
    private static final String XML_NODE_BOOKING_ID = "BookingID";
    public static final String XML_NODE_BOOKING_DATE = "BookingDate";

    // XML nodes for booking service list
    private static final String XML_NODE_REFERENCE = "RefNo";

    // XML nodes for booking service list
    private static final String XML_NODE_BOOKING_SERVICE_ID = "BookingServiceID";
    private static final String XML_NODE_SERVICE_ID = "ServiceID";
    private static final String XML_NODE_SERVICE_NAME = "ServiceName";
    private static final String XML_NODE_SERVICE_PRICE = "ServicePrice";
    private static final String XML_NODE_SERVICE_DURATION = "ServiceDuration";
    private static final String XML_NODE_DISPLAY_ORDER = "DisplayOrder";

    // Control fields
    private View view;
    private Spinner spinner1, spinner2;
    private TextView subtotal;
    private ListView listView;
    private RelativeLayout serviceFrameLayout;
    private static FloatingActionButton payPaymentBtn;
    private static FloatingActionButton backPaymentBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private String refNo;
    private CompanyDbHelper companyDb;

    private int clientId;
    private ClientDbHelper clientDb;
    private List<Client> clients = new ArrayList<>();

    private int bookingId;
    private BookingDbHelper bookingDb;
    private ArrayList<Booking> bookings = new ArrayList<>();
    private Booking booking;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public Payment_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.payment_layout, container, false);
        task = null;

        initViews();
        setListeners();
        refreshServiceListData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Initiate Views
    private void initViews() {
        spinner1 = view.findViewById(R.id.spinner1);
        spinner1.requestFocus();

        subtotal = view.findViewById(R.id.subtotal);
        spinner2 = view.findViewById(R.id.spinner2);
        listView = view.findViewById(R.id.card_listView_serviceList);
        serviceFrameLayout = view.findViewById(R.id.service_frame_layout);

        payPaymentBtn = view.findViewById(R.id.payPaymentBtn);
        backPaymentBtn = view.findViewById(R.id.backPaymentBtn);
    }

    private void setListeners() {
        payPaymentBtn.setOnClickListener(this);
        backPaymentBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.payPaymentBtn:
                // Add service
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_submit_payment),
                            Utils.Task.Submit.toString());
                }
                break;

            case R.id.backPaymentBtn:
                // Back to service page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Home_Fragment(),
                                Utils.Home_Fragment)
                        .commit();
                break;
        }
    }

    // Check Validation Method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Check if all strings are null or not
        if (spinner1.getSelectedItem() == null || spinner2.getSelectedItem() == null) {

            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));
        }

        return validate;
    }


    private void doSubmission(String message, final String taskSelection) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirmation))
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new Payment_Fragment.BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                taskSelection);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }


    // Refresh and download all the active services from server
    private void refreshServiceListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active clients from server
    private void downloadClients() {
        XMLParser parser = new XMLParser();
        companyDb = new CompanyDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_CLIENTS_FOR_PAYMENT, companyDb.getCompanyId());
        String xml = parser.getXmlFromUrl(url, true, null, null);

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

                    // Adding each child node to service object
                    client.setClientId(Integer.parseInt(parser.getValue(e1, XML_NODE_CLIENT_ID)));
                    client.setClientName(parser.getValue(e1, XML_NODE_CLIENT_NAME));

                    clients.add(client);
                }

                clientDb = new ClientDbHelper(getContext());
                clientDb.deleteAllClients();
                clientDb.addAllClients(clients);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedPayment() {
        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&subtotal=%5$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_SUBMIT_PAYMENT,
                    bookingId,
                    subtotal.getText().toString().replace("RM ", ""));

            httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    public void httpParamSubmission(String url) {
        Boolean status = Boolean.FALSE;

        try {
            URL url1 = new URL(url);
            String xml = "";

            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(Utils.timeout_submission);
            connection.setConnectTimeout(Utils.timeout_submission);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder xmlResponse = new StringBuilder();
                BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()), 8192);
                String strLine;

                while ((strLine = input.readLine()) != null) {
                    xmlResponse.append(strLine);
                }

                xml = xmlResponse.toString();
                input.close();
            }

            if (xml.length() > 0) {
                XMLParser parser = new XMLParser();
                Document doc = parser.getDomElement(xml);
                NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_INSERT);
                Element e = (Element) nl.item(0);

                status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

                if (status == Boolean.TRUE) {
                    NodeList nll = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                    Element el = (Element) nll.item(0);

                    refNo = parser.getValue(el, XML_NODE_REFERENCE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    // Download all the active bookings from server
    private void downloadBookings() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_BOOKINGS, companyDb.getCompanyId());
        String xml = parser.getXmlFromUrl(url, true, null, null);

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Booking booking;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    booking = new Booking();

                    // Adding each child node to booking object
                    booking.setBookingId(Integer.parseInt(parser.getValue(e1, XML_NODE_BOOKING_ID)));
                    booking.setBookingDate(simpleDateFormat.parse(parser.getValue(e1, XML_NODE_BOOKING_DATE)).getTime());
                    booking.setClientId(Integer.parseInt(parser.getValue(e1, XML_NODE_CLIENT_ID)));

                    NodeList nl2 = doc.getElementsByTagName(Utils.XML_NODE_ITEM);
                    Element e2;
                    ArrayList<BookingService> bookingServices = new ArrayList<>();
                    BookingService bookingService;

                    for (int j = 0; j < nl2.getLength(); j++) {
                        e2 = (Element) nl2.item(j);

                        bookingService = new BookingService();

                        // Adding each child node to booking service object
                        bookingService.setBookingId(Integer.parseInt(parser.getValue(e2, XML_NODE_BOOKING_ID)));
                        bookingService.setBookingServiceId(Integer.parseInt(parser.getValue(e2, XML_NODE_BOOKING_SERVICE_ID)));
                        bookingService.setServiceName(parser.getValue(e2, XML_NODE_SERVICE_NAME));
                        bookingService.setServicePrice(Float.parseFloat(parser.getValue(e2, XML_NODE_SERVICE_PRICE)));
                        bookingService.setServiceDuration(Integer.parseInt(parser.getValue(e2, XML_NODE_SERVICE_DURATION)));
                        bookingService.setDisplayOrder(Integer.parseInt(parser.getValue(e2, XML_NODE_DISPLAY_ORDER)));

                        if (booking.getBookingId() == bookingService.getBookingId()) {
                            bookingServices.add(bookingService);
                        }
                    }

                    booking.setBookingServices(bookingServices);

                    bookings.add(booking);
                }

                bookingDb = new BookingDbHelper(getContext());
                bookingDb.deleteAllBookings();
                bookingDb.addAllBookings(bookings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setClientSpinnerData() {
        ArrayAdapter<Client> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, clients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Client client = (Client) parent.getSelectedItem();
                clientId = client.getClientId();
                updateBookingListData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateBookingListData() {
        if (clientId != 0) {
            setBookingSpinnerData();
        }
    }

    private void setBookingSpinnerData() {
        // Generate spinner list
        bookingDb = new BookingDbHelper(getContext());
        bookings = bookingDb.getBookingByClientId(clientId);

        ArrayAdapter<Booking> adapter2 = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, bookings);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                booking = (Booking) parent.getSelectedItem();
                bookingId = booking.getBookingId();
                updateServiceListData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateServiceListData() {
        if (bookingId != 0) {
            ArrayList<BookingService> bookingServices = bookingDb.getBookingServicesByBookingId(bookingId);
            BookingServicesAdapter adapter = new BookingServicesAdapter(getActivity(), bookingServices);
            listView.setAdapter(adapter);
            subtotal.setText(String.format(Locale.getDefault(), "%1$s %2$.2f",
                    getString(R.string.service_price_currency), booking.getSubTotal(bookingServices)));
        } else {
            serviceFrameLayout.setVisibility(View.INVISIBLE);
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            isNetworkConnected = Utils.isNetworkAvailable(getContext());

            if (isNetworkConnected == Boolean.TRUE) {
                if (param.length != 0) {
                    proceedPayment();
                } else {
                    downloadClients();
                    downloadBookings();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);

            if (isNetworkConnected == Boolean.TRUE) {
                if (task != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Utils.Bundle_Task, task);
                    bundle.putBoolean(Utils.Bundle_Status, status);
                    bundle.putInt(Utils.Bundle_ID, bookingId);
                    bundle.putString("refNo", refNo);

                    PaymentSuccessful_Fragment fragment = new PaymentSuccessful_Fragment();
                    fragment.setArguments(bundle);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .addToBackStack(getTag())
                            .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                            .replace(R.id.frameContainer, fragment, Utils.PaymentSuccessful_Fragment)
                            .commit();
                } else {
                    setClientSpinnerData();
                }
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
