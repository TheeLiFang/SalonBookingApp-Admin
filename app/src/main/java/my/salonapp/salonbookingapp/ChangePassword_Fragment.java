package my.salonapp.salonbookingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ChangePassword_Fragment extends Fragment implements OnClickListener {

    // URLs
    private static final String ASPX_UPDATE_PASSWORD = "UpdateCompanyPassword.aspx";
    private static final String ASPX_GET_ALL_STAFFS = "GetAdminLogin.aspx";

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

    // Control fields
    private View view;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmPassword;
    private static FloatingActionButton updatePasswordBtn;
    private static FloatingActionButton backPasswordBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private CompanyDbHelper companyDb;
    private Company company;

    public ChangePassword_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.changepassword_layout, container, false);
        task = null;

        initViews();
        setListeners();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);

        updatePasswordBtn = view.findViewById(R.id.updatePasswordBtn);
        backPasswordBtn = view.findViewById(R.id.backPasswordBtn);
    }

    // Set event listeners
    private void setListeners() {
        updatePasswordBtn.setOnClickListener(this);
        backPasswordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updatePasswordBtn:
                // Update password
                task = Utils.Task.Update;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_update_password),
                            Utils.Task.Update.toString());
                }
                break;

            case R.id.backPasswordBtn:
                // Back to setting page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new More_Fragment(),
                                Utils.More_Fragment)
                        .commit();
                break;
        }
    }

    // Check validation method
    private Boolean checkValidation() {
        companyDb = new CompanyDbHelper(getContext());
        company = companyDb.getCompany();

        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        String currPassword = currentPassword.getText().toString();
        String password = newPassword.getText().toString();
        String retypePassword = confirmPassword.getText().toString();

        // Check if all the strings are null or not
        if (currPassword.equals("") || password.equals("")
                || retypePassword.equals("")) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));
        }
        // Check if current password equal to database
        else if (!currPassword.equals(company.getCompanyPassword())) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.password_not_match_database));
        }
        // Check if both password should be equal
        else if (!password.equals(retypePassword)) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.new_password_not_match));
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
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                taskSelection);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void updatePassword() {
        companyDb = new CompanyDbHelper(getContext());

        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&password=%5$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_UPDATE_PASSWORD,
                    companyDb.getCompanyId(),
                    newPassword.getText().toString());

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void getCompany() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s?email=%4$s&password=%5$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_STAFFS,
                company.getCompanyEmail(),
                newPassword.getText().toString());
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

                companyDb = new CompanyDbHelper(getContext());
                companyDb.deleteAllCompanies();
                companyDb.addCompany(company);

                this.status = Boolean.TRUE;
            }
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            isNetworkConnected = Utils.isNetworkAvailable(getContext());

            if (isNetworkConnected == Boolean.TRUE) {
                if (param[0].equals(Utils.Task.Update.toString())) {
                    updatePassword();
                    getCompany();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isNetworkConnected == Boolean.TRUE) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Utils.Bundle_Task, task);
                bundle.putBoolean(Utils.Bundle_Status, status);

                More_Fragment fragment = new More_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, fragment, Utils.More_Fragment)
                        .commit();
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
