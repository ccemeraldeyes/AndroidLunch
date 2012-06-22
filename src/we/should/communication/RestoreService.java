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

import we.should.WeShouldActivity;
import we.should.database.WSdb;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Gets a stringified version of the local database from the remote database and restores it.
 * 
 * @author colleen
 *
 */
public class RestoreService extends IntentService{

	/**
	 * RestoreService constructor
	 */
	public RestoreService() {
		super("we.should.communication.RestoreService");
	}

	/**
	 * Gets a stringified version of the local database from the remote database and restores it locally
	 * 
	 * @param intent
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String username = extras.getString(WeShouldActivity.ACCOUNT_NAME);
		
		String dbstring = buildDbString(username);
		
		WSdb db = new WSdb(this);
		db.open();
		db.Restore(dbstring);
		db.close();
		
		Intent weshouldactivity = new Intent();
		weshouldactivity.setClass(getApplicationContext(), WeShouldActivity.class);
		weshouldactivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		weshouldactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(weshouldactivity);
	}

	
	public String buildQueryString(int index, String username){
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    nameValuePairs.add(new BasicNameValuePair("user_email", username));
	    nameValuePairs.add(new BasicNameValuePair("index", ""+index+""));
		
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		return paramString;
	}
	
	public String buildDbString(String username){
		boolean done = false;
		String dbstring = "";
		int index = 0;
		
		//Since HttpRequests have a character limit, we have to get the data back in 4096 byte chunks
		//and build the string
		while(!done){
		
			HttpClient httpclient = new DefaultHttpClient();
	
			String paramString = buildQueryString(index, username);
			
			HttpGet httpget = new HttpGet("http://23.21.136.252/restore?"+paramString);
						
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
				JSONObject resp = new JSONObject(new String(buf));
				
				String doneString = resp.getString("done");
				if(doneString.equals("true")){
					done = true;
				}
				
				Log.d("RESTORE RESPONSE", new String(buf));
	

				dbstring += resp.getString("data");
			   		    
	
			} catch (ClientProtocolException e) {
				Log.e("RESTORE SERVICE", e.getMessage());
			} catch (IOException e) {
				Log.e("RESTORE SERVICE", e.getMessage());
			} catch (JSONException e) {
				e.printStackTrace();
			} 
			index++;
			
		}
		
		return dbstring;
	}
	
	public static String getUrl(){
		return "restore";
	}
}
