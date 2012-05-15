package we.should;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import we.should.list.Category;
import we.should.list.Field;
import we.should.list.Item;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ViewScreen is the activity that displays an item and allows the user to call
 * the item if it has a valid phone number.
 * 
 * @author Will
 */

public class ViewScreen extends Activity {
	
	/** Intent key. **/
	private static final int REFER = 0;
	
	/** The category of the item we're viewing. **/
	private Category mCategory;
	
	/** The index of the item we're viewing. **/
	private int mIndex;
	
	/** The item we're viewing. **/
	private Item mItem;
	
	/** The call button. **/
	private Button mCall;
	
	/** The list of fields. **/
	private ListView mFieldListView;
	
	/** The data to display. **/
	private Map<Field, String> mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);

        Bundle extras = getIntent().getExtras();
		final String catName = (String) extras.get(WeShouldActivity.CATEGORY);
		mCategory = Category.getCategory(catName, this);
		
		((TextView) findViewById(R.id.category)).setText(catName);
		
		mIndex = extras.getInt(WeShouldActivity.INDEX);
		mItem = mCategory.getItems().get(mIndex);

        mCall = (Button) findViewById(R.id.call);
        update();
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		update();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.view_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refer:
    		Intent intent = new Intent(getApplicationContext(), ReferDialog.class);
    		startActivityForResult(intent, REFER);
			break;
		case R.id.edit:
    		intent = new Intent(getApplicationContext(), EditScreen.class);
    		intent.putExtra(WeShouldActivity.CATEGORY, mCategory.getName());
    		intent.putExtra(WeShouldActivity.INDEX, mIndex);
    		startActivityForResult(intent, ActivityKey.EDIT_ITEM.ordinal());
			break;
		}
		return true;
	}
	
	/**
	 * Updates the information displayed by this ViewScreen.
	 */
	private void update() {
		Category cat = Category.getCategory(mItem.getCategory().getName(), this);
		mItem = cat.getItems().get(mIndex);
		
		((TextView) findViewById(R.id.name)).setText(mItem.getName());
		
        if (mItem.getPhoneNo() == null || mItem.getPhoneNo().equals("")) {
        	mCall.setVisibility(View.GONE);
        }
        
        mData = new HashMap<Field, String>();
        for (Field f : mItem.getCategory().getFields()) {
        	mData.put(f, mItem.get(f));
        }
        mData.remove(Field.NAME);
		
		mFieldListView = (ListView) findViewById(R.id.fieldList);
		List<Field> fields = new ArrayList<Field>(mItem.getCategory().getFields());
		fields.remove(Field.NAME);
		mFieldListView.setAdapter(new ViewAdapter(this, fields, mData));
	}
}
