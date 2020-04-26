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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceDetail_Fragment extends Fragment implements OnClickListener {

    // URLs
    private static final String ASPX_SUBMIT_SALON_SERVICE = "SubmitSalonService.aspx";
    private static final String ASPX_UPDATE_SALON_SERVICE = "UpdateSalonService.aspx";
    private static final String ASPX_DELETE_SALON_SERVICE = "DeleteSalonService.aspx";

    // Control fields
    private View view;
    private TextView serviceTitle;
    private Spinner categorySpinner;
    private EditText serviceName;
    private EditText servicePrice;
    private EditText serviceDuration;
    private static FloatingActionButton addServiceBtn;
    private static FloatingActionButton updateServiceBtn;
    private static FloatingActionButton deleteServiceBtn;
    private static FloatingActionButton backServiceBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private int serviceId, categoryId;
    private CategoryDbHelper db;
    private List<Category> categories = new ArrayList<>();
    private Category category;

    public ServiceDetail_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.servicedetail_layout, container, false);
        task = null;

        initViews();
        setListeners();

        return view;
    }

    // Initialize all views
    private void initViews() {
        serviceTitle = view.findViewById(R.id.serviceTitle);

        categorySpinner = view.findViewById(R.id.spinner);
        categorySpinner.requestFocus();

        serviceName = view.findViewById(R.id.serviceName);
        servicePrice = view.findViewById(R.id.servicePrice);
        serviceDuration = view.findViewById(R.id.serviceDuration);

        addServiceBtn = view.findViewById(R.id.addServiceBtn);

        updateServiceBtn = view.findViewById(R.id.updateServiceBtn);
        deleteServiceBtn = view.findViewById(R.id.deleteServiceBtn);
        backServiceBtn = view.findViewById(R.id.backServiceBtn);

        // Generate spinner list
        db = new CategoryDbHelper(getContext());
        categories = db.getAllCategories();

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) parent.getSelectedItem();
                categoryId = category.getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        showData();
    }

    // Set Listeners
    private void setListeners() {
        addServiceBtn.setOnClickListener(this);
        updateServiceBtn.setOnClickListener(this);
        deleteServiceBtn.setOnClickListener(this);
        backServiceBtn.setOnClickListener(this);
    }

    // Toggle buttons and display selected staff's data if it is edit mode
    private void showData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Mode mode = (Utils.Mode) bundle.getSerializable(Utils.Bundle_Mode);

            if (mode == Utils.Mode.Insert) {
                updateServiceBtn.setVisibility(View.GONE);
                deleteServiceBtn.setVisibility(View.GONE);
            } else {
                serviceTitle.setText(getString(R.string.editService));

                serviceId = bundle.getInt(Utils.Bundle_ID);

                if (serviceId > 0) {
                    ServiceDbHelper serviceDb = new ServiceDbHelper(getContext());
                    Service service = serviceDb.getServiceById(serviceId);

                    serviceName.setText(service.getServiceName());
                    servicePrice.setText(String.valueOf(service.getServicePrice()));
                    serviceDuration.setText(Integer.toString(service.getServiceDuration()));

                    // Set selected category to spinner
                    Category cat = new Category(service.getCategoryId(), service.getCategoryName());
                    categorySpinner.setSelection(getIndex(categorySpinner, cat));
                }

                addServiceBtn.setVisibility(View.GONE);
            }
        }
    }

    private int getIndex(Spinner spinner, Category cat) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(cat.getCategoryName())) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addServiceBtn:
                // Add service
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_add_new_service),
                            Utils.Task.Submit.toString());
                }
                break;

            case R.id.updateServiceBtn:
                // Update service
                task = Utils.Task.Update;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    doSubmission(getString(R.string.confirmation_update_service),
                            Utils.Task.Update.toString());
                }
                break;

            case R.id.deleteServiceBtn:
                // Delete service
                task = Utils.Task.Delete;

                doSubmission(getString(R.string.confirmation_delete_service),
                        Utils.Task.Delete.toString());
                break;

            case R.id.backServiceBtn:
                // Back to service page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Service_Fragment(),
                                Utils.Service_Fragment)
                        .commit();
                break;
        }
    }

    // Check Validation Method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Get all edit text texts
        String name = serviceName.getText().toString();
        String price = servicePrice.getText().toString();
        String duration = serviceDuration.getText().toString();

        // Check if all strings are null or not
        if (name.equals("") || name.length() == 0
                || price.equals("") || price.length() == 0
                || duration.equals("") || duration.length() == 0) {

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
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                taskSelection);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void addService() {
        try {
            String url = String.format("%1$s%2$s%3$s?service=%4$s&price=%5$s&duration=%6$s&categoryID=%7$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_SUBMIT_SALON_SERVICE,
                    serviceName.getText().toString(),
                    servicePrice.getText().toString(),
                    serviceDuration.getText().toString(),
                    categoryId);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void updateService() {
        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s&service=%5$s&price=%6$s&duration=%7$s&categoryID=%8$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_UPDATE_SALON_SERVICE,
                    serviceId,
                    serviceName.getText().toString(),
                    servicePrice.getText().toString(),
                    serviceDuration.getText().toString(),
                    categoryId);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
        } catch (Exception e) {
            status = Boolean.FALSE;
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }
    }

    private void deleteService() {
        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_DELETE_SALON_SERVICE,
                    serviceId);

            XMLParser parser = new XMLParser();
            status = parser.httpParamSubmission(url);
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
                if (param[0].equals(Utils.Task.Submit.toString())) {
                    addService();
                } else if (param[0].equals(Utils.Task.Update.toString())) {
                    updateService();
                } else if (param[0].equals(Utils.Task.Delete.toString())) {
                    deleteService();
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
                bundle.putInt(Utils.Bundle_ID, categoryId);

                Service_Fragment fragment = new Service_Fragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, fragment, Utils.Service_Fragment)
                        .commit();
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
