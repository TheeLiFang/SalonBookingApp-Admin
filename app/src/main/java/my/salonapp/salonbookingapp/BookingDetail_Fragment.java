package my.salonapp.salonbookingapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class BookingDetail_Fragment extends Fragment implements View.OnClickListener {

    // URLs
    private static final String ASPX_SUBMIT_BOOKING = "SubmitBooking.aspx";
    private static final String ASPX_DELETE_BOOKING = "DeleteBooking.aspx";
    private static final String ASPX_GET_ALL_BOOKINGS = "GetAllBookings.aspx";
    private static final String ASPX_CHECK_STAFF_BOOKING_TIME_SLOT_EXISTENCE = "CheckStaffBookingTimeSlotExistence.aspx";

    // XML nodes for client list
    private static final String XML_NODE_CLIENT_ID = "ClientID";

    // XML nodes for staff list
    private static final String XML_NODE_SERVICE_PRICE = "ServicePrice";
    private static final String XML_NODE_SERVICE_DURATION = "ServiceDuration";

    // XML nodes for service list
    private static final String XML_NODE_SERVICE_NAME = "ServiceName";

    // XML nodes for booking list
    private static final String XML_NODE_BOOKING_ID = "BookingID";
    private static final String XML_NODE_BOOKING_DATE = "BookingDate";

    // XML nodes for booking service list
    private static final String XML_NODE_BOOKING_SERVICE_ID = "BookingServiceID";
    private static final String XML_NODE_DISPLAY_ORDER = "DisplayOrder";

    // XML nodes for get staff booking time slot existence
    private static final String XML_NODE_STAFF_BOOKING_TIME_SLOT_REGISTERED = "RegisteredYN";
    private static final String XML_NODE_START_TIME = "StartTime";
    private static final String XML_NODE_END_TIME = "EndTime";

    // Control fields
    private View view;
    private TextView bookingTitle, serviceListHint;
    private EditText bookingTime;
    private Spinner spinner1, spinner2, spinner3;
    private Button addServiceItem;
    private ListView listView;
    private static FloatingActionButton addBookingBtn;
    private static FloatingActionButton deleteBookingBtn;
    private static FloatingActionButton backBookingBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private Boolean registeredYN;
    private String startTime, endTime;
    private int bookingId;
    private CompanyDbHelper companyDb;
    private ClientDbHelper clientDb;
    private List<Client> clients = new ArrayList<>();
    private Client client;
    private StaffDbHelper db;
    private ArrayList<Staff> staffs = new ArrayList<>();
    private Staff staff;
    private ServiceDbHelper serviceDb;
    private ArrayList<Service> services = new ArrayList<>();
    private Service service;
    private int clientId, staffId, serviceId;
    private ArrayList<Service> selectedServices = new ArrayList<>();
    private SelectedServicesAdapter selectedServicesAdapter;
    private ArrayList<Integer> selectedServiceId = new ArrayList<>();
    private BookingDbHelper bookingDb;
    private ArrayList<Booking> bookings = new ArrayList<>();
    private Boolean isInsertMood = Boolean.FALSE;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public BookingDetail_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bookingdetail_layout, container, false);
        task = null;

        initViews();
        setListeners();
        refreshSpinnerData();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        bookingTitle = view.findViewById(R.id.bookingTitle);
        serviceListHint = view.findViewById(R.id.service_list_hint);
        serviceListHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SimpleTooltip.Builder(getContext())
                        .anchorView(serviceListHint)
                        .text(R.string.service_list_hint)
                        .gravity(Gravity.TOP)
                        .animated(true)
                        .transparentOverlay(false)
                        .build()
                        .show();
            }
        });

        bookingTime = view.findViewById(R.id.bookingTime);
        // Disable input for readonly
        bookingTime.setInputType(InputType.TYPE_NULL);

        spinner1 = view.findViewById(R.id.spinner1);
        spinner2 = view.findViewById(R.id.spinner2);
        spinner3 = view.findViewById(R.id.spinner3);

        listView = view.findViewById(R.id.card_listView_serviceList);
        addServiceItem = view.findViewById(R.id.addServiceItem);
        addBookingBtn = view.findViewById(R.id.addBookingBtn);

        deleteBookingBtn = view.findViewById(R.id.deleteBookingBtn);
        backBookingBtn = view.findViewById(R.id.backBookingBtn);

        // Generate spinner list
        clientDb = new ClientDbHelper(getContext());
        clients = clientDb.getAllClients();

        ArrayAdapter<Client> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, clients);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                client = (Client) parent.getSelectedItem();
                clientId = client.getClientId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Generate spinner list
        db = new StaffDbHelper(getContext());
        staffs = db.getAllStaffs();

        // Add default option to display all category of services
        staffs.add(new Staff(0, "No specific"));
        Collections.sort(staffs);

        ArrayAdapter<Staff> staffAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, staffs);
        staffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        spinner2.setAdapter(staffAdapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                staff = (Staff) parent.getSelectedItem();
                staffId = staff.getStaffId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Generate spinner list
        serviceDb = new ServiceDbHelper(getContext());
        services = serviceDb.getAllServices();

        // Add default option to empty
        services.add(new Service(0, "- Please select a service -"));
        Collections.sort(services);

        ArrayAdapter<Service> serviceAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, services);
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        spinner3.setAdapter(serviceAdapter);
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                service = (Service) parent.getSelectedItem();
                serviceId = service.getServiceId();
                if (serviceId != 0) {
                    service = serviceDb.getServiceById(serviceId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        showData();
    }

    // Set event listeners
    private void setListeners() {
        addBookingBtn.setOnClickListener(this);
        deleteBookingBtn.setOnClickListener(this);
        backBookingBtn.setOnClickListener(this);

        addServiceItem.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Boolean isServiceSelected = Boolean.FALSE;

                for (Service item : selectedServices) {
                    if (item.getServiceId() == service.getServiceId()) {
                        isServiceSelected = Boolean.TRUE;
                        break;
                    }
                }

                if (isServiceSelected == Boolean.FALSE && service.getServiceId() > 0) {
                    selectedServices.add(service);
                }

                selectedServicesAdapter = new SelectedServicesAdapter(getActivity(), selectedServices);
                listView.setAdapter(selectedServicesAdapter);
                selectedServicesAdapter.notifyDataSetChanged();
            }
        });
        if (isInsertMood == Boolean.TRUE) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.confirmation))
                            .setMessage(getString(R.string.confirmation_delete_service))
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    selectedServices.remove(position);
                                    selectedServicesAdapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), getString(R.string.success_delete_service), Toast.LENGTH_LONG)
                                            .show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                    return true;
                }
            });
        }
    }

    // Toggle buttons and display selected staff's data if it is edit mode
    private void showData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Mode mode = (Utils.Mode) bundle.getSerializable(Utils.Bundle_Mode);

            if (mode == Utils.Mode.Insert) {
                isInsertMood = Boolean.TRUE;

                bookingTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar selectedDate = Calendar.getInstance();
                        final Calendar currentDate = Calendar.getInstance();

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selectedDate.set(year, monthOfYear, dayOfMonth);

                                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        selectedDate.set(Calendar.MINUTE, minute);

                                        // Check if user selected date/time must be later than current date/time
                                        if (isValidTime(currentDate, selectedDate)) {
                                            bookingTime.setText(simpleDateFormat.format(selectedDate.getTime()));
                                        } else {
                                            bookingTime.setText("");
                                            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_booking_time));
                                        }
                                    }
                                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
                            }
                        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));

                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                });
                bookingTime.requestFocus();
                deleteBookingBtn.setVisibility(View.GONE);
            } else {
                bookingTitle.setText(getString(R.string.reviewBooking));
                bookingDb = new BookingDbHelper(getContext());

                bookingId = bundle.getInt(Utils.Bundle_ID);

                if (bookingId > 0) {
                    BookingDbHelper db = new BookingDbHelper(getContext());
                    Booking booking = db.getBookingByBookingId(bookingId);
                    ArrayList<BookingService> bookingServices = db.getBookingServicesByBookingId(bookingId);
                    booking.setBookingServices(bookingServices);

                    bookingTime.setText(simpleDateFormat.format(booking.getBookingDate()));

                    // Set selected client to spinner
                    Client cl = new Client(booking.getClientId(), booking.getClientName());
                    spinner1.setSelection(getClientIndex(spinner1, cl));

                    // Set selected staff to spinner
                    Staff st = new Staff(booking.getStaffId(), booking.getStaffName());
                    spinner2.setSelection(getStaffIndex(spinner2, st));

                    // Disable input for readonly
                    spinner1.setEnabled(false);
                    spinner2.setEnabled(false);
                    spinner3.setEnabled(false);
                    serviceListHint.setVisibility(View.GONE);
                }

                addBookingBtn.setVisibility(View.GONE);
            }
        }
    }

    private int getClientIndex(Spinner spinner, Client cl) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(cl.getClientName())) {
                return i;
            }
        }

        return 0;
    }

    private int getStaffIndex(Spinner spinner, Staff st) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(st.getStaffName())) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookingBtn:
                // Add booking
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_add_new_booking),
                            Utils.Task.Submit.toString());
                }
                break;

            case R.id.deleteBookingBtn:
                // Delete booking
                task = Utils.Task.Delete;

                doSubmission(getString(R.string.confirmation_delete_booking),
                        Utils.Task.Delete.toString());
                break;

            case R.id.backBookingBtn:
                // Back to booking page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Booking_Fragment(),
                                Utils.Booking_Fragment)
                        .commit();
                break;
        }
    }

    private Boolean isValidTime(Calendar currentDate, Calendar selectedDate) {
        return 0 < selectedDate.compareTo(currentDate);
    }

    // Check validation method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        String time = bookingTime.getText().toString();

        // Check if all the strings are null or not
        if (time.equals("") || time.length() == 0
                || spinner1.getSelectedItem() == null || spinner2.getSelectedItem() == null
                || selectedServices.size() == 0) {
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
                        new BookingDetail_Fragment.BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                taskSelection);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void addBooking() {
        companyDb = new CompanyDbHelper(getContext());

        StringBuilder stringList = new StringBuilder();
        Iterator<Integer> iterator = selectedServiceId.iterator();
        while (iterator.hasNext()) {
            stringList.append(iterator.next());
            if (iterator.hasNext()) {
                stringList.append(";");
            }
        }

        try {
            String url = String.format("%1$s%2$s%3$s?companyID=%4$s&clientID=%5$s&staffID=%6$s&bookingDate=%7$s&serviceList=%8$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_SUBMIT_BOOKING,
                    companyDb.getCompanyId(),
                    clientId,
                    staffId,
                    bookingTime.getText().toString(),
                    stringList.toString());

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void deleteBooking() {
        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_DELETE_BOOKING,
                    bookingId);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
        }
    }

    // Refresh and download all the active clients and staffs from server
    private void refreshSpinnerData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active bookings from server
    private void downloadBookings() {
        XMLParser parser = new XMLParser();
        companyDb = new CompanyDbHelper(getContext());
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

    private void checkStaffBookingTimeSlotExistence() {
        for (Service service : selectedServices) {
            selectedServiceId.add(service.getServiceId());
        }

        StringBuilder stringList = new StringBuilder();
        Iterator<Integer> iterator = selectedServiceId.iterator();
        while (iterator.hasNext()) {
            stringList.append(iterator.next());
            if (iterator.hasNext()) {
                stringList.append(";");
            }
        }

        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&startTime=%5$s&serviceList=%6$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_CHECK_STAFF_BOOKING_TIME_SLOT_EXISTENCE,
                    staffId,
                    bookingTime.getText().toString(),
                    stringList);

            httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    public void httpParamSubmission(String url) {
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
                NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
                Element e = (Element) nl.item(0);

                status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

                if (status == Boolean.TRUE) {
                    NodeList nll = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                    Element el = (Element) nll.item(0);

                    registeredYN = Boolean.parseBoolean(parser.getValue(el, XML_NODE_STAFF_BOOKING_TIME_SLOT_REGISTERED));
                    startTime = parser.getValue(el, XML_NODE_START_TIME);
                    endTime = parser.getValue(el, XML_NODE_END_TIME);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            isNetworkConnected = Utils.isNetworkAvailable(getContext());

            if (isNetworkConnected == Boolean.TRUE) {
                if (param.length != 0) {
                    if (param[0].equals(Utils.Task.Submit.toString())) {
                        checkStaffBookingTimeSlotExistence();
                        if (registeredYN == Boolean.FALSE) {
                            addBooking();
                        }
                    } else if (param[0].equals(Utils.Task.Delete.toString())) {
                        deleteBooking();
                    }
                } else {
                    downloadBookings();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isNetworkConnected == Boolean.TRUE) {
                if (task != null) {
                    if (registeredYN == Boolean.TRUE) {
                        new CustomToast().Show_Toast(getActivity(), view,
                                String.format("%1$s %2$s - %3$s",
                                        getString(R.string.booking_staff_not_available), startTime, endTime));
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Utils.Bundle_Task, task);
                        bundle.putBoolean(Utils.Bundle_Status, status);

                        Booking_Fragment fragment = new Booking_Fragment();
                        fragment.setArguments(bundle);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(getTag())
                                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                                .replace(R.id.frameContainer, fragment, Utils.Booking_Fragment)
                                .commit();
                    }
                } else {
                    if (isInsertMood == Boolean.FALSE) {
                        // Display the service item list
                        ArrayList<BookingService> bookingServicesList = bookingDb.getBookingServicesByBookingId(bookingId);
                        SelectedBookingServicesAdapter adapter = new SelectedBookingServicesAdapter(getActivity(), bookingServicesList);
                        listView.setAdapter(adapter);
                    }
                }
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}