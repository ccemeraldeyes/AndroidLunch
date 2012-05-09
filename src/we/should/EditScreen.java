package we.should;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class EditScreen extends Activity {
	
	/** The save button. **/
	private Button mSave;
	
	/** The category spinner. **/
	private Spinner mCategories;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_item);
		
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditScreen.this.finish();
			}
		});
		
		mCategories = (Spinner) findViewById(R.id.categories);
		List<String> list = Arrays.asList("Restaurants", "Movies");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
			      android.R.layout.simple_spinner_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCategories.setAdapter(adapter);
	}
	
}
