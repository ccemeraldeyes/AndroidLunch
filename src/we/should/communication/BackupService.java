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
import we.should.database.WSdb;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * BackupService stores a stringified copy of the local database to the remote database.
 * 
 * @author colleen
 *
 */
public class BackupService extends IntentService {
	
	/**
	 * BackupService constructor
	 */
	public BackupService() {
		super("we.should.communication.BackupService");
	}


	/**
	 * Gets a stringified version of the local database and sends it to the remote database to be saved
	 * 
	 * @param intent
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		Log.v("BACKUP SERVICE", "starting backup service");
		
		WSdb db = new WSdb(this);
		db.open();
		
		String data = db.Backup();
		db.close();
		
		Bundle extras = intent.getExtras();
		String email = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		Log.d("BACKUP LEN", ""+data.length()+"");
		
		sendToDb(data, email);
		
	}

	public void sendToDb(String data, String email){
		//The data is split into sections of 4096 bytes to fit within HttpRequest character limits
		for(int i=0; i<=data.length()/4096; i++){
			
			String splitData = data.substring(i*4096, Math.min(i*4096+4096, data.length()));
			HttpClient httpclient = new DefaultHttpClient();
	
			String paramString = buildQueryString(i, email, splitData);
			
			HttpGet httpget = new HttpGet("http://23.23.237.174/backup?"+paramString);
	
			try {

				httpclient.execute(httpget);
			    Log.d("SAVE TO REMOTE", "backing up");
	
			} catch (ClientProtocolException e) {
				Log.e("SAVE TO REMOTE", e.getMessage());
			} catch (IOException e) {
				Log.e("SAVE TO REMOTE", e.getMessage());
			}
		}
	}
	
	
	public String buildQueryString(int i, String email, String splitData){
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", email));	    
	    nameValuePairs.add(new BasicNameValuePair("data", splitData));
	    
	    if(i==0){
	    	nameValuePairs.add(new BasicNameValuePair("append", "false"));
	    } else {
	    	nameValuePairs.add(new BasicNameValuePair("append", "true"));
	    }
		
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		
		return paramString;
	}

	public static String getUrl(){
		return "backup";
	}
}
