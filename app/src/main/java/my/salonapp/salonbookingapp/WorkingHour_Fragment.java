package my.salonapp.salonbookingapp;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class WorkingHour_Fragment extends Fragment implements View.OnClickListener {

    // URL
    private static final String ASPX_COMPANY_UPDATE_WORKING_HOUR = "UpdateCompanyWorkingHour.aspx";

    // Control fields
    private View view;
    private CheckBox mondayYN;
    private EditText mondayStartTime;
    private EditText mondayEndTime;
    private CheckBox tuesdayYN;
    private EditText tuesdayStartTime;
    private EditText tuesdayEndTime;
    private CheckBox wednesdayYN;
    private EditText wednesdayStartTime;
    private EditText wednesdayEndTime;
    private CheckBox thursdayYN;
    private EditText thursdayStartTime;
    private EditText thursdayEndTime;
    private CheckBox fridayYN;
    private EditText fridayStartTime;
    private EditText fridayEndTime;
    private CheckBox saturdayYN;
    private EditText saturdayStartTime;
    private EditText saturdayEndTime;
    private CheckBox sundayYN;
    private EditText sundayStartTime;
    private EditText sundayEndTime;
    private FloatingActionButton updateWorkingHourBtn;
    private FloatingActionButton backWorkingHourBtn;

    // Variables
    private Utils.Task task;
    private Boolean status, isNetworkConnected;
    private int staffId, companyId;
    private CompanyDbHelper db;
    private Company company;

    public WorkingHour_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.workinghour_layout, container, false);

        initViews();
        setListeners();
        showData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // Initiate Views
    private void initViews() {
        mondayYN = view.findViewById(R.id.mondayYN);
        mondayStartTime = view.findViewById(R.id.mondayStartTime);
        mondayEndTime = view.findViewById(R.id.mondayEndTime);
        tuesdayYN = view.findViewById(R.id.tuesdayYN);
        tuesdayStartTime = view.findViewById(R.id.tuesdayStartTime);
        tuesdayEndTime = view.findViewById(R.id.tuesdayEndTime);
        wednesdayYN = view.findViewById(R.id.wednesdayYN);
        wednesdayStartTime = view.findViewById(R.id.wednesdayStartTime);
        wednesdayEndTime = view.findViewById(R.id.wednesdayEndTime);
        thursdayYN = view.findViewById(R.id.thursdayYN);
        thursdayStartTime = view.findViewById(R.id.thursdayStartTime);
        thursdayEndTime = view.findViewById(R.id.thursdayEndTime);
        fridayYN = view.findViewById(R.id.fridayYN);
        fridayStartTime = view.findViewById(R.id.fridayStartTime);
        fridayEndTime = view.findViewById(R.id.fridayEndTime);
        saturdayYN = view.findViewById(R.id.saturdayYN);
        saturdayStartTime = view.findViewById(R.id.saturdayStartTime);
        saturdayEndTime = view.findViewById(R.id.saturdayEndTime);
        sundayYN = view.findViewById(R.id.sundayYN);
        sundayStartTime = view.findViewById(R.id.sundayStartTime);
        sundayEndTime = view.findViewById(R.id.sundayEndTime);

        // Disable input for readonly
        mondayStartTime.setInputType(InputType.TYPE_NULL);
        mondayEndTime.setInputType(InputType.TYPE_NULL);
        tuesdayStartTime.setInputType(InputType.TYPE_NULL);
        tuesdayEndTime.setInputType(InputType.TYPE_NULL);
        wednesdayStartTime.setInputType(InputType.TYPE_NULL);
        wednesdayEndTime.setInputType(InputType.TYPE_NULL);
        thursdayStartTime.setInputType(InputType.TYPE_NULL);
        thursdayEndTime.setInputType(InputType.TYPE_NULL);
        fridayStartTime.setInputType(InputType.TYPE_NULL);
        fridayEndTime.setInputType(InputType.TYPE_NULL);
        saturdayStartTime.setInputType(InputType.TYPE_NULL);
        saturdayEndTime.setInputType(InputType.TYPE_NULL);
        sundayStartTime.setInputType(InputType.TYPE_NULL);
        sundayEndTime.setInputType(InputType.TYPE_NULL);

        updateWorkingHourBtn = view.findViewById(R.id.updateWorkingHourBtn);
        backWorkingHourBtn = view.findViewById(R.id.backWorkingHourBtn);
    }

    // Show start time picker if check box is checked;
    // Otherwise, clear start and end time fields
    private void toggleStartEndTimeEditText(CheckBox checkBox, EditText editText1,
                                            EditText editText2) {
        if (checkBox.isChecked() == Boolean.TRUE) {
            editText1.requestFocus();
            setTimePickerDialog(editText1, editText2);
        } else {
            editText1.setText("");
            editText2.setText("");
        }

        editText1.setEnabled(checkBox.isChecked());
        editText2.setEnabled(checkBox.isChecked());
    }

    // Call first time picker and auto call next time picker if found
    private void setTimePickerDialog(final EditText currentEditText, @Nullable final EditText nextEditText) {
        final Calendar selectedDate = Calendar.getInstance();

        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                currentEditText.setText(simpleDateFormat.format(selectedDate.getTime()));

                if (nextEditText != null) {
                    nextEditText.requestFocus();

                    setTimePickerDialog(nextEditText, null);
                }
            }
        }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), true).show();
    }

    private void setListeners() {
        mondayYN.setOnClickListener(this);
        mondayStartTime.setOnClickListener(this);
        mondayEndTime.setOnClickListener(this);
        tuesdayYN.setOnClickListener(this);
        tuesdayStartTime.setOnClickListener(this);
        tuesdayEndTime.setOnClickListener(this);
        wednesdayYN.setOnClickListener(this);
        wednesdayStartTime.setOnClickListener(this);
        wednesdayEndTime.setOnClickListener(this);
        thursdayYN.setOnClickListener(this);
        thursdayStartTime.setOnClickListener(this);
        thursdayEndTime.setOnClickListener(this);
        fridayYN.setOnClickListener(this);
        fridayStartTime.setOnClickListener(this);
        fridayEndTime.setOnClickListener(this);
        saturdayYN.setOnClickListener(this);
        saturdayStartTime.setOnClickListener(this);
        saturdayEndTime.setOnClickListener(this);
        sundayYN.setOnClickListener(this);
        sundayStartTime.setOnClickListener(this);
        sundayEndTime.setOnClickListener(this);

        updateWorkingHourBtn.setOnClickListener(this);
        backWorkingHourBtn.setOnClickListener(this);
    }

    // Display current logged-in company's monday to sunday working hour
    private void showData() {
        db = new CompanyDbHelper(getContext());
        company = db.getCompany();

        if (company.getMondayYN() == Boolean.TRUE) {
            mondayYN.setChecked(true);
            mondayStartTime.setText(company.getMondayStartTime());
            mondayEndTime.setText(company.getMondayEndTime());
        }

        if (company.getTuesdayYN() == Boolean.TRUE) {
            tuesdayYN.setChecked(true);
            tuesdayStartTime.setText(company.getTuesdayStartTime());
            tuesdayEndTime.setText(company.getTuesdayEndTime());
        }

        if (company.getWednesdayYN() == Boolean.TRUE) {
            wednesdayYN.setChecked(true);
            wednesdayStartTime.setText(company.getWednesdayStartTime());
            wednesdayEndTime.setText(company.getWednesdayEndTime());
        }

        if (company.getThursdayYN() == Boolean.TRUE) {
            thursdayYN.setChecked(true);
            thursdayStartTime.setText(company.getThursdayStartTime());
            thursdayEndTime.setText(company.getThursdayEndTime());
        }

        if (company.getFridayYN() == Boolean.TRUE) {
            fridayYN.setChecked(true);
            fridayStartTime.setText(company.getFridayStartTime());
            fridayEndTime.setText(company.getFridayEndTime());
        }

        if (company.getSaturdayYN() == Boolean.TRUE) {
            saturdayYN.setChecked(true);
            saturdayStartTime.setText(company.getSaturdayStartTime());
            saturdayEndTime.setText(company.getSaturdayEndTime());
        }

        if (company.getSundayYN() == Boolean.TRUE) {
            sundayYN.setChecked(true);
            sundayStartTime.setText(company.getSundayStartTime());
            sundayEndTime.setText(company.getSundayEndTime());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mondayYN:
                toggleStartEndTimeEditText(mondayYN, mondayStartTime, mondayEndTime);
                break;

            case R.id.mondayStartTime:
                setTimePickerDialog(mondayStartTime, mondayEndTime);
                break;

            case R.id.mondayEndTime:
                setTimePickerDialog(mondayEndTime, null);
                break;

            case R.id.tuesdayYN:
                toggleStartEndTimeEditText(tuesdayYN, tuesdayStartTime, tuesdayEndTime);
                break;

            case R.id.tuesdayStartTime:
                setTimePickerDialog(tuesdayStartTime, tuesdayEndTime);
                break;

            case R.id.tuesdayEndTime:
                setTimePickerDialog(tuesdayEndTime, null);
                break;

            case R.id.wednesdayYN:
                toggleStartEndTimeEditText(wednesdayYN, wednesdayStartTime, wednesdayEndTime);
                break;

            case R.id.wednesdayStartTime:
                setTimePickerDialog(wednesdayStartTime, wednesdayEndTime);
                break;

            case R.id.wednesdayEndTime:
                setTimePickerDialog(wednesdayEndTime, null);
                break;

            case R.id.thursdayYN:
                toggleStartEndTimeEditText(thursdayYN, thursdayStartTime, thursdayEndTime);
                break;

            case R.id.thursdayStartTime:
                setTimePickerDialog(thursdayStartTime, thursdayEndTime);
                break;

            case R.id.thursdayEndTime:
                setTimePickerDialog(thursdayEndTime, null);
                break;

            case R.id.fridayYN:
                toggleStartEndTimeEditText(fridayYN, fridayStartTime, fridayEndTime);
                break;

            case R.id.fridayStartTime:
                setTimePickerDialog(fridayStartTime, fridayEndTime);
                break;

            case R.id.fridayEndTime:
                setTimePickerDialog(fridayEndTime, null);
                break;

            case R.id.saturdayYN:
                toggleStartEndTimeEditText(saturdayYN, saturdayStartTime, saturdayEndTime);
                break;

            case R.id.saturdayStartTime:
                setTimePickerDialog(saturdayStartTime, saturdayEndTime);
                break;

            case R.id.saturdayEndTime:
                setTimePickerDialog(saturdayEndTime, null);
                break;

            case R.id.sundayYN:
                toggleStartEndTimeEditText(sundayYN, sundayStartTime, sundayEndTime);
                break;

            case R.id.sundayStartTime:
                setTimePickerDialog(sundayStartTime, sundayEndTime);
                break;

            case R.id.sundayEndTime:
                setTimePickerDialog(sundayEndTime, null);
                break;

            case R.id.updateWorkingHourBtn:
                // Add booking
                task = Utils.Task.Submit;

                // Call checkValidation method
                if (checkValidation() == Boolean.TRUE) {
                    try {
                        if (mondayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(mondayStartTime.getText().toString(), mondayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }

                        if (tuesdayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(tuesdayStartTime.getText().toString(), tuesdayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }

                        if (wednesdayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(wednesdayStartTime.getText().toString(), wednesdayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }

                        if (thursdayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(thursdayStartTime.getText().toString(), thursdayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }

                        if (fridayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(fridayStartTime.getText().toString(), fridayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }

                        if (saturdayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(saturdayStartTime.getText().toString(), saturdayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }

                        if (sundayYN.isChecked() == Boolean.TRUE) {
                            if (isValidTime(sundayStartTime.getText().toString(), sundayEndTime.getText().toString()) == Boolean.FALSE) {
                                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.invalid_start_end_time));
                                return;
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e("Error: ", e.getMessage());
                    }

                    doSubmission(getString(R.string.confirmation_update_working_hour));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.all_fields_are_required));
                }
                break;

            case R.id.backWorkingHourBtn:
                // Back to booking page
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

    private Boolean isValidTime(String startTime, String endTime) throws ParseException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);

            return startDate.before(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Error: ", e.getMessage());
        }

        return Boolean.FALSE;
    }

    // Check validation method
    private Boolean checkValidation() {
        Boolean validate = Boolean.TRUE;

        // Get all edit text values
        Boolean checkYN1 = mondayYN.isChecked();
        String startTime1 = mondayStartTime.getText().toString();
        String endTime1 = mondayEndTime.getText().toString();
        Boolean checkYN2 = tuesdayYN.isChecked();
        String startTime2 = tuesdayStartTime.getText().toString();
        String endTime2 = tuesdayEndTime.getText().toString();
        Boolean checkYN3 = wednesdayYN.isChecked();
        String startTime3 = wednesdayStartTime.getText().toString();
        String endTime3 = wednesdayEndTime.getText().toString();
        Boolean checkYN4 = thursdayYN.isChecked();
        String startTime4 = thursdayStartTime.getText().toString();
        String endTime4 = thursdayEndTime.getText().toString();
        Boolean checkYN5 = fridayYN.isChecked();
        String startTime5 = fridayStartTime.getText().toString();
        String endTime5 = fridayEndTime.getText().toString();
        Boolean checkYN6 = saturdayYN.isChecked();
        String startTime6 = saturdayStartTime.getText().toString();
        String endTime6 = saturdayEndTime.getText().toString();
        Boolean checkYN7 = sundayYN.isChecked();
        String startTime7 = sundayStartTime.getText().toString();
        String endTime7 = sundayEndTime.getText().toString();

        // Check if all the strings are null or not
        if (checkYN1 == Boolean.TRUE) {
            if (startTime1.length() == 0 || endTime1.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        if (checkYN2 == Boolean.TRUE) {
            if (startTime2.length() == 0 || endTime2.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        if (checkYN3 == Boolean.TRUE) {
            if (startTime3.length() == 0 || endTime3.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        if (checkYN4 == Boolean.TRUE) {
            if (startTime4.length() == 0 || endTime4.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        if (checkYN5 == Boolean.TRUE) {
            if (startTime5.length() == 0 || endTime5.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        if (checkYN6 == Boolean.TRUE) {
            if (startTime6.length() == 0 || endTime6.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        if (checkYN7 == Boolean.TRUE) {
            if (startTime7.length() == 0 || endTime7.length() == 0) {
                validate = Boolean.FALSE;
            }
        }

        return validate;
    }

    private void doSubmission(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirmation))
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new WorkingHour_Fragment.BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    // Update company working hour
    private void updateWorkingHour() {
        CompanyDbHelper db = new CompanyDbHelper(getContext());
        companyId = db.getCompanyId();

        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s\n" +
                            "&yn1=%5$s&s1=%6$s&e1=%7$s\n" +
                            "&yn2=%8$s&s2=%9$s&e2=%10$s\n" +
                            "&yn3=%11$s&s3=%12$s&e3=%13$s\n" +
                            "&yn4=%14$s&s4=%15$s&e4=%16$s\n" +
                            "&yn5=%17$s&s5=%18$s&e5=%19$s\n" +
                            "&yn6=%20$s&s6=%21$s&e6=%22$s\n" +
                            "&yn7=%23$s&s7=%24$s&e7=%25$s\n",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_COMPANY_UPDATE_WORKING_HOUR,
                    companyId, mondayYN.isChecked() ? 1 : 0, mondayStartTime.getText().toString(),
                    mondayEndTime.getText().toString(), tuesdayYN.isChecked() ? 1 : 0, tuesdayStartTime.getText().toString(),
                    tuesdayEndTime.getText().toString(), wednesdayYN.isChecked() ? 1 : 0, wednesdayStartTime.getText().toString(),
                    wednesdayEndTime.getText().toString(), thursdayYN.isChecked() ? 1 : 0, thursdayStartTime.getText().toString(),
                    thursdayEndTime.getText().toString(), fridayYN.isChecked() ? 1 : 0, fridayStartTime.getText().toString(),
                    fridayEndTime.getText().toString(), saturdayYN.isChecked() ? 1 : 0, saturdayStartTime.getText().toString(),
                    saturdayEndTime.getText().toString(), sundayYN.isChecked() ? 1 : 0, sundayStartTime.getText().toString(),
                    sundayEndTime.getText().toString());

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
                updateWorkingHour();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);

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
