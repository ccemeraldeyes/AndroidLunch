package we.should.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.R;
import we.should.WeShouldActivity;
import we.should.database.WSdb;
import we.should.list.Category;
import we.should.list.Item;
import we.should.list.ReferralItem;
import we.should.list.Referrals;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
		final List<Referral> list = new ArrayList<Referral>();
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		String dataAsString = bundle.getString("we.should.communication.data");
		
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
		Log.v("REFERRAL LIST", list.toString());
		mAdapter = new ReferralAdapter(this, list);
		lv.setAdapter(mAdapter);
		
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				
				List<Referral> approvedList = mAdapter.getApprovedList();
				
				//TODO: send approved/rejected to remote db and delete
				
				Referrals refs = Referrals.getReferralCategory(c);
				
				for(Referral r: approvedList){
					ReferralItem ref = refs.newItem(r.getData());

					try{
						ref.save();
					} catch(Exception e) {
						Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
						return;
					}
					

				}
				
				deleteRefs(list);
				
				finish();
			}
			
		});
	}
	
	public void deleteRefs(List<Referral> referrals){
		
		Log.v("DELETE REFERRALS", "deleting");
		SharedPreferences settings = getSharedPreferences(WeShouldActivity.PREFS, 0);
		
		HttpClient httpclient = new DefaultHttpClient();
		
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", settings.getString(WeShouldActivity.ACCOUNT_NAME, "")));
		
	    JSONArray listArray = new JSONArray();
	    
	    for(Referral r: referrals){
	    	JSONObject o = new JSONObject();
	    	try {
				o.put("name", r.getName());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	try {
				o.put("referred_by", r.getSender());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	listArray.put(o);
	    }
	    
	    nameValuePairs.add(new BasicNameValuePair("delete_list", listArray.toString()));
	    
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		
		HttpGet httpget = new HttpGet("http://23.23.237.174/delete-referrals?"+paramString);
		
		try {
			HttpResponse response = httpclient.execute(httpget);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
