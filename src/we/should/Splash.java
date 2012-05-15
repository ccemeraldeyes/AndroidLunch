package we.should;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Splash extends Activity {
	
	/** The list of accounts. **/
	private List<String> mAccounts;

	/** The accounts spinner. **/
	private Spinner mAccountsSpinner;
	
	/** The log in button. **/
	private Button mLogin;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		checkLoggedIn();
		
		mAccountsSpinner = (Spinner) findViewById(R.id.accounts);
		setupSpinner();
		
		mLogin = (Button) findViewById(R.id.login);
		mLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				logIn();
			}
			
		});
	}
	
	/**
	 * Check to see if the user is already logged in, and if so go straight to
	 * the main activity.
	 */
	private void checkLoggedIn() {
		SharedPreferences settings = getSharedPreferences(WeShouldActivity.PREFS, 0);
		String accountName = settings.getString(WeShouldActivity.ACCOUNT_NAME, null);
		if (accountName != null) {
			Intent openStartingPoint = new Intent("we.should.MAIN");
			startActivity(openStartingPoint);
		}
	}
	
	/**
	 * Log in.
	 */
	private void logIn() {
		SharedPreferences settings = getSharedPreferences(WeShouldActivity.PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(WeShouldActivity.ACCOUNT_NAME, (String) mAccountsSpinner.getSelectedItem());
		editor.commit();
		Intent openStartingPoint = new Intent("we.should.MAIN");
		startActivity(openStartingPoint);
	}
	
	/**
	 * Set up the accounts spinner.
	 */
	private void setupSpinner() {
		// We put them into a set first to get rid of repeats
		Set<String> accountSet = new HashSet<String>();
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = AccountManager.get(this).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				accountSet.add(account.name);
			}
		}
		mAccounts = new ArrayList<String>(accountSet);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mAccounts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mAccountsSpinner.setAdapter(adapter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	
	protected boolean checkReferrals(){
//		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
//		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
//		String emails = "";
//		for (Account account : accounts) {
//		    if (emailPattern.matcher(account.name).matches()) {
//		        emails += account.name+",";
//		    }
//		}
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://23.23.237.174/check-referrals");

		try {
		    // Add your data
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("user_email", WeShouldActivity.ACCOUNT_NAME));
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httppost);
		    
		    //TODO: check for new referrals. maybe just save new items but flag them?
		    //then present them for approval to user
		    //redirect to new page before main

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
		
		return false;
	
	}
	
}
