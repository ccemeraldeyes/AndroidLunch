package we.should.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

public class GetReferralsService extends IntentService {

	public GetReferralsService() {
		super("we.should.communication.GetReferralsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String username = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
//		HttpPost httppost = new HttpPost("http://23.23.237.174/check-referrals");

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", username));
		
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		
		HttpGet httpget = new HttpGet("http://23.23.237.174/check-referrals?"+paramString);
		
		try {
		    // Add your data
//		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//		    nameValuePairs.add(new BasicNameValuePair("user_email", WeShouldActivity.ACCOUNT_NAME));
//		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//		    // Execute HTTP Post Request
//		    HttpResponse response = httpclient.execute(httppost);
		    //response.getEntity().
			
			HttpResponse response = httpclient.execute(httpget);
			
			InputStream is = response.getEntity().getContent();
			
			byte[] buf = new byte[4096];
			is.read(buf);

			JSONObject resp = new JSONObject(new String(buf));
			
			Log.v("REFERRAL RESPONSE", new String(buf));
		    
		    Log.v("GETREFERRALSSERVICE", "Checking for new referrals");
		    
		    //TODO: check for new referrals. maybe just save new items but flag them?
		    //then present them for approval to user
		    //redirect to new page before main

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
			Log.v("GETREFERRALSSERVICE", e.getMessage());
		} catch (IOException e) {
		    // TODO Auto-generated catch block
			Log.v("GETREFERRALSSERVICE", e.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//IF RESPONSE HAS NEW ITEMS
		//SET ITEMS AS EXTRAS IN APPROVEREFERRAL ACTIVITY
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(ns);
		
		int icon = R.drawable.restaurant;
		String tickerText = "New referrals!";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "New referrals!";
		CharSequence contentText = "You have [X] new referrals awaiting your approval.";
		Intent notificationIntent = new Intent(this, ApproveReferral.class);
		//THIS IS WHERE TO SET THE EXTRAS I THINK
		
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
		nm.notify(ActivityKey.NEW_REFERRAL.ordinal(), notification);
	}

}
