package we.should.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import we.should.WeShouldActivity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BackupService extends IntentService {
	

	
	public BackupService() {
		super("we.should.communication.BackupService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.v("BACKUP SERVICE", "starting backup service");
		
		String data = ""; //TODO: get data from troy's thing
		
		Bundle extras = intent.getExtras();
		String email = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		for(int i=0; i<=data.length()/4096; i++){
			
			String splitData = data.substring(i*4096, i*4096+4096);
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			
	
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("user_email", email));	    
		    nameValuePairs.add(new BasicNameValuePair("data", splitData));
		    
		    if(i==1){
		    	nameValuePairs.add(new BasicNameValuePair("append", "false"));
		    } else {
		    	nameValuePairs.add(new BasicNameValuePair("append", "true"));
		    }
			
			String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
	
			
			HttpGet httpget = new HttpGet("http://23.23.237.174/backup?"+paramString);
	
			try {
				
	
				
				httpclient.execute(httpget);
			    Log.v("SAVE TO REMOTE", "backing up");
	
			} catch (ClientProtocolException e) {
			    // TODO Auto-generated catch block
				Log.v("SAVE TO REMOTE", e.getMessage());
			} catch (IOException e) {
			    // TODO Auto-generated catch block
				Log.v("SAVE TO REMOTE", e.getMessage());
			}
			}
		
	}



}
