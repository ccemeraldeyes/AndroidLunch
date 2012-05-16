package we.should;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import we.should.list.Field;

import android.accounts.Account;
import android.accounts.AccountManager;
import we.should.list.Category;
import we.should.list.Item;
import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReferDialog extends Activity {
	
	/** The item we're sending. **/
	private Item mItem;
	
	/** The emails. **/
	private EditText mEmails;
	
	/** The send button. **/
	private Button mSend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refer);
		
		Bundle extras = getIntent().getExtras();
		String catName = (String) extras.get(WeShouldActivity.CATEGORY);
		Category cat = Category.getCategory(catName, this);
		
		int index = extras.getInt(WeShouldActivity.INDEX);		
		if (index == -1) {
			mItem = cat.newItem();
		} else {
			mItem = cat.getItems().get(index);
		}
		
		mEmails = (EditText) findViewById(R.id.emails);
		
		mSend = (Button) findViewById(R.id.send);
		mSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
//				List<String> emails = new ArrayList<String>();
//				for (String email : mEmails.getText().toString().split(",")) {
//					emails.add(email.trim());
//				}
				send(mEmails.getText().toString(), mItem);
				finish();
			}
		});
	}
	
	/**
	 * Handles the sending of items.
	 * @param item 
	 */
	private void send(String emails, Item item) {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
		String email = "";
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        email = account.name;
		        break;
		    }
		}
		
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://23.23.237.174/refer");

		try {
		    // Add your data
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("user_email", email));
		    nameValuePairs.add(new BasicNameValuePair("email_list", emails));
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    //TODO: get item stuff from db and send that too!

		    
		    
		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
	}
	
}
