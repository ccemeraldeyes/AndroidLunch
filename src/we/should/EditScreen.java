package we.should;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import we.should.list.Category;
import we.should.list.Field;
import we.should.list.Item;
import android.accounts.Account;
import android.accounts.AccountManager;
import we.should.search.DetailPlace;
import we.should.search.Place;
import we.should.search.PlaceRequest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Patterns;

import android.text.Editable;
import android.text.TextWatcher;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditScreen extends Activity {
	
	/** The item that we're editing. **/
	private Item mItem;
	
	/** The name of the item. **/
	private AutoCompleteTextView mName;
	
	/** The list of fields. **/
	private EditAdapter mFieldListView;
	
	/** The data associated with each field. **/
	private Map<Field, String> mData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_item);
		
		Bundle extras = getIntent().getExtras();
		String catName = (String) extras.get(WeShouldActivity.CATEGORY);
		Category cat = Category.getCategory(catName, this);
		
		int index = extras.getInt(WeShouldActivity.INDEX);		
		if (index == -1) {
			mItem = cat.newItem();
		} else {
			mItem = cat.getItems().get(index);
		}
		
		TextView catDisplay = (TextView) findViewById(R.id.category);
		catDisplay.setText(catName);
		
		mData = new HashMap<Field, String>();
		for (Field f : cat.getFields()) {
			mData.put(f, mItem.get(f));
		}
		mData.remove(Field.NAME);
		
		mName = (AutoCompleteTextView) findViewById(R.id.name);
		mName.setText(mItem.getName());
		mName.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				if (s.length() == (mName.getThreshold() - 1)) {
					setupList();
				}
			}

			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			
		});
		mName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				fillFields((Place) mName.getAdapter().getItem(position));
			}

			public void onNothingSelected(AdapterView<?> parent) {}
			
		});
		
		ListView lv = (ListView) findViewById(R.id.fieldList);
		List<Field> fields = new ArrayList<Field>(mItem.getCategory().getFields());
		fields.remove(Field.NAME);
		mFieldListView = new EditAdapter(this, fields, mData);
		lv.setAdapter(mFieldListView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save:
			save();
		}
		return true;
	}
	
	/**
	 * Set up the autocomplete list.
	 */
	private void setupList() {
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		List<Place> places;
		try {
			places = (new PlaceRequest()).searchByLocation(location, mName.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		mName.setAdapter(new PlaceAdapter(this, places));
	}
	
	/**
	 * Fills in any fields from the selected place.
	 */
	private void fillFields(Place place) {
		DetailPlace detailPlace = (new PlaceRequest()).searchPlaceDetail(place.getReference());
		Map<Field, String> fields = new HashMap<Field, String>();
		fields.put(Field.PHONENUMBER, detailPlace.getLocalPhoneNumber());
		fields.put(Field.WEBSITE, detailPlace.getWebSite());
		fields.put(Field.ADDRESS, detailPlace.getAddress());
		mFieldListView.setFields(fields);
	}
	
	/**
	 * Save the data then exit.
	 */
	private void save() {
		for (Field f : mData.keySet()) {
			mItem.set(f, mData.get(f));
		}
		
		if (mItem.getName() == null || mItem.getName().equals("")) {
			Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mItem.set(Field.NAME, mName.getText().toString());
		mItem.save();
		
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
		HttpPost httppost = new HttpPost("http://23.23.237.174/save-item");

		try {
		    // Add your data
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("user_email", email));
			for (Field f : mData.keySet()) {
				nameValuePairs.add(new BasicNameValuePair(f.getName(), mData.get(f)));
			}
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		    // Execute HTTP Post Request
		    HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
		
		
		finish();
	}
	
}
