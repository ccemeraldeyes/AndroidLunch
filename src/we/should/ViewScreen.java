package we.should;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewScreen extends Activity {
	
	/** The call button. **/
	private Button mCall;
	
	/** The edit button. **/
	private Button mEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);   
        
        mCall = (Button) findViewById(R.id.call);
        
        mEdit = (Button) findViewById(R.id.edit);
        mEdit.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(getApplicationContext(), EditScreen.class);
        		startActivity(intent);
        	}
        });
    }
	
}
