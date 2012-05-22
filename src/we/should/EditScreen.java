package we.should;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import we.should.list.Category;
import we.should.list.Field;
import we.should.list.Item;
import we.should.list.Tag;
import we.should.search.DetailPlace;
import we.should.search.Place;
import we.should.search.PlaceRequest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditScreen extends Activity {
	
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
			mItem = cat.getItems().get(index);
		}
		
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
				setTags();
			}
		});

		mTags = new HashSet<Tag>(mItem.getTags());
		mTagsView = (TextView) findViewById(R.id.tags);
		mTagsView.setText(Tag.getFormatted(mTags));
		mTagsView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setTags();
			}
		});
		
		mDelete = (Button) findViewById(R.id.deleteItem);
		mDelete.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				delete();
			}
		});
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
			if (resultCode == RESULT_OK) {
				mTags = (Set<Tag>) data.getSerializableExtra(WeShouldActivity.TAGS);
				mTagsView.setText(Tag.getFormatted(mTags));
			}
			break;
		}
	}
	
	/**
	 * Pulls up the set tags menu.
	 */
	private void setTags() {
		Intent intent = new Intent(EditScreen.this, SetTags.class);
		
		// Why do we have to use .toArray() here?  Because Eclipse sucks.
		intent.putExtra(WeShouldActivity.TAGS, (Serializable) mTags);
		startActivityForResult(intent, ActivityKey.SET_TAGS.ordinal());
	}
	
	/**
	 * Set up the autocomplete list.
	 */
	private void setupList(String constraint) {
		if (!mItem.getFields().contains(Field.ADDRESS)) {
			return;
		}
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if(mLookup != null && !mLookup.getStatus().equals(Status.FINISHED))
			mLookup.cancel(true);
		mLookup = new DoSuggestionLookup(locationManager, this);
		mLookup.execute(constraint);
//		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if (location == null) {
//			return;
//		}
//		try {
//			List<Place> places = (new PlaceRequest()).searchByLocation(location, constraint);
//			mName.setAdapter(new PlaceAdapter(this, places));
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
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
	 * Save the data then exit.
	 */
	private void save() {
		for (Field f : mData.keySet()) {
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
                setResult(Activity.RESULT_CANCELED);
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
			//LocationManager locationManager = (LocationManager) EditScreen.getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location == null) {
				return places;
			}
			try {
				places = (new PlaceRequest()).searchByLocation(location, params[0]);
			} catch (Exception e) {
				e.printStackTrace();
				return places;
			}
			return places;
		}
		protected void onPostExecute(List<Place> result){
			if(result != null) mName.setAdapter(new PlaceAdapter(ctx, result));
		}
		
	}
}
