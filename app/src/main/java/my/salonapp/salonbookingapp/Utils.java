package my.salonapp.salonbookingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Java Singleton Design Pattern - Bill Pugh Singleton Implementation
 * https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples
 *
 * @author Pankaj
 * @since Mar 03, 2013
 */
public class Utils {
    private static final Utils INSTANCE = new Utils();

    // Default constructor
    private Utils() {
    }

    public static Utils getInstance() {
        return INSTANCE;
    }

    // Indicate insert or edit page
    public enum Mode {
        Insert,
        Edit
    }

    // Indicate user is performing submit, update or delete operation
    public enum Task {
        SendEmail,
        Submit,
        Update,
        Delete
    }

    // Server URLs
    public static final String SERVER_URL = "http://jtsalon.southeastasia.cloudapp.azure.com/";
    public static final String SERVER_FOLDER = "jtsalon_exc/";

    // Set connection timeout
    public static int timeout_submission = 30000;

    // XML Nodes
    public static final String XML_NODE_ROW = "Row";
    public static final String XML_NODE_ITEM = "Item";
    public static final String XML_NODE_DOWNLOAD = "Download";
    public static final String XML_NODE_STATUS = "Status";
    public static final String XML_NODE_INSERT = "Insert";

    // Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    // Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String ForgotPassword_Fragment = "ForgotPassword_Fragment";
    public static final String Home_Fragment = "Home_Fragment";
    public static final String WorkingHour_Fragment = "WorkingHour_Fragment";
    public static final String Staff_Fragment = "Staff_Fragment";
    public static final String StaffDetail_Fragment = "StaffDetail_Fragment";
    public static final String Service_Fragment = "Service_Fragment";
    public static final String ServiceDetail_Fragment = "ServiceDetail_Fragment";
    public static final String Booking_Fragment = "Booking_Fragment";
    public static final String BookingDetail_Fragment = "BookingDetail_Fragment";
    public static final String Payment_Fragment = "Payment_Fragment";
    public static final String PaymentSuccessful_Fragment = "PaymentSuccessful_Fragment";
    public static final String Client_Fragment = "Client_Fragment";
    public static final String ClientDetail_Fragment = "ClientDetail_Fragment";
    public static final String TransactionHistory_Fragment = "TransactionHistory_Fragment";
    public static final String TransactionHistoryDetail_Fragment = "TransactionHistoryDetail_Fragment";
    public static final String More_Fragment = "More_Fragment";
    public static final String ChangePassword_Fragment = "ChangePassword_Fragment";

    // Bundle to pass data between two fragment pages
    public static final String Bundle_Mode = "mode";
    public static final String Bundle_Task = "task";
    public static final String Bundle_Status = "status";
    public static final String Bundle_ID = "id";

    private static Boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
            }
        } else {
            NetworkInfo network = connectivityManager.getActiveNetworkInfo();

            return network != null && network.isConnected();
        }

        return false;
    }

    public static Boolean isNetworkAvailable(Context context) {
        if (isNetworkConnected(context) == Boolean.TRUE) {
            try {
                URL url = new URL(Utils.SERVER_URL);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int timeout_network_check = 10000;
                connection.setConnectTimeout(timeout_network_check);
                connection.connect();

                return (connection.getResponseCode() == HttpURLConnection.HTTP_OK) ? Boolean.TRUE : Boolean.FALSE;
            } catch (Exception e) {
                return Boolean.FALSE;
            }
        }

        return Boolean.FALSE;
    }

    private <T> String writeXML(String rootElement, T obj) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
                "<" + rootElement + ">" +
                obj.toString() +
                "</" + rootElement + ">";
    }
}
