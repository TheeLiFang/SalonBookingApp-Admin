package my.salonapp.salonbookingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientDetail_Fragment extends Fragment implements OnClickListener {

    // URLs
    private static final String ASPX_SUBMIT_CLIENT = "SubmitClientByCompany.aspx";
    private static final String ASPX_CHECK_CLIENT_EMAIL_EXISTENCE = "CheckClientEmailExistence.aspx";
    private static final String ASPX_UPDATE_CLIENT = "UpdateClient.aspx";

    // XML nodes for get email existence
    private static final String XML_NODE_EMAIL_REGISTERED = "RegisteredYN";

    // Control fields
    private View view;
    private TextView clientTitle;
    private EditText fullName;
    private EditText emailId;
    private EditText mobileNumber;
    private EditText allergicRemark;
    private EditText remark;
    private static FloatingActionButton addClientBtn;
    private static FloatingActionButton updateClientBtn;
    private static FloatingActionButton backClientBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private Boolean registeredYN;
    private String getEmailId;
    private int clientId;

    public ClientDetail_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.clientdetail_layout, container, false);
        task = null;

        initViews();
        setListeners();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        clientTitle = view.findViewById(R.id.clientTitle);

        fullName = view.findViewById(R.id.fullName);
        // Default focus on client name field once enter the page
        fullName.requestFocus();

        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        allergicRemark = view.findViewById(R.id.allergicRemark);
        allergicRemark.setFocusable(false);
        remark = view.findViewById(R.id.remark);
        remark.setFocusable(false);

        addClientBtn = view.findViewById(R.id.addClientBtn);

        updateClientBtn = view.findViewById(R.id.updateClientBtn);
        backClientBtn = view.findViewById(R.id.backClientBtn);

        showData();
    }

    // Set event listeners
    private void setListeners() {
        addClientBtn.setOnClickListener(this);
        updateClientBtn.setOnClickListener(this);
        backClientBtn.setOnClickListener(this);
    }

    // Toggle buttons and display selected client's data if it is edit mode
    private void showData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Mode mode = (Utils.Mode) bundle.getSerializable(Utils.Bundle_Mode);

            if (mode == Utils.Mode.Insert) {
                updateClientBtn.setVisibility(View.GONE);
            } else {
                clientTitle.setText(getString(R.string.editClient));

                clientId = bundle.getInt(Utils.Bundle_ID);

                if (clientId > 0) {
                    ClientDbHelper db = new ClientDbHelper(getContext());
                    Client client = db.getClientById(clientId);

                    fullName.setText(client.getClientName());
                    emailId.setText(client.getClientEmail());
                    emailId.setInputType(InputType.TYPE_NULL);
                    mobileNumber.setText(client.getClientPhone());
                    // Replace delimiter ';' and display as new line in edit texts
                    allergicRemark.setText(client.getClientAllergicRemark().replace(
                            getString(R.string.delimiter), getString(R.string.newline)
                    ));
                    remark.setText(client.getClientRemark().replace(
                            getString(R.string.delimiter), getString(R.string.newline)
                    ));
                }

                addClientBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addClientBtn:
                // Add client
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_add_new_client),
                            Utils.Task.Submit.toString());
                }
                break;

            case R.id.updateClientBtn:
                // Update client
                task = Utils.Task.Update;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_update_client),
                            Utils.Task.Update.toString());
                }
                break;

            case R.id.backClientBtn:
                // Back to client page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Client_Fragment(),
                                Utils.Client_Fragment)
                        .commit();
                break;
        }
    }

    // Check validation method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        String name = fullName.getText().toString();
        getEmailId = emailId.getText().toString();
        String phone = mobileNumber.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);

        // Check if all the strings are null or not
        if (name.equals("") || name.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || phone.equals("") || phone.length() == 0) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));

            // Set to focus on missing fill in field
            if (name.equals("") || name.length() == 0) {
                fullName.requestFocus();
            } else if (getEmailId.equals("") || getEmailId.length() == 0) {
                emailId.requestFocus();
            }

            // Check if email id valid or not
        } else if (!m.find()) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_invalid_email));
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

    private void addClient() {
        String strAllergicRemark = "";
        String strRemark = "";

        if (!(allergicRemark.getText().toString().trim().equals("")) && allergicRemark.getText().toString().trim().length() > 0) {
            strAllergicRemark = setMultiLineTexts(allergicRemark);
        }

        if (!(remark.getText().toString().trim().equals("")) && remark.getText().toString().trim().length() > 0) {
            strRemark = setMultiLineTexts(remark);
        }

        try {
            String url = String.format("%1$s%2$s%3$s?client=%4$s&email=%5$s&phone=%6$s&allergic=%7$s&remark=%8$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_SUBMIT_CLIENT,
                    fullName.getText().toString(),
                    emailId.getText().toString(),
                    mobileNumber.getText().toString(),
                    strAllergicRemark,
                    strRemark);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void updateClient() {
        String strAllergicRemark = "";
        String strRemark = "";

        if (!(allergicRemark.getText().toString().trim().equals("")) && allergicRemark.getText().toString().trim().length() > 0) {
            strAllergicRemark = setMultiLineTexts(allergicRemark);
        }

        if (!(remark.getText().toString().trim().equals("")) && remark.getText().toString().trim().length() > 0) {
            strRemark = setMultiLineTexts(remark);
        }

        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&client=%5$s&email=%6$s&phone=%7$s&allergic=%8$s&remark=%9$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_UPDATE_CLIENT,
                    clientId,
                    fullName.getText().toString(),
                    emailId.getText().toString(),
                    mobileNumber.getText().toString(),
                    strAllergicRemark,
                    strRemark);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    // Allow edit text to store multi-line text by append delimiter ';' and store to database
    private String setMultiLineTexts(EditText editText) {
        StringBuilder remark = new StringBuilder();
        Scanner scanner = new Scanner(editText.getText().toString().trim());

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            remark.append(line).append(getString(R.string.delimiter));
        }

        return remark.toString().substring(0, remark.toString().length() - 1);
    }

    private void checkEmailRegisteredYN(String getEmailId) {
        try {
            String url = String.format("%1$s%2$s%3$s?email=%4$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_CHECK_CLIENT_EMAIL_EXISTENCE,
                    getEmailId);

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
                        addClient();
                    }
                } else if (param[0].equals(Utils.Task.Update.toString())) {
                    registeredYN = Boolean.FALSE;
                    updateClient();
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

                Client_Fragment fragment = new Client_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, fragment, Utils.Client_Fragment)
                        .commit();
            } else if (isNetworkConnected == Boolean.TRUE && registeredYN == Boolean.TRUE) {
                emailId.requestFocus();
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.email_id_registered));
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
