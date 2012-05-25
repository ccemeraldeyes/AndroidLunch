package we.should.search;

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

	public abstract List<Place> search(String query) throws Exception;
	
	/**
	 * @param url - the url to execute.
	 * @return a JSONObject from Google Place API
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
