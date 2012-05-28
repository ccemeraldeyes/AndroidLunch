package we.should;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import we.should.FieldAdapter.ProtoField;
import we.should.list.Category;
import we.should.list.Field;
import we.should.list.FieldType;
import we.should.list.GenericCategory;
import we.should.list.Tag;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class NewCategory extends Activity {
	
	/** The EditText with the name of the new category. **/
	private EditText mName;
	
	/** The Spinner associated with the category color. **/
	private Spinner mColor;
	
	/** The mappable checkbox. **/
	private CheckBox mMappable;
	
	/** The phoneable checkbox. **/
	private CheckBox mPhoneable;
	
	/** The URL Checkbox **/
	private CheckBox mUrl;
	 
	/** The array adapter that backs the list view. **/
	private FieldAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_category);
        
        mName = (EditText) findViewById(R.id.name);
        
        mColor = (Spinner) findViewById(R.id.color);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_item,
				new ArrayList<String>(Color.getColors()));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mColor.setAdapter(adapter);
        
        mMappable = (CheckBox) findViewById(R.id.mappable);
        
        mPhoneable = (CheckBox) findViewById(R.id.phoneable);
        
        mUrl = (CheckBox) findViewById(R.id.url);
        
        mAdapter = new FieldAdapter(this, new ArrayList<ProtoField>());
        ListView lv = (ListView) findViewById(R.id.fieldList);
        lv.setAdapter(mAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_cat_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			Intent intent = new Intent(this, HelpScreen.class);
			intent.putExtra(WeShouldActivity.HELP_TEXT, R.string.help_add);
			startActivity(intent);
			break;
		case R.id.save:
			save();
			break;
		case R.id.add:
			mAdapter.add(new ProtoField());
			break;
		}
		return true;
	}

	/**
	 * Saves the given category.
	 */
	private void save() {
		List<ProtoField> protoFields = mAdapter.getAll();
		Set<String> names = new HashSet<String>();
		
		int duration = Toast.LENGTH_LONG;
		boolean saveable = true;
		for (ProtoField pf : protoFields) {
			if (names.contains(pf.name)) {
				saveable = false;
				Toast.makeText(this, "Duplicate name: " + pf.name, duration).show();
			}
			if (pf.name.equals("")) {
				saveable = false;
				Toast.makeText(this, "All fields must be named.", duration).show();
			}
			if (Field.getReservedNames().contains(pf.name.toLowerCase())) {
				saveable = false;
				Toast.makeText(this, "Field name " + pf.name + " is reserved.", duration).show();
			}
			names.add(pf.name);
		}
		
		if (Category.getCategory(mName.getText().toString(), this) != null) {
			Toast.makeText(this, "There is already a category called "
					+ mName.getText().toString(), duration).show();
			saveable = false;
			
		}
		
		if (saveable) {
			List<Field> fields = new ArrayList<Field>();
			fields.add(Field.NAME);
			if (mUrl.isChecked()) {
				fields.add(Field.WEBSITE);
			}
			if (mPhoneable.isChecked()) {
				fields.add(Field.PHONENUMBER);
			}
			if (mMappable.isChecked()) {
				fields.add(Field.ADDRESS);
			}
			
			for (ProtoField pf : protoFields) {
				fields.add(new Field(pf.name, FieldType.get(pf.type)));
			}
			fields.add(Field.COMMENT);
			//fields.add(Field.TAGS);
			try{
				Color color = Color.get((String) mColor.getSelectedItem());
				Category cat = new GenericCategory(mName.getText().toString(), fields, this);
				cat.setColor(color);
				cat.save();
			} catch(IllegalArgumentException e) {
				//Toast
				
			}
			finish();
		} else {
			findViewById(R.id.fullLayout).invalidate();
			return;
		}
	}
	
}
