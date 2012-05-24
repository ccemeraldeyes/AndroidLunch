package we.should.communication;

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

import we.should.WeShouldActivity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RestoreService extends IntentService{

	public RestoreService() {
		super("we.should.communication.RestoreService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		String username = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		HttpClient httpclient = new DefaultHttpClient();

	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", username));
		
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		
		HttpGet httpget = new HttpGet("http://23.23.237.174/restore?"+paramString);
		
		JSONObject resp = new JSONObject();
		JSONArray categories = new JSONArray();
		
		try {
			
			HttpResponse response = httpclient.execute(httpget);
			
			InputStream is = response.getEntity().getContent();
			
			byte[] buf = new byte[4096];
			is.read(buf);
			
			Log.v("RESTORE RESPONSE", new String(buf));

			resp = new JSONObject(new String(buf));
			categories = resp.getJSONArray("categories"); 
			//TODO: do the same for tags and items
		   		    

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
			Log.v("GETREFERRALSSERVICE", e.getMessage());
		} catch (IOException e) {
		    // TODO Auto-generated catch block
			Log.v("GETREFERRALSSERVICE", e.getMessage());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.v("GET REFFERAL SERVICE", "JSON EXCEPTION "+e.getMessage());
		}
		
	}

}
