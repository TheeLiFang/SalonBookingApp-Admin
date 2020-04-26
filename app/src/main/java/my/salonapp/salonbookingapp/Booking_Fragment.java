package my.salonapp.salonbookingapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.shrikanthravi.collapsiblecalendarview.data.Day;
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class Booking_Fragment extends Fragment {

    // URLs
    private static final String ASPX_GET_ALL_BOOKINGS = "GetAllBookings.aspx";

    // XML nodes for booking list
    private static final String XML_NODE_BOOKING_ID = "BookingID";
    private static final String XML_NODE_COMPANY_ID = "CompanyID";
    private static final String XML_NODE_COMPANY_NAME = "CompanyName";
    private static final String XML_NODE_CLIENT_ID = "ClientID";
    private static final String XML_NODE_CLIENT_NAME = "ClientName";
    private static final String XML_NODE_STAFF_ID = "StaffID";
    private static final String XML_NODE_STAFF_NAME = "StaffName";
    private static final String XML_NODE_BOOKING_DATE = "BookingDate";
    private static final String XML_NODE_BOOKING_DURATION = "BookingDuration";
    // XML nodes for booking service list
    private static final String XML_NODE_BOOKING_SERVICE_ID = "BookingServiceID";
    private static final String XML_NODE_SERVICE_NAME = "ServiceName";
    private static final String XML_NODE_SERVICE_PRICE = "ServicePrice";
    private static final String XML_NODE_SERVICE_DURATION = "ServiceDuration";
    private static final String XML_NODE_DISPLAY_ORDER = "DisplayOrder";

    // Control fields
    private View view;
    private ListView listView;
    private FloatingActionButton addNewBookingBtn;
    private WaveSwipeRefreshLayout mSwipeRefreshLayout;

    // Variables
    private CompanyDbHelper companyDb;
    private BookingDbHelper db;
    private ArrayList<Booking> bookings = new ArrayList<>();
    private Booking booking;

    public Booking_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.booking_layout, container, false);

        initViews();
        setListeners();
        showRedirectMessage();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Initialize all the views
    private void initViews() {
        listView = view.findViewById(R.id.card_listView_booking);

        addNewBookingBtn = view.findViewById(R.id.addNewBookingBtn);
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

    // Set event listeners
    private void setListeners() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final BookingDetail_Fragment fragment = new BookingDetail_Fragment();
        final Bundle bundle = new Bundle();
        final CollapsibleCalendar collapsibleCalendar = view.findViewById(R.id.calBooking);

        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = collapsibleCalendar.getSelectedDay();
                String selectedDate = day.getDay() + "/" + String.format(Locale.getDefault(), "%02d", (day.getMonth() + 1)) + "/" + day.getYear();

                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    bookings = db.getBookingsByDate(simpleDateFormat.parse(selectedDate));

                    BookingsAdapter adapter = new BookingsAdapter(getActivity(), bookings);
                    listView.setAdapter(adapter);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Error: ", e.getMessage());
                }
            }

            @Override
            public void onItemClick(View view) {
            }

            @Override
            public void onDataUpdate() {
            }

            @Override
            public void onMonthChange() {
            }

            @Override
            public void onWeekChange(int i) {
            }

            @Override
            public void onClickListener() {
            }

            @Override
            public void onDayChanged() {
            }
        });
        collapsibleCalendar.expand(0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                booking = bookings.get(position);

                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Edit);
                bundle.putInt(Utils.Bundle_ID, booking.getBookingId());
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.BookingDetail_Fragment)
                        .commit();
            }
        });

        addNewBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.BookingDetail_Fragment)
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
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_add_new_booking));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_add_new_booking));
                }
            } else if (task == Utils.Task.Delete) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_delete_booking));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_delete_booking));
                }
            }
        }

        refreshBookingListData();
    }

    // Refresh and download all the active bookings from server
    private void refreshBookingListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active bookings from server
    private void downloadBookings() {
        XMLParser parser = new XMLParser();
        companyDb = new CompanyDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_BOOKINGS, companyDb.getCompanyId());
        String xml = parser.getXmlFromUrl(url, true, null, null);
        bookings = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Booking booking;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    booking = new Booking();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    // Adding each child node to booking object
                    booking.setBookingId(Integer.parseInt(parser.getValue(e1, XML_NODE_BOOKING_ID)));
                    booking.setCompanyId(Integer.parseInt(parser.getValue(e1, XML_NODE_COMPANY_ID)));
                    booking.setCompanyName(parser.getValue(e1, XML_NODE_COMPANY_NAME));
                    booking.setClientId(Integer.parseInt(parser.getValue(e1, XML_NODE_CLIENT_ID)));
                    booking.setClientName(parser.getValue(e1, XML_NODE_CLIENT_NAME));
                    booking.setStaffId(Integer.parseInt(parser.getValue(e1, XML_NODE_STAFF_ID)));
                    booking.setStaffName(parser.getValue(e1, XML_NODE_STAFF_NAME));
                    booking.setBookingDate(simpleDateFormat.parse(parser.getValue(e1, XML_NODE_BOOKING_DATE)).getTime());
                    booking.setBookingDuration(Integer.parseInt(parser.getValue(e1, XML_NODE_BOOKING_DURATION)));

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

                db = new BookingDbHelper(getContext());
                db.deleteAllBookings();
                db.addAllBookings(bookings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bookings = db.getAllBookings();
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            if (Utils.isNetworkAvailable(getContext())) {
                downloadBookings();
            } else {
                db = new BookingDbHelper(getContext());
                bookings = db.getAllBookings();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            // Default show only today's date schedule
            bookings = db.getBookingsByDate(new Date());
            BookingsAdapter adapter = new BookingsAdapter(getActivity(), bookings);
            listView.setAdapter(adapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}