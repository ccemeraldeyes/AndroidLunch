package we.should.search;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MovieRequest extends Search {
	private final String SCHEME = "http";
	private final String PLACES_SEARCH_URL =  "http://www.imdbapi.com/?t=";
	public final String LOG_KEY = "WeShould.MovieRequest";
	private Map<String, IMDBRecord> cache; //TODO;
	
	public MovieRequest() {}
	
	public List<Place> search(String query){
		List<Place> out = new ArrayList<Place>();
		String baseUrl = PLACES_SEARCH_URL;
        String url = baseUrl + query.trim();
    	Log.v(LOG_KEY, "build string url is: " + url);
    	URI request;
    	try {
			request = new URI(SCHEME, url, null);
		} catch (URISyntaxException e) {
			Log.i(LOG_KEY, "URL improperly formed! " + url);
			return out;
		}
		JSONObject response = new JSONObject();
		try {
			response = Search.executeQuery(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
			Place m = new IMDBRecord(response);
			out.add(m);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
		
		
	}
	public DetailPlace searchPlaceDetail(String reference) {
		List<Place> out = search(reference);
		DetailPlace ret = null;
		if(out.size() > 0){
			ret = (IMDBRecord) out.get(0);
		}
		return ret;
	}
	
}
