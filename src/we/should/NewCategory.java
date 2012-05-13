package we.should;

import java.util.ArrayList;

import we.should.FieldAdapter.ProtoField;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

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
	}
	
}
