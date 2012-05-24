package we.should;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import we.should.list.Category;
import we.should.list.Field;
import we.should.list.Item;
import we.should.list.Tag;
import we.should.search.DetailPlace;
import we.should.search.Place;
import we.should.search.PlaceRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EditScreen extends Activity {
	/** Result code for item deletion **/
	public static final int DELETE = 2;

	/** The item that we're editing. **/
	private Item mItem;
	
	/** The name of the item. **/
	private AutoCompleteTextView mName;
	
	/** The list of fields. **/
	private List<Field> mFields;
	
	/** The listview holding the fields. **/
	private ListView mFieldListView;
	
	/** A display of all of this item's tags. **/
	private TextView mTagsView;
	
	/** Delete Item button **/
	private Button mDelete;
	
	/** The data associated with each field. **/
	private Map<Field, String> mData;
	
	/** Which tags are set to true. **/
	private Set<Tag> mTags;
	
	/** All of the tags that have been added by addTag, plus tags from
	 * before. **/
	private List<Tag> mAllTags;
	
	/** Async location lookup **/
	private AsyncTask<String, Void, List<Place>> mLookup;

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
			mItem = cat.getItem(index);
		}
		if (mItem == null) throw new IllegalStateException("Index points to non-existent item!");
		
		TextView catDisplay = (TextView) findViewById(R.id.category);
		catDisplay.setText(catName);
		
		mData = new HashMap<Field, String>();
		for (Field f : mItem.getFields()) {
			mData.put(f, mItem.get(f));
		}
		mData.remove(Field.NAME);
		
		mName = (AutoCompleteTextView) findViewById(R.id.name);
		mName.setText(mItem.getName());
		mName.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {}

			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//int orig = s.length() - count + before;
				if ((s.length() >= mName.getThreshold())){ //&& orig < mName.getThreshold()) {
					setupList(s.toString());
				}
			}
			
		});
		mName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				PlaceAdapter pa = (PlaceAdapter) mName.getAdapter();
				fillFields((Place) pa.getItem(position));
			}
			
		});
		
		mFieldListView = (ListView) findViewById(R.id.fieldList);
		mFields = new ArrayList<Field>(mItem.getFields());
		mFields.remove(Field.NAME);
		mFieldListView.setAdapter(new EditAdapter(this, mFields, mData));
		
		TextView tagsText = (TextView) findViewById(R.id.tags_text);
		tagsText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				modifyTags();
			}
		});

		mTags = new HashSet<Tag>(mItem.getTags());
		mTagsView = (TextView) findViewById(R.id.tags);
		mTagsView.setText(Tag.getFormatted(mTags));
		mTagsView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				modifyTags();
			}
		});
		
		mDelete = (Button) findViewById(R.id.deleteItem);
		if (!mItem.isAdded()) mDelete.setVisibility(View.GONE);
		else mDelete.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				delete();
			}
		});
		
		mAllTags = new ArrayList<Tag>(Tag.getTags(this));
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
		case R.id.help:
			Intent intent = new Intent(this, HelpScreen.class);
			intent.putExtra(WeShouldActivity.HELP_TEXT, R.string.help_edit);
			startActivity(intent);
			break;
		case R.id.save:
			save();	
			break;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (ActivityKey.get(requestCode)) {
		case SET_TAGS:
			if (resultCode == RESULT_OK || resultCode == RESULT_FIRST_USER) {
				mTags = (Set<Tag>) data.getSerializableExtra(WeShouldActivity.TAGS);
				mTagsView.setText(Tag.getFormatted(mTags));
			}
			if (resultCode == RESULT_FIRST_USER) {
				save();
			}
			break;
		}
	}
	
	/**
	 * Asks the user whether they want to add a new tag or edit the current
	 * ones.
	 */
	private void modifyTags() {
		final CharSequence[] items = {"Create new tag", "Set tags"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	// Yes, I know this is brittle.  Sorry.
		        if (item == 0) {
		        	addTag();
		        } else {
		        	setTags();
		        }
		    }
		});
		builder.show();
	}
	
	/**
	 * Creates a new tag and adds it to this item.
	 */
	private void addTag() {
		final Set<String> tagNames = new HashSet<String>();
		for (Tag t : Tag.getTags(this)) {
			tagNames.add(t.toString());
		}
		AlertDialog.Builder builder;

		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.add_tag, null);

		final EditText name = (EditText) layout.findViewById(R.id.name);
		final Spinner color = (Spinner) layout.findViewById(R.id.color);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item,
				new ArrayList<String>(Tag.getAllTagColors().keySet()));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		color.setAdapter(adapter);

		builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle("Add a new tag");
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String nameStr = name.getText().toString();
				if (tagNames.contains(nameStr)) {
					Toast.makeText(EditScreen.this,
							"A tag with this name already exists",
							Toast.LENGTH_LONG).show();
					addTag();
					return;
				}
				String colorStr = (String) color.getSelectedItem();
				mAllTags.add(0, new Tag(0, nameStr, colorStr));
				mTags.add(new Tag(0, name.getText().toString(), colorStr));
				mTagsView.setText(Tag.getFormatted(mTags));
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}
	
	/**
	 * Pulls up the set tags menu.
	 */
	private void setTags() {
		// Set up the tag names
		final CharSequence[] tagNames = new CharSequence[mAllTags.size()];
		int i = 0;
		for (Tag t : mAllTags) {
			tagNames[i] = t.toString();
			i++;
		}
		
		// Set up our storage for which tags the user has selected
		final Set<Tag> selectedTags = new HashSet<Tag>(mTags);
		boolean[] checkedItems = new boolean[tagNames.length];
		for (Tag t : mTags) {
			checkedItems[mAllTags.indexOf(t)] = true;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Set Tags");
		builder.setMultiChoiceItems(tagNames, checkedItems,
				new DialogInterface.OnMultiChoiceClickListener() {
			public void onClick(DialogInterface dialog, int which,
					boolean checked) {
				if (checked) {
					selectedTags.add(mAllTags.get(which));
				} else {
					selectedTags.remove(mAllTags.get(which));
				}
			}
		});
		
		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mTags = selectedTags;
				mTagsView.setText(Tag.getFormatted(mTags));
			}
		});
		
		// We don't need to do anything special on cancel
		builder.setNegativeButton("Cancel", null);
		builder.show();
	}
	
	/**
	 * Set up the autocomplete list.
	 */
	private void setupList(String constraint) {
		if (!mItem.getFields().contains(Field.ADDRESS)) {
			return;
		}
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if(mLookup != null) {
			if(!mLookup.getStatus().equals(Status.FINISHED)) {
				mLookup.cancel(true);
			}
		} 
		mLookup = new DoSuggestionLookup(locationManager, this);
		mLookup.execute(constraint);

	}
	
	/**
	 * Fills in any fields from the selected place.
	 */
	private void fillFields(Place place) {
		DetailPlace detailPlace = (new PlaceRequest()).searchPlaceDetail(place.getReference());
		Map<Field, String> fieldMap = detailPlace.asFieldMap();
		for (Field f : fieldMap.keySet()) {
			if (fieldMap.get(f) != null) {
				mData.put(f, fieldMap.get(f));
			}
		}

		mFieldListView.setAdapter(new EditAdapter(this, mFields, mData));
	}
	
	/**
	 * Save the data then exit. If there is an error, Toast it and do nothing.
	 */
	private void save() {
		for (Field f : mItem.getFields()) {
			mItem.set(f, mData.get(f));
		}
		mItem.set(Field.NAME, mName.getText().toString());
		
		if (mItem.getName() == null || mItem.getName().equals("")) {
			Toast.makeText(this, "Please enter a name.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		for (Tag t : mTags) {
			mItem.addTag(t.toString(), t.getColor());
		}
		try {
			mItem.save();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}

		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		
		SharedPreferences settings = getSharedPreferences(WeShouldActivity.PREFS, 0);
		

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", settings.getString(WeShouldActivity.ACCOUNT_NAME, "")));	    
	    nameValuePairs.add(new BasicNameValuePair("item", mItem.dataToDB().toString()));
		
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");

		
		HttpGet httpget = new HttpGet("http://23.23.237.174/save-item?"+paramString);

		try {
			

			
			httpclient.execute(httpget);
		    Log.v("GETREFERRALSSERVICE", "backing up items");

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
			Log.v("GETREFERRALSSERVICE", e.getMessage());
		} catch (IOException e) {
		    // TODO Auto-generated catch block
			Log.v("GETREFERRALSSERVICE", e.getMessage());
		}
		finish();
	}
	private void delete() {
		new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.delete_item)
        .setMessage(R.string.delete_item_confirm)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        	
        	public void onClick(DialogInterface dialog, int which) {

                mItem.delete();
                setResult(DELETE);
                finish();    
            }

        })
        .setNegativeButton(R.string.no, null)
        .show();
	}
	private class DoSuggestionLookup extends AsyncTask<String, Void, List<Place>> {

		LocationManager locationManager;
		Context ctx;
		public DoSuggestionLookup(LocationManager lm, Context ctx){
			this.locationManager = lm;
			this.ctx = ctx;
		}
		
		protected List<Place> doInBackground(String... params) {
			List<Place> places = new ArrayList<Place>();
			if (!mItem.getFields().contains(Field.ADDRESS)) {
				return places;
			}
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location == null) {
				return places;
			}
			try {
				if(!isCancelled()) places = (new PlaceRequest()).searchByLocation(location, params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return places;
			}
			return places;
		}
		protected void onPostExecute(List<Place> result){
			if(result != null && !isCancelled()) mName.setAdapter(new PlaceAdapter(ctx, result));
		}
		
	}
}
