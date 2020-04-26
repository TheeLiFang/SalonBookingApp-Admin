package my.salonapp.salonbookingapp;

import android.content.res.ColorStateList;
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

import my.salonapp.salonbookingapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassword_Fragment extends Fragment implements OnClickListener {

	// URL
	private static final String ASPX_CHECK_COMPANY_EMAIL_EXISTENCE = "CheckCompanyEmailExistence.aspx";
	private static final String ASPX_GENERATE_NEW_PASSWORD = "GenerateCompanyNewPassword.aspx";

	// XML nodes for get email existence
	private static final String XML_NODE_EMAIL_REGISTERED = "RegisteredYN";

	// Control fields
	private static View view;
	private static EditText emailId;
	private static TextView submit, back;

	// Variables
	private Utils.Task task;
	private Boolean status, isNetworkConnected;
	private Boolean registeredYN;
	private String email;

	public ForgotPassword_Fragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.forgotpassword_layout, container, false);
		task = null;

		initViews();
		setListeners();
		showRedirectMessage();

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	// Initialize Views
	private void initViews() {
		emailId = view.findViewById(R.id.registered_emailid);
		submit = view.findViewById(R.id.forgot_button);
		back = view.findViewById(R.id.backToLoginBtn);

		// Setting text selector over text views
		XmlPullParser xrp = getResources().getXml(R.drawable.text_selector);
		try {
			ColorStateList csl = ColorStateList.createFromXml(getResources(),
					xrp, null);

			back.setTextColor(csl);
			submit.setTextColor(csl);

		} catch (Exception e) {
		}
	}

	private void setListeners() {
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.backToLoginBtn:
				// Back to login page
				new MainActivity().replaceLoginFragment();
				break;

			case R.id.forgot_button:
				// Generate email
				task = Utils.Task.SendEmail;
				email = emailId.getText().toString();

				// Call checkValidation method
				if (checkValidation() == Boolean.TRUE) {
					new BackgroundTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
							Utils.Task.SendEmail.toString());
				}
				break;

		}

	}

	private void showRedirectMessage() {
		Bundle bundle = this.getArguments();

		if (bundle != null) {
			Utils.Task task = (Utils.Task) bundle.getSerializable(Utils.Bundle_Task);
			Boolean status = bundle.getBoolean(Utils.Bundle_Status);

			if (task == Utils.Task.SendEmail) {
				if (status == Boolean.FALSE) {
					new CustomToast().Show_Toast(getActivity(), view, getString(R.string.email_id_not_registered));
				}
			}
		}
	}


	// Check Validation Method
	private Boolean checkValidation() {
		Boolean validate = Boolean.TRUE;
		String getEmailId = emailId.getText().toString();

		// Pattern for email id validation
		Pattern p = Pattern.compile(Utils.regEx);

		// Match the pattern
		Matcher m = p.matcher(getEmailId);

		// First check if email id is not null else show error toast
		if (getEmailId.equals("") || getEmailId.length() == 0) {
			validate = Boolean.FALSE;
			new CustomToast().Show_Toast(getActivity(), view,  getString(R.string.enter_email_id));
		}
		// Check if email id is valid or not
		else if (!m.find()) {
			validate = Boolean.FALSE;
			new CustomToast().Show_Toast(getActivity(), view,getString(R.string.email_id_invalid));
		}

		return validate;
	}

	private void generateNewPassword() {
		try {
			String url = String.format("%1$s%2$s%3$s?email=%4$s",
					Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_GENERATE_NEW_PASSWORD,
					email);

			XMLParser parser = new XMLParser();
			status = parser.httpParamSubmission(url);
		} catch (Exception e) {
			status = Boolean.FALSE;
			e.printStackTrace();
			Log.e("Error: ", e.getMessage());
		}
	}

	private void checkEmailRegisteredYN(String getEmailId) {
		try {
			String url = String.format("%1$s%2$s%3$s?email=%4$s",
					Utils.SERVER_URL, Utils.SERVER_FOLDER, ASPX_CHECK_COMPANY_EMAIL_EXISTENCE,
					getEmailId);

			httpParamSubmission(url);
		} catch (Exception e) {
			status = Boolean.FALSE;
			e.printStackTrace();
			Log.e("Error: ", e.getMessage());
		}
	}

	public void httpParamSubmission(String url) {
		Boolean status = Boolean.FALSE;

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

				if(status == Boolean.TRUE) {
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
				if (param[0].equals(Utils.Task.SendEmail.toString())) {
					checkEmailRegisteredYN(email);
					if (registeredYN == Boolean.TRUE) {
						generateNewPassword();
					}
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);

			if (isNetworkConnected == Boolean.TRUE && registeredYN == Boolean.TRUE) {
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.Bundle_Task, task);
				bundle.putBoolean(Utils.Bundle_Status, status);

				Login_Fragment fragment = new Login_Fragment();
				fragment.setArguments(bundle);

				getActivity().getSupportFragmentManager()
						.beginTransaction()
						.setCustomAnimations(R.anim.left_enter, R.anim.right_out)
						.replace(R.id.frameContainer, fragment, Utils.Login_Fragment)
						.commit();
			} else if (isNetworkConnected == Boolean.TRUE && registeredYN == Boolean.FALSE) {
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.Bundle_Task, task);
				bundle.putBoolean(Utils.Bundle_Status, Boolean.FALSE);

				ForgotPassword_Fragment fragment = new ForgotPassword_Fragment();
				fragment.setArguments(bundle);

				getActivity().getSupportFragmentManager()
						.beginTransaction()
						.replace(R.id.frameContainer, fragment, Utils.ForgotPassword_Fragment)
						.commit();
			} else {
				new CustomToast().Show_Toast(getActivity(), view, getString(R.string.error_no_network));
			}
		}
	}
}