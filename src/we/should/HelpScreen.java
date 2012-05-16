package we.should;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HelpScreen extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.help);
	    
	    Bundle extras = getIntent().getExtras();
	    int helpText = extras.getInt(WeShouldActivity.HELP_TEXT);
	    
	    TextView tv = (TextView) findViewById(R.id.help);
	    tv.setText(helpText);
    }
	
}
