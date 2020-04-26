package my.salonapp.salonbookingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import java.util.Locale;

public class PaymentSuccessful_Fragment extends Fragment implements OnClickListener {

    // Control fields
    private View view;
    private TextView bookingId;
    private static FloatingActionButton backPaymentBtn;

    public PaymentSuccessful_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.paymentsuccessful_layout, container, false);

        initViews();
        setListeners();

        return view;
    }

    // Initialize all views
    private void initViews() {
        bookingId = view.findViewById(R.id.bookingId);
        backPaymentBtn = view.findViewById(R.id.backPaymentBtn);

        Bundle bundle = this.getArguments();

        if(bundle != null){
            bookingId.setText(String.format(Locale.getDefault(), "%1$s%2$s", "Your booking ID is : ", bundle.getString("refNo")));
        }
    }

    // Set Listeners
    private void setListeners() {
        backPaymentBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backPaymentBtn:
                // Back to service page
                FragmentManager fragment = getActivity().getSupportFragmentManager();
                fragment
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                        .replace(R.id.frameContainer, new Home_Fragment(),
                                Utils.Home_Fragment)
                        .commit();
                break;
        }
    }
}
