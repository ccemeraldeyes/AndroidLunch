package we.should.search;

/**
 * This is an abstract class to be used in performing item suggestion lookups.
 * Subclass this to create a new type of search method. 
 *
 */
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class Search {
	
	/**
	 * Override this method to return a list of SearchResult objects produced from a string
	 * query
	 * @param query
	 * @return a list of SearchResult objects that reflect the search query string
	 * 
	 */
	public abstract List<SearchResult> search(String query);
	
	/**
	 * Executes an HTTP get query on the given URI and returns the resulting JSON object.
	 * 
	 * @param url - the url to execute.
	 * @return a JSONObject of the result
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected static JSONObject executeQuery(URI url) 
			throws JSONException, ClientProtocolException, 
			IOException, URISyntaxException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		ResponseHandler<String> handler = new BasicResponseHandler();
		String result = httpclient.execute(request, handler);
		return new JSONObject (result);
	}
}
