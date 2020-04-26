package my.salonapp.salonbookingapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login_Fragment extends Fragment implements OnClickListener {

    // URL
    private static final String ASPX_GET_ALL_STAFFS = "GetAdminLogin.aspx";
    private static final String ASPX_GET_ALL_BOOKINGS = "GetAllBookings.aspx";

    // XML nodes for company list
    private static final String XML_NODE_COMPANY_ID = "CompanyID";
    private static final String XML_NODE_COMPANY_NAME = "CompanyName";
    private static final String XML_NODE_COMPANY_EMAIL = "CompanyEmail";
    private static final String XML_NODE_COMPANY_PASSWORD = "CompanyPassword";
    private static final String XML_NODE_COMPANY_PHONE = "CompanyPhone";
    private static final String XML_NODE_COMPANY_ADDRESS = "CompanyAddress";
    private static final String XML_NODE_MONDAY_YN = "MondayYN";
    private static final String XML_NODE_MONDAY_START_TIME = "MondayStartTime";
    private static final String XML_NODE_MONDAY_END_TIME = "MondayEndTime";
    private static final String XML_NODE_TUESDAY_YN = "TuesdayYN";
    private static final String XML_NODE_TUESDAY_START_TIME = "TuesdayStartTime";
    private static final String XML_NODE_TUESDAY_END_TIME = "TuesdayEndTime";
    private static final String XML_NODE_WEDNESDAY_YN = "WednesdayYN";
    private static final String XML_NODE_WEDNESDAY_START_TIME = "WednesdayStartTime";
    private static final String XML_NODE_WEDNESDAY_END_TIME = "WednesdayEndTime";
    private static final String XML_NODE_THURSDAY_YN = "ThursdayYN";
    private static final String XML_NODE_THURSDAY_START_TIME = "ThursdayStartTime";
    private static final String XML_NODE_THURSDAY_END_TIME = "ThursdayEndTime";
    private static final String XML_NODE_FRIDAY_YN = "FridayYN";
    private static final String XML_NODE_FRIDAY_START_TIME = "FridayStartTime";
    private static final String XML_NODE_FRIDAY_END_TIME = "FridayEndTime";
    private static final String XML_NODE_SATURDAY_YN = "SaturdayYN";
    private static final String XML_NODE_SATURDAY_START_TIME = "SaturdayStartTime";
    private static final String XML_NODE_SATURDAY_END_TIME = "SaturdayEndTime";
    private static final String XML_NODE_SUNDAY_YN = "SundayYN";
    private static final String XML_NODE_SUNDAY_START_TIME = "SundayStartTime";
    private static final String XML_NODE_SUNDAY_END_TIME = "SundayEndTime";
    // XML nodes for booking list
    private static final String XML_NODE_BOOKING_ID = "BookingID";
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
    private EditText emailID, password;
    private Button loginButton;
    private TextView forgotPassword, signUp;
    private CheckBox show_hide_password;
    private LinearLayout loginLayout;
    private Animation shakeAnimation;
    private FragmentManager fragmentManager;

    // Variables
    private CompanyDbHelper db;
    private Boolean status, isNetworkConnected;
    private Company company;

    public Login_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_layout, container, false);

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
        fragmentManager = getActivity().getSupportFragmentManager();

        emailID = view.findViewById(R.id.login_emailID);
        password = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.loginBtn);
        forgotPassword = view.findViewById(R.id.forgot_password);
        signUp = view.findViewById(R.id.createAccount);
        show_hide_password = view.findViewById(R.id.show_hide_password);
        loginLayout = view.findViewById(R.id.login_layout);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.shake);

        // Setting text selector over textviews
        XmlPullParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp, null);

            forgotPassword.setTextColor(csl);
            show_hide_password.setTextColor(csl);
            signUp.setTextColor(csl);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    // Set Listeners
    private void setListeners() {
        loginButton.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signUp.setOnClickListener(this);

        // Set check listener over checkbox for showing and hiding password
        show_hide_password
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checked then show password else hide password
                        if (isChecked) {
                            show_hide_password.setText(R.string.hide_pwd); // change checkbox text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd); // change checkbox text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:
                checkValidation();
                break;

            case R.id.forgot_password:

                // Replace forgot password fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer,
                                new ForgotPassword_Fragment(),
                                Utils.ForgotPassword_Fragment).commit();
                break;
            case R.id.createAccount:

                // Replace signUp fragment with animation
                fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, new SignUp_Fragment(),
                                Utils.SignUp_Fragment).commit();
                break;
        }
    }

    private void showRedirectMessage() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Task task = (Utils.Task) bundle.getSerializable(Utils.Bundle_Task);
            Boolean status = bundle.getBoolean(Utils.Bundle_Status);

            if (task == Utils.Task.Submit) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_add_new_company));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_add_new_company));
                }
            } else if (task == Utils.Task.SendEmail) {
                new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_send_forget_password_email));
            }
        }
    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        String getEmailId = emailID.getText().toString();
        String getPassword = password.getText().toString();

        // Check pattern for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check for both field is empty or not
        if (getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_invalid_enter_credentials));
            emailID.requestFocus();
        }
        // Check if email id is valid or not
        else if (!m.find()) {
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_invalid_email_format));
            emailID.requestFocus();
        } else {
            new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void getCompany() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s?email=%4$s&password=%5$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_STAFFS,
                emailID.getText().toString(),
                password.getText().toString());
        String xml = parser.getXmlFromUrl(url, true, null, null);

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1 = (Element) nl1.item(0);
                Company company = new Company();

                company.setCompanyId(Integer.parseInt(parser.getValue(e1, XML_NODE_COMPANY_ID)));
                company.setCompanyName(parser.getValue(e1, XML_NODE_COMPANY_NAME));
                company.setCompanyEmail(parser.getValue(e1, XML_NODE_COMPANY_EMAIL));
                company.setCompanyPassword(parser.getValue(e1, XML_NODE_COMPANY_PASSWORD));
                company.setCompanyPhone(parser.getValue(e1, XML_NODE_COMPANY_PHONE));
                company.setCompanyAddress(parser.getValue(e1, XML_NODE_COMPANY_ADDRESS));
                company.setMondayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_MONDAY_YN)) > 0);
                company.setMondayStartTime(parser.getValue(e1, XML_NODE_MONDAY_START_TIME));
                company.setMondayEndTime(parser.getValue(e1, XML_NODE_MONDAY_END_TIME));
                company.setTuesdayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_TUESDAY_YN)) > 0);
                company.setTuesdayStartTime(parser.getValue(e1, XML_NODE_TUESDAY_START_TIME));
                company.setTuesdayEndTime(parser.getValue(e1, XML_NODE_TUESDAY_END_TIME));
                company.setWednesdayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_WEDNESDAY_YN)) > 0);
                company.setWednesdayStartTime(parser.getValue(e1, XML_NODE_WEDNESDAY_START_TIME));
                company.setWednesdayEndTime(parser.getValue(e1, XML_NODE_WEDNESDAY_END_TIME));
                company.setThursdayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_THURSDAY_YN)) > 0);
                company.setThursdayStartTime(parser.getValue(e1, XML_NODE_THURSDAY_START_TIME));
                company.setThursdayEndTime(parser.getValue(e1, XML_NODE_THURSDAY_END_TIME));
                company.setFridayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_FRIDAY_YN)) > 0);
                company.setFridayStartTime(parser.getValue(e1, XML_NODE_FRIDAY_START_TIME));
                company.setFridayEndTime(parser.getValue(e1, XML_NODE_FRIDAY_END_TIME));
                company.setSaturdayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_SATURDAY_YN)) > 0);
                company.setSaturdayStartTime(parser.getValue(e1, XML_NODE_SATURDAY_START_TIME));
                company.setSaturdayEndTime(parser.getValue(e1, XML_NODE_SATURDAY_END_TIME));
                company.setSundayYN(Integer.parseInt(parser.getValue(e1, XML_NODE_SUNDAY_YN)) > 0);
                company.setSundayStartTime(parser.getValue(e1, XML_NODE_SUNDAY_START_TIME));
                company.setSundayEndTime(parser.getValue(e1, XML_NODE_SUNDAY_END_TIME));

                db = new CompanyDbHelper(getContext());
                db.deleteAllCompanies();
                db.addCompany(company);

                this.status = Boolean.TRUE;
            }
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    // Download all the active bookings from server
    private void downloadBookings() {
        XMLParser parser = new XMLParser();
        db = new CompanyDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_BOOKINGS, db.getCompanyId());
        String xml = parser.getXmlFromUrl(url, true, null, null);
        ArrayList<Booking> bookings = new ArrayList<>();

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

                BookingDbHelper bookingDb = new BookingDbHelper(getContext());
                bookingDb.deleteAllBookings();
                bookingDb.addAllBookings(bookings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            isNetworkConnected = Utils.isNetworkAvailable(getContext());

            if (isNetworkConnected == Boolean.TRUE) {
                if (Utils.isNetworkAvailable(getContext())) {
                    getCompany();
                    downloadBookings();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);

            if (isNetworkConnected == Boolean.TRUE) {
                if (status == Boolean.TRUE) {
                    Intent myIntent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(myIntent);
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_invalid_login));
                    emailID.requestFocus();
                }
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
