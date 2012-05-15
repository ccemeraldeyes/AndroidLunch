package we.should;

import java.util.ArrayList;
import java.util.List;

import we.should.list.Category;
import we.should.list.Item;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReferDialog extends Activity {
	
	/** The item we're sending. **/
	private Item mItem;
	
	/** The emails. **/
	private EditText mEmails;
	
	/** The send button. **/
	private Button mSend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refer);
		
		Bundle extras = getIntent().getExtras();
		String catName = (String) extras.get(WeShouldActivity.CATEGORY);
		Category cat = Category.getCategory(catName, this);
		
		int index = extras.getInt(WeShouldActivity.INDEX);		
		if (index == -1) {
			mItem = cat.newItem();
		} else {
			mItem = cat.getItems().get(index);
		}
		
		mEmails = (EditText) findViewById(R.id.emails);
		
		mSend = (Button) findViewById(R.id.send);
		mSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				List<String> emails = new ArrayList<String>();
				for (String email : mEmails.getText().toString().split(",")) {
					emails.add(email.trim());
				}
				send(emails);
				finish();
			}
		});
	}
	
	/**
	 * Handles the sending of items.
	 */
	private void send(List<String> emails) {
		// do stuff!
	}
	
}
