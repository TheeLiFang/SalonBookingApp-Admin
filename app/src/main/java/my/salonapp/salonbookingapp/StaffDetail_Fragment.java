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
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StaffDetail_Fragment extends Fragment implements OnClickListener {

    // URLs
    private static final String ASPX_SUBMIT_STAFF = "SubmitStaff.aspx";
    private static final String ASPX_UPDATE_STAFF = "UpdateStaff.aspx";
    private static final String ASPX_DELETE_STAFF = "DeleteStaff.aspx";

    // Control fields
    private View view;
    private TextView staffTitle;
    private EditText fullName;
    private EditText emailId;
    private EditText mobileNumber;
    private static FloatingActionButton addStaffBtn;
    private static FloatingActionButton updateStaffBtn;
    private static FloatingActionButton deleteStaffBtn;
    private static FloatingActionButton backStaffBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private int staffId;
    private CompanyDbHelper companyDb;

    public StaffDetail_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.staffdetail_layout, container, false);
        task = null;

        initViews();
        setListeners();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        staffTitle = view.findViewById(R.id.staffTitle);

        fullName = view.findViewById(R.id.fullName);
        // Default focus on staff name field once enter the page
        fullName.requestFocus();

        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = view.findViewById(R.id.mobileNumber);

        addStaffBtn = view.findViewById(R.id.addStaffBtn);

        updateStaffBtn = view.findViewById(R.id.updateStaffBtn);
        deleteStaffBtn = view.findViewById(R.id.deleteStaffBtn);
        backStaffBtn = view.findViewById(R.id.backStaffBtn);

        showData();
    }

    // Set event listeners
    private void setListeners() {
        addStaffBtn.setOnClickListener(this);
        updateStaffBtn.setOnClickListener(this);
        deleteStaffBtn.setOnClickListener(this);
        backStaffBtn.setOnClickListener(this);
    }

    // Toggle buttons and display selected staff's data if it is edit mode
    private void showData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Mode mode = (Utils.Mode) bundle.getSerializable(Utils.Bundle_Mode);

            if (mode == Utils.Mode.Insert) {
                updateStaffBtn.setVisibility(View.GONE);
                deleteStaffBtn.setVisibility(View.GONE);
            } else {
                staffTitle.setText(getString(R.string.editStaff));

                staffId = bundle.getInt(Utils.Bundle_ID);

                if (staffId > 0) {
                    StaffDbHelper db = new StaffDbHelper(getContext());
                    Staff staff = db.getStaffById(staffId);

                    fullName.setText(staff.getStaffName());
                    emailId.setText(staff.getStaffEmail());
                    mobileNumber.setText(staff.getStaffPhone());
                }

                addStaffBtn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addStaffBtn:
                // Add staff
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_add_new_staff),
                            Utils.Task.Submit.toString());
                }
                break;

            case R.id.updateStaffBtn:
                // Update staff
                task = Utils.Task.Update;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_update_staff),
                            Utils.Task.Update.toString());
                }
                break;

            case R.id.deleteStaffBtn:
                // Delete staff
                task = Utils.Task.Delete;

                doSubmission(getString(R.string.confirmation_delete_staff),
                        Utils.Task.Delete.toString());
                break;

            case R.id.backStaffBtn:
                // Back to staff page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Staff_Fragment(),
                                Utils.Staff_Fragment)
                        .commit();
                break;
        }
    }

    // Check validation method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        String name = fullName.getText().toString();
        String email = emailId.getText().toString();
        String phone = mobileNumber.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(email);

        // Check if all the strings are null or not
        if (name.equals("") || name.length() == 0
                || email.equals("") || email.length() == 0
                || phone.equals("") || phone.length() == 0) {
            validate = Boolean.FALSE;
            new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));

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

    private void addStaff() {
        companyDb = new CompanyDbHelper(getContext());

        try {
            String url = String.format("%1$s%2$s%3$s?staff=%4$s&email=%5$s&phone=%6$s&companyID=%7$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_SUBMIT_STAFF,
                    fullName.getText().toString(),
                    emailId.getText().toString(),
                    mobileNumber.getText().toString(),
                    companyDb.getCompanyId());

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void updateStaff() {
        companyDb = new CompanyDbHelper(getContext());

        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&staff=%5$s&email=%6$s&phone=%7$s&companyID=%8$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_UPDATE_STAFF,
                    staffId,
                    fullName.getText().toString(),
                    emailId.getText().toString(),
                    mobileNumber.getText().toString(),
                    companyDb.getCompanyId());

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void deleteStaff() {
        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_DELETE_STAFF,
                    staffId);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", Objects.requireNonNull(e.getMessage()));
        }
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            isNetworkConnected = Utils.isNetworkAvailable(getContext());

            if (isNetworkConnected == Boolean.TRUE) {
                if (param[0].equals(Utils.Task.Submit.toString())) {
                    addStaff();
                } else if (param[0].equals(Utils.Task.Update.toString())) {
                    updateStaff();
                } else if (param[0].equals(Utils.Task.Delete.toString())) {
                    deleteStaff();
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
                bundle.putInt(Utils.Bundle_ID, staffId);

                Staff_Fragment fragment = new Staff_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, fragment, Utils.Staff_Fragment)
                        .commit();
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
