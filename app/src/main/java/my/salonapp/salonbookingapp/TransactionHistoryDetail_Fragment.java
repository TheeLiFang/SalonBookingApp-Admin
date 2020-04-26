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
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import my.salonapp.salonbookingapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionHistoryDetail_Fragment extends Fragment implements OnClickListener {

    // URL
    private static final String ASPX_RESEND_RECEIPT = "ResendReceipt.aspx";

    // Control fields
    private View view;
    private ListView listView;
    private TextView bookingDate;
    private TextView bookingId;
    private TextView clientName;
    private TextView staffName;
    private TextView subtotal;
    private static FloatingActionsMenu multiple_actions_left;
    private static FloatingActionButton resendReceiptTransactionHistoryBtn;
    private static FloatingActionButton backTransactionHistoryBtn;

    // Variables
    private Boolean status, isNetworkConnected;
    private int transactionId;

    public TransactionHistoryDetail_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.transactionhistorydetail_layout, container, false);

        initViews();
        setListeners();

        return view;
    }

    // Initialize all the views
    private void initViews() {
        bookingDate = view.findViewById(R.id.bookingDate);
        bookingId = view.findViewById(R.id.bookingId);
        clientName = view.findViewById(R.id.clientName);
        staffName = view.findViewById(R.id.staffName);
        listView = view.findViewById(R.id.card_listView_transaction_history_service_List);
        subtotal = view.findViewById(R.id.subtotal);

        multiple_actions_left = view.findViewById(R.id.multiple_actions_left);
        resendReceiptTransactionHistoryBtn = view.findViewById(R.id.resendReceiptTransactionHistoryBtn);
        backTransactionHistoryBtn = view.findViewById(R.id.backTransactionHistoryBtn);

        showData();
    }

    // Set event listeners
    private void setListeners() {
        resendReceiptTransactionHistoryBtn.setOnClickListener(this);
        backTransactionHistoryBtn.setOnClickListener(this);
    }

    // Toggle buttons and display selected staff's data if it is edit mode
    private void showData() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            transactionId = bundle.getInt(Utils.Bundle_ID);

            if (transactionId > 0) {
                TransactionHistoryDbHelper transactionHistoryDb = new TransactionHistoryDbHelper(getContext());
                TransactionHistory transactionHistory = transactionHistoryDb.getTransactionHistoryById(transactionId);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                bookingDate.setText(simpleDateFormat.format(transactionHistory.getBookingDate()));
                bookingId.setText(transactionHistory.getRefNo());
                clientName.setText(transactionHistory.getClientName());
                staffName.setText(transactionHistory.getStaffName());

                ArrayList<TransactionHistoryService> transactionHistoryServices =
                        transactionHistoryDb.getTransactionHistoryServicesByTransactionId(transactionId);
                TransactionHistoryServicesAdapter adapter = new TransactionHistoryServicesAdapter(getActivity(), transactionHistoryServices);
                listView.setAdapter(adapter);
                subtotal.setText(String.format(Locale.getDefault(), "%1$s %2$.2f",
                        getString(R.string.service_price_currency), transactionHistory.getServicesSubTotal(transactionHistoryServices)));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resendReceiptTransactionHistoryBtn:
                // Resend receipt
                doSubmission(getString(R.string.confirmation_resend_receipt));

                break;

            case R.id.backTransactionHistoryBtn:
                // Back to transaction history page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new TransactionHistory_Fragment(),
                                Utils.TransactionHistory_Fragment)
                        .commit();
                break;
        }
    }

    private void doSubmission(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.confirmation))
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void resendReceipt() {
        try {
            String url = String.format("%1$s%2$s%3$s?id=%4$s",
                    Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_RESEND_RECEIPT,
                    transactionId);

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
                resendReceipt();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (isNetworkConnected == Boolean.TRUE) {
                if (status == Boolean.TRUE) {
                    multiple_actions_left.collapse();
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_resend_receipt));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_resend_receipt));
                }
            } else {
                new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
            }
        }
    }
}
