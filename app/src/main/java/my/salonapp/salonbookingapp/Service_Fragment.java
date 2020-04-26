package my.salonapp.salonbookingapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class Service_Fragment extends Fragment {

    // URL
    private static final String ASPX_GET_ALL_STAFFS = "GetAllSalonServices.aspx";
    private static final String ASPX_GET_ALL_CATEGORIES = "GetAllCategories.aspx";

    // XML nodes for service list
    private static final String XML_NODE_SERVICE_ID = "ServiceID";
    public static final String XML_NODE_SERVICE_NAME = "ServiceName";
    public static final String XML_NODE_CATEGORY_ID = "CategoryID";
    public static final String XML_NODE_CATEGORY_NAME = "CategoryName";
    public static final String XML_NODE_SERVICE_PRICE = "ServicePrice";
    public static final String XML_NODE_SERVICE_DURATION = "ServiceDuration";

    // Control fields
    private View view;
    private Spinner categorySpinner;
    private ListView listView;
    private FloatingActionButton addNewServiceBtn;
    private WaveSwipeRefreshLayout mSwipeRefreshLayout;

    // Variables
    private ServiceDbHelper db;
    private ArrayList<Service> services = new ArrayList<>();
    private Service service;
    private int categoryId;
    private CategoryDbHelper categoryDb;
    private List<Category> categories = new ArrayList<>();

    public Service_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_layout, container, false);

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
        categorySpinner = view.findViewById(R.id.categorySpinner);
        categorySpinner.requestFocus();

        listView = view.findViewById(R.id.card_listView_service);
        addNewServiceBtn = view.findViewById(R.id.addNewServiceBtn);

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
        final ServiceDetail_Fragment fragment = new ServiceDetail_Fragment();
        final Bundle bundle = new Bundle();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                service = services.get(position);

                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Edit);
                bundle.putInt(Utils.Bundle_ID, service.getServiceId());
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.ServiceDetail_Fragment)
                        .commit();
            }
        });

        addNewServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bundle.putSerializable(Utils.Bundle_Mode, Utils.Mode.Insert);
                fragment.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .addToBackStack(getTag())
                        .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                        .replace(R.id.frameContainer, fragment, Utils.ServiceDetail_Fragment)
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
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_add_new_service));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_add_new_service));
                }
            } else if (task == Utils.Task.Update) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_update_service));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_update_service));
                }
            } else if (task == Utils.Task.Delete) {
                if (status == Boolean.TRUE) {
                    new CustomToast().Show_Toast_Success(getActivity(), view, getString(R.string.success_delete_service));
                } else {
                    new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_delete_service));
                }
            }
        }

        refreshServiceListData();
    }

    // Refresh and download all the active services from server
    private void refreshServiceListData() {
        new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // Download all the active services from server
    private void downloadServices() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s?submission=%4$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_STAFFS, "");
        String xml = parser.getXmlFromUrl(url, true, null, null);
        services = new ArrayList<>();

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Service service;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    service = new Service();

                    // Adding each child node to service object
                    service.setServiceId(Integer.parseInt(parser.getValue(e1, XML_NODE_SERVICE_ID)));
                    service.setServiceName(parser.getValue(e1, XML_NODE_SERVICE_NAME));
                    service.setCategoryId(Integer.parseInt(parser.getValue(e1, XML_NODE_CATEGORY_ID)));
                    service.setCategoryName(parser.getValue(e1, XML_NODE_CATEGORY_NAME));
                    service.setServicePrice(Float.parseFloat(parser.getValue(e1, XML_NODE_SERVICE_PRICE)));
                    service.setServiceDuration(Integer.parseInt(parser.getValue(e1, XML_NODE_SERVICE_DURATION)));

                    services.add(service);
                }

                db = new ServiceDbHelper(getContext());
                db.deleteAllServices();
                db.addAllServices(services);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        services = db.getAllServices();
    }

    // Download all the active categories from server
    private void downloadCategories() {
        XMLParser parser = new XMLParser();
        String url = String.format("%1$s%2$s%3$s",
                Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GET_ALL_CATEGORIES);
        String xml = parser.getXmlFromUrl(url, true, null, null);

        try {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName(Utils.XML_NODE_DOWNLOAD);
            Element e = (Element) nl.item(0);
            Boolean status = Boolean.valueOf(parser.getValue(e, Utils.XML_NODE_STATUS));

            if (status == Boolean.TRUE) {
                NodeList nl1 = doc.getElementsByTagName(Utils.XML_NODE_ROW);
                Element e1;
                Category category;

                // Looping through all item nodes <Row>
                for (int i = 0; i < nl1.getLength(); i++) {
                    e1 = (Element) nl1.item(i);

                    category = new Category();

                    // Adding each child node to service object
                    category.setCategoryId(Integer.parseInt(parser.getValue(e1, XML_NODE_CATEGORY_ID)));
                    category.setCategoryName(parser.getValue(e1, XML_NODE_CATEGORY_NAME));

                    categories.add(category);
                }

                categoryDb = new CategoryDbHelper(getContext());
                categoryDb.deleteAllCategories();
                categoryDb.addAllCategories(categories);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSpinnerData() {
        // Add default option to display all category of services
        categories.add(new Category(0, "- All -"));
        //Collections.sort(ArrayList<Category> categories);
        Collections.sort(categories);

        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Attaching data adapter to spinner
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getSelectedItem();
                categoryId = category.getCategoryId();
                updateServiceListData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateServiceListData() {
        if (categoryId == 0) {
            services = db.getAllServices();
        } else {
            services = db.getServicesByCategoryId(categoryId);
        }
        ServicesAdapter adapter = new ServicesAdapter(getActivity(), services);
        listView.setAdapter(adapter);
    }

    class BackgroundTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            if (Utils.isNetworkAvailable(getContext())) {
                downloadServices();
                downloadCategories();
            } else {
                db = new ServiceDbHelper(getContext());
                services = db.getAllServices();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String param) {
            super.onPostExecute(param);
            ServicesAdapter adapter = new ServicesAdapter(getActivity(), services);
            listView.setAdapter(adapter);
            setSpinnerData();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
