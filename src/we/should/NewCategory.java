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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
	
	/** The add field button. **/
	private Button mAddField;
	
	/** The save button. **/
	private Button mSave;

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
        
        mAddField = (Button) findViewById(R.id.add);
        mAddField.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				mAdapter.add(new ProtoField());
			}
        	
        });
        
        mSave = (Button) findViewById(R.id.save);
        mSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				NewCategory.this.save();
			}
        	
        });
	}

	/**
	 * Saves the given category.
	 */
	private void save() {
		List<ProtoField> protoFields = mAdapter.getAll();
		Set<String> names = new HashSet<String>();
		
		int duration = Toast.LENGTH_SHORT;
		boolean saveable = true;
		for (ProtoField pf : protoFields) {
			if (names.contains(pf.name)) {
				saveable = false;
				Toast.makeText(this, "Duplicate name: " + pf.name, duration).show();
			}
			names.add(pf.name);
			if (pf.name.equalsIgnoreCase(Field.NAME.getName())) {
				saveable = false;
				Toast.makeText(this, "Cannot have a field named '" + pf.name + "'", duration).show();
			}
			if (pf.name.equalsIgnoreCase(Field.PHONENUMBER.getName())) {
				saveable = false;
				Toast.makeText(this, "Cannot have a field named '" + pf.name + "'", duration).show();
			}
			if (pf.name.equalsIgnoreCase(Field.ADDRESS.getName())) {
				saveable = false;
				Toast.makeText(this, "Cannot have a field named '" + pf.name + "'", duration).show();
			}
		}
		
		if (Category.getCategory(mName.getText().toString(), this) != null) {
			Toast.makeText(this, "There is already a category called "
					+ mName.getText().toString(), duration).show();
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
			(new GenericCategory(mName.getText().toString(), fields, this)).save();
			finish();
		} else {
			findViewById(R.id.fullLayout).invalidate();
			return;
		}
	}
	
}
