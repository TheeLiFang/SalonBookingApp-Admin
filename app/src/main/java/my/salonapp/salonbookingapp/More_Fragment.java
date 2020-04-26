package my.salonapp.salonbookingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import my.salonapp.salonbookingapp.R;

public class More_Fragment extends Fragment {

    // Control fields
    private View view;
    private static Button changePasswordBtn, changeWorkingHourBtn;

    public More_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.more_layout, container, false);

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
        changePasswordBtn = view.findViewById(R.id.changePasswordBtn);
        changeWorkingHourBtn = view.findViewById(R.id.changeWorkingHourBtn);
    }

    // Set Listeners
    private void setListeners() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final ChangePassword_Fragment changePasswordFragment = new ChangePassword_Fragment();
        final WorkingHour_Fragment workingHourFragment = new WorkingHour_Fragment();
        final Bundle bundle = new Bundle();

        // Change to booking detail
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                changePasswordFragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, changePasswordFragment, Utils.ChangePassword_Fragment)
                        .commit();
            }
        });

        // Change to working hour
        changeWorkingHourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                workingHourFragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, workingHourFragment, Utils.WorkingHour_Fragment)
                        .commit();
            }
        });

    }

    private void showRedirectMessage() {
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Utils.Task task = (Utils.Task) bundle.getSerializable(Utils.Bundle_Task);
            Boolean status = bundle.getBoolean(Utils.Bundle_Status);

            if (task == Utils.Task.Submit) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_update_workingHour));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_update_workingHour));
                }
            }
           else if (task == Utils.Task.Update) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_update_password));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_update_password));
                }
            }
        }
    }

}
