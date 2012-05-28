package we.should;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import we.should.list.Category;
import we.should.list.Item;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
			mItem = cat.getItem(index);
		}
		if(mItem == null) throw new IllegalStateException("Index points to an item that doesn't exist!");
		
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
//		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
//		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
//		String email = "";
//		for (Account account : accounts) {
//		    if (emailPattern.matcher(account.name).matches()) {
//		        email = account.name;
//		        break;
//		    }
//		}
		
		SharedPreferences settings = getSharedPreferences(WeShouldActivity.PREFS, 0);
		String email = settings.getString(WeShouldActivity.ACCOUNT_NAME, "");
		
		Log.v("REFER EMAILS", emails);
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		//HttpPost httppost = new HttpPost("http://23.23.237.174/refer");

		try {
		    // Add your data
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("user_email", email));
		    nameValuePairs.add(new BasicNameValuePair("email_list", emails));
		    
//		    List<Field> fields = item.getFields();
//		    String itemdata = "";
//		    for(Field f: fields){
//		    	itemdata+=f.getName()+": "+item.get(f)+",";
//		    }
		    	
		    //Log.v("REFERRAL DATA", itemdata);
		    nameValuePairs.add(new BasicNameValuePair("item_data", mItem.dataToDB().toString()));
		    nameValuePairs.add(new BasicNameValuePair("item_name", mItem.getName()));
		    //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    
		    String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		    
		    HttpGet httpget = new HttpGet("http://23.23.237.174/refer?"+paramString);

		    
		    
		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httpget);
		    
		    System.out.println(response.toString());

		} catch (ClientProtocolException e) {
			Log.e("ReferDialog",e.getMessage());
		} catch (IOException e) {
			Log.e("ReferDialog",e.getMessage());
		}
	}
	
}
