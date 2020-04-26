package my.salonapp.salonbookingapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class Staff_Fragment extends Fragment {

    // URL
    private static final String ASPX_GET_ALL_STAFFS = "GetAllStaffs.aspx";

    // XML nodes for staff list
    private static final String XML_NODE_STAFF_ID = "StaffID";
    private static final String XML_NODE_STAFF_NAME = "StaffName";
    private static final String XML_NODE_STAFF_EMAIL = "StaffEmail";
    private static final String XML_NODE_STAFF_PHONE = "StaffPhone";

    // Control fields
    private View view;
    private ListView listView;
    private FloatingActionButton addNewStaffBtn;
    private WaveSwipeRefreshLayout mSwipeRefreshLayout;

    // Variables
    private CompanyDbHelper companyDb;
    private StaffDbHelper db;
    private ArrayList<Staff> staffs = new ArrayList<>();
    private Staff staff;

    public Staff_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.staff_layout, container, false);

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
        listView = view.findViewById(R.id.card_listView_staff);
        addNewStaffBtn = view.findViewById(R.id.addNewStaffBtn);

        mSwipeRefreshLayout = view.findViewById(R.id.main_swipe);
        mSwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
        mSwipeRefreshLayout.setWaveColor(ContextCompat.getColor(getContext(), R.color.violet));
        mSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                }, 1500);
            }
        });
    }

    private void setListeners() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final StaffDetail_Fragment fragment = new StaffDetail_Fragment();
        final Bundle bundle = new Bundle();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                staff = staffs.get(position);

                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Edit);
                bundle.putInt(Utils.Bundle_ID, staff.getStaffId());
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.StaffDetail_Fragment)
                        .commit();
            }
        });

        addNewStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.StaffDetail_Fragment)
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
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_add_new_staff));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_add_new_staff));
                }
            } else if (task == Utils.Task.Update) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_update_staff));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_update_staff));
                }
            } else if (task == Utils.Task.Delete) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_delete_staff));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_delete_staff));
                }
            }
        }

        refreshStaffListData();
    }

    // Refresh and download all the active staffs from server
    private void refreshStaffListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active staffs from server
    private void downloadStaffs() {
        XMLParser parser = new XMLParser();
        companyDb = new CompanyDbHelper(getContext());
        String url = String.format("%1$s%2$s%3$s?id=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_STAFFS, companyDb.getCompanyId());
        String xml = parser.getXmlFromUrl(url, true, null, null);
        staffs = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Staff staff;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    staff = new Staff();

                    // Adding each child node to staff object
                    staff.setStaffId(Integer.parseInt(parser.getValue(e1, XML_NODE_STAFF_ID)));
                    staff.setStaffName(parser.getValue(e1, XML_NODE_STAFF_NAME));
                    staff.setStaffEmail(parser.getValue(e1, XML_NODE_STAFF_EMAIL));
                    staff.setStaffPhone(parser.getValue(e1, XML_NODE_STAFF_PHONE));

                    staffs.add(staff);
                }

                db = new StaffDbHelper(getContext());
                db.deleteAllStaffs();
                db.addAllStaffs(staffs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        staffs = db.getAllStaffs();
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            if (Utils.isNetworkAvailable(getContext())) {
                downloadStaffs();
            } else {
                db = new StaffDbHelper(getContext());
                staffs = db.getAllStaffs();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            StaffsAdapter adapter = new StaffsAdapter(getActivity(), staffs);
            listView.setAdapter(adapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
