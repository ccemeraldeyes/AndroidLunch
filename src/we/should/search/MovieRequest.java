package we.should.search;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class MovieRequest extends Search {
	private final String SCHEME = "http";
	private final String PLACES_SEARCH_URL =  "//www.imdbapi.com/?t=";
	public final String LOG_KEY = "WeShould.MovieRequest";
	
	private final Map<String, SearchResult> cache; //This reduces time for item filling. 
	
	public MovieRequest() {
		cache = new HashMap<String, SearchResult>();
	}
	
	
	public List<SearchResult> search(String query){
		List<SearchResult> out = new ArrayList<SearchResult>();
		if(cache.containsKey(query)) {
			out.add(cache.get(query));
			return out;
		}

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
		} catch (Exception e) {
			Log.i(LOG_KEY, "Movie lookup failed.");
		}
		Log.v(LOG_KEY, response.toString());
		try {
			SearchResult m = new IMDBSearchResult(response);
			out.add(m);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		for(SearchResult s : out) cache.put(s.getName(), s);
		return out;
		
	}
	
	/**
	 * 
	 * @param reference
	 * @return
	 */
	public DetailSearchResult searchDetail(String reference) {
		List<SearchResult> out = search(reference);
		DetailSearchResult ret = null;
		if(out.size() > 0){
			ret = (IMDBSearchResult) out.get(0);
		}
		return ret;
	}
	
}
