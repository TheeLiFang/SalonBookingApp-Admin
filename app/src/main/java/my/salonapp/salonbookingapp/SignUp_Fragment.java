package my.salonapp.salonbookingapp;

import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp_Fragment extends Fragment implements OnClickListener {

    // URLs
    private static final String ASPX_SUBMIT_COMPANY = "SubmitCompany.aspx";
    private static final String ASPX_CHECK_COMPANY_EMAIL_EXISTENCE = "CheckCompanyEmailExistence.aspx";

    // XML nodes for get email existence
    private static final String XML_NODE_EMAIL_REGISTERED = "RegisteredYN";

    // Control fields
    private View view;
    private EditText fullName;
    private EditText emailId;
    private EditText mobileNumber;
    private EditText location;
    private EditText password;
    private EditText confirmPassword;
    private TextView login;
    private Button signUpButton;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private Boolean registeredYN;
    private String getEmailId;
    private int companyId;

    public SignUp_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        task = null;

        initViews();
        setListeners();
        showRedirectMessage();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        fullName = view.findViewById(R.id.fullName);
        // Default focus on staff name field once enter the page
        fullName.requestFocus();

        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        location = view.findViewById(R.id.location);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirmPassword);

        signUpButton = view.findViewById(R.id.signUpBtn);
        login = view.findViewById(R.id.already_user);

        // Setting text selector over textviews
        XmlPullParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp, null);

            login.setTextColor(csl);
        } catch (Exception e) {
        }
    }

    // Set event listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                // Add company
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                            Utils.Task.Submit.toString());
                }
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().replaceLoginFragment();
                break;
        }

    }

    private void showRedirectMessage() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Task task = (Utils.Task) bundle.getSerializable(Utils.Bundle_Task);
            Boolean status = bundle.getBoolean(Utils.Bundle_Status);
            //int id = bundle.getInt(Utils.Bundle_ID);
            String message = "";

            if (task == Utils.Task.Submit) {
                if (status == Boolean.FALSE) {
                    message = getString(R.string.email_id_registered);
                }
            }

            if (message.length() > 0) {
                new CustomToast().Show_Toast(getActivity(), view, message);
            }
        }
    }

    // Check validation method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        String getFullName = fullName.getText().toString();
        getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getLocation = location.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all strings are null or not
        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getLocation.equals("") || getLocation.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));

            // Set to focus on missing fill in field
            if (getFullName.equals("") || getFullName.length() == 0) {
                fullName.requestFocus();
            } else if (getEmailId.equals("") || getEmailId.length() == 0) {
                emailId.requestFocus();
            } else if (getMobileNumber.equals("") || getMobileNumber.length() == 0) {
                mobileNumber.requestFocus();
            } else if (getLocation.equals("") || getLocation.length() == 0) {
                location.requestFocus();
            } else if (getPassword.equals("") || getPassword.length() == 0) {
                password.requestFocus();
            } else if (getConfirmPassword.equals("") || getConfirmPassword.length() == 0) {
                confirmPassword.requestFocus();
            }
        }
        // Check if email id valid or not
        else if (!m.find()) {
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_invalid_email));
        }
        // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword)) {
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.password_not_match));
        }

        return validate;
    }

    private void addStaff() {
        try {
            String url = String.format("%1$s%2$s%3$s?company=%4$s&password=%5$s&email=%6$s&phone=%7$s&address=%8$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_SUBMIT_COMPANY,
                    fullName.getText().toString(),
                    password.getText().toString(),
                    emailId.getText().toString(),
                    mobileNumber.getText().toString(),
                    location.getText().toString());

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void checkEmailRegisteredYN(String getEmailId) {
        try {
            String url = String.format("%1$s%2$s%3$s?email=%4$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_CHECK_COMPANY_EMAIL_EXISTENCE,
                    getEmailId);

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
                NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
                Element e = (Element) nl.item(0);

                status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

                if (status == Boolean.TRUE) {
                    NodeList nll = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                    Element el = (Element) nll.item(0);

                    registeredYN = Boolean.parseBoolean(parser.getValue(el, XML_NODE_EMAIL_REGISTERED));
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
                if (param[0].equals(Utils.Task.Submit.toString())) {
                    checkEmailRegisteredYN(getEmailId);
                    if (registeredYN == Boolean.FALSE) {
                        addStaff();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isNetworkConnected == Boolean.TRUE && registeredYN == Boolean.FALSE) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.Bundle_Task, task);
                bundle.putBoolean(Utils.Bundle_Status, status);
                bundle.putInt(Utils.Bundle_ID, companyId);

                Login_Fragment fragment = new Login_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, fragment, Utils.Login_Fragment)
                        .commit();
            } else if (isNetworkConnected == Boolean.TRUE && registeredYN == Boolean.TRUE) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.Bundle_Task, task);
                bundle.putBoolean(Utils.Bundle_Status, Boolean.FALSE);

                SignUp_Fragment fragment = new SignUp_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContainer, fragment, Utils.SignUp_Fragment)
                        .commit();
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
