package we.should.communication;

import java.util.ArrayList;
import java.util.List;

import we.should.R;
import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.ReferralItem;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ApproveReferral extends Activity {
	
	/** The button to save approved referrals. **/
	private Button mSave;
	
	/** The adapter that handles our referrals. **/
	private ReferralAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		final Context c = this.getApplicationContext(); //I don't know if this is the right way to do this!
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.approve_referral);
		
		ListView lv = (ListView) findViewById(R.id.referralList);
		List<Referral> list = new ArrayList<Referral>();
		
		//get referral items from extras
		list.add(new Referral("The Kraken", "Will", false));
		list.add(new Referral("Reboot", "Troy", false));
		mAdapter = new ReferralAdapter(this, list);
		lv.setAdapter(mAdapter);
		
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// get which items are approved from the referraladapter
				
				List<Referral> approvedList = mAdapter.getApprovedList();
				
				Category cat = Category.getCategory("referral", c); //is this right?
				
				
				WSdb db = new WSdb(c);
				// save them
				for(Referral r: approvedList){
					db.insertItem(r.getName(), 0, null); //get cat and data
				}
				
				finish();
			}
			
		});
	}

}
