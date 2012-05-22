package we.should.communication;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.R;
import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.Item;
import we.should.list.ReferralItem;
import we.should.list.Referrals;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
		
		Bundle bundle = this.getIntent().getExtras();
		String dataAsString = bundle.getString("data");
		
		Log.v("DATA EXTRA", dataAsString);
		
		JSONArray data = new JSONArray();
		//get referral items from extras
		
		try {
			data = new JSONArray(dataAsString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		for(int i=0; i<data.length(); i++){
			try {
				JSONObject o = data.getJSONObject(i);
				Log.v("REFFERAL OBJECT DATA", o.toString());
				
				JSONObject d = new JSONObject(o.getString("data"));
				
				list.add(new Referral(o.getString("item_name"), o.getString("referred_by"), false, d));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v("REFERRAL OBJECT DATA", e.getMessage());
			}
		
		}
		mAdapter = new ReferralAdapter(this, list);
		lv.setAdapter(mAdapter);
		
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// get which items are approved from the referraladapter
				
				List<Referral> approvedList = mAdapter.getApprovedList();
				
				//send approved/rejected to remote db and delete
				
				Referrals refs = Referrals.getReferralCategory(c);
				
				// save them
				for(Referral r: approvedList){
					//get JSON object (store in referral)
					ReferralItem ref = refs.newItem(r.getData());
					ref.save();
					

				}
				
				finish();
			}
			
		});
	}

}
