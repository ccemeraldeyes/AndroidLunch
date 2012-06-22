package we.should.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import we.should.ActivityKey;
import we.should.R;
import we.should.WeShouldActivity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * GetReferralsService is a background service that queries the remote database checking for referrals
 * and notifies the user if there are any unread referrals.
 * 
 * For our purposes, "unread" means that they have not clicked save on the approve screen
 * 
 * @author colleen
 *
 */

public class GetReferralsService extends IntentService {

	/**
	 * GetReferralsService constructor
	 */
	public GetReferralsService() {
		super("we.should.communication.GetReferralsService");
	}

	/**
	 * Queries the remote database to check for unread referrals. If any are returned, creates a notification
	 * that, once clicked on, will open the approval page.
	 * 
	 * @param intent
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String username = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		
		JSONArray data = getFromDb(username);
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.referrals;
		String tickerText = "New referrals!";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "New referrals!";
		
		Log.v("REFERRAL DATA", data.toString());
		
		if(data.length() >0){
			CharSequence contentText = "You have "+data.length()+" new referrals awaiting your approval.";
			Intent notificationIntent = new Intent(this, ApproveReferral.class);
			
			notificationIntent.putExtra("we.should.communication.data", data.toString());
				
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
			
			
			nm.notify(ActivityKey.NEW_REFERRAL.ordinal(), notification);
		} 
	}
	
	public String buildQueryString(String username){
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", username));
		
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		return paramString;
	}

	
	public JSONArray getFromDb(String username){
		HttpClient httpclient = new DefaultHttpClient();

		String paramString = buildQueryString(username);
		
		HttpGet httpget = new HttpGet("http://23.21.136.252/check-referrals?"+paramString);
		
		JSONObject resp = new JSONObject();
		JSONArray data = new JSONArray();
		
		try {
			
			HttpResponse response = httpclient.execute(httpget);
			
			InputStream is = response.getEntity().getContent();
			

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			int i=0;
			while(i != -1){
				i = is.read();
				baos.write(i);
			}			
			
			byte[] buf = baos.toByteArray(); 
			
			Log.v("REFERRAL RESPONSE", new String(buf));

			resp = new JSONObject(new String(buf));

			data = resp.getJSONArray("referrals"); 
		    
		    Log.v("GETREFERRALSSERVICE", "Checking for new referrals");
		    

		} catch (ClientProtocolException e) {
			Log.e("GETREFERRALSSERVICE", e.getMessage());
		} catch (IOException e) {
			Log.e("GETREFERRALSSERVICE", e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("GET REFFERAL SERVICE", "JSON EXCEPTION "+e.getMessage());
		}
		
		return data;
	}
	
	public static String getUrl(){
		return "check-referrals";
	}
	
}
