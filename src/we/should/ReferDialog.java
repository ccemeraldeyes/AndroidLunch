package we.should;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ReferDialog extends Activity {
	
	/** The emails. **/
	private EditText mEmails;
	
	/** The send button. **/
	private Button mSend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refer);
		
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
