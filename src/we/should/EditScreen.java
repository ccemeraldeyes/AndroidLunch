package we.should;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import we.should.list.Category;
import we.should.list.Field;
import we.should.list.Item;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EditScreen extends Activity {
	
	/** The item that we're editing. **/
	private Item mItem;
	
	/** The list of fields. **/
	private ListView mFieldListView;
	
	/** The data associated with each field. **/
	private Map<Field, String> mData;
	
	/** The save button. **/
	private Button mSave;

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
		
		mFieldListView = (ListView) findViewById(R.id.fieldList);
		List<Field> fields = mItem.getCategory().getFields();
		mFieldListView.setAdapter(new EditAdapter(this, fields, mData));
		
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditScreen.this.save();
			}
		});
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
		
		mItem.save();
		finish();
	}
	
}
