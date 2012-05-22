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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class NewCategory extends Activity {
	
	/** The EditText with the name of the new category. **/
	private EditText mName;
	
	/** The mappable checkbox. **/
	private CheckBox mMappable;
	
	/** The phoneable checkbox. **/
	private CheckBox mPhoneable;
	
	/** The array adapter that backs the list view. **/
	private FieldAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_category);
        
        mName = (EditText) findViewById(R.id.name);
        
        mMappable = (CheckBox) findViewById(R.id.mappable);
        
        mPhoneable = (CheckBox) findViewById(R.id.phoneable);
        
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
			if (Field.getReservedNames().contains(pf.name.toLowerCase())) {
				saveable = false;
				Toast.makeText(this, "Field name " + pf.name + " is reserved.", duration).show();
			}
		}
		
		if (Category.getCategory(mName.getText().toString(), this) != null) {
			Toast.makeText(this, "There is already a category called "
					+ mName.getText().toString(), duration).show();
			saveable = false;
			
		}
		
		if (saveable) {
			List<Field> fields = new ArrayList<Field>();
			fields.add(Field.NAME);
			if (mMappable.isChecked()) {
				fields.add(Field.ADDRESS);
			}
			if (mPhoneable.isChecked()) {
				fields.add(Field.PHONENUMBER);
			}
			for (ProtoField pf : protoFields) {
				fields.add(new Field(pf.name, FieldType.get(pf.type)));
			}
			fields.add(Field.COMMENT);
			try{
				(new GenericCategory(mName.getText().toString(), fields, this)).save();
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
