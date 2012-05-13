package we.should;

import we.should.list.Category;
import we.should.list.Item;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * ViewScreen is the activity that displays an item and allows the user to call
 * the item if it has a valid phone number.
 * 
 * @author Will
 */

public class ViewScreen extends Activity {
	
	/** The item we're viewing. **/
	private Item mItem;
	
	/** The call button. **/
	private Button mCall;
	
	/** The edit button. **/
	private Button mEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.view_item);
        
        Bundle extras = getIntent().getExtras();
		String catName = (String) extras.get(WeShouldActivity.CATEGORY);
		Category cat = null;
		for (Category c : Category.getCategories(this)) {
			if (c.getName().equals(catName)) {
				cat = c;
				break;
			}
		}
		
		int index = extras.getInt(WeShouldActivity.INDEX);
		mItem = cat.getItems().get(index);
        
        mCall = (Button) findViewById(R.id.call);
        mCall.setEnabled(mItem.getPhoneNo() != null && !mItem.getPhoneNo().equals(""));
        
        mEdit = (Button) findViewById(R.id.edit);
        mEdit.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent(getApplicationContext(), EditScreen.class);
        		startActivity(intent);
        	}
        });
    }
	
}
