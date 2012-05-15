package we.should.search;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

/**
 * PlaceRequest is the object use for querying on Google Place API.
 * It has two major types of querying, searchByLocation, and searchPlaceDetail
 * 
 * It is an object create by AsynTask in Activity to perform query on Google Place API.
 * @author Lawrence
 */
public class PlaceRequest {
	/**
	 * PlaceRequest.searchTypes defines what type of Place are you searching for.
	 * available types for the search can be find in https://developers.google.com/maps/documentation/places/supported_types
	 * 
	 * Currently it will search for any Place with type University or Restaurant, or Movie_rental, or movie_theater, or cafe, or bar.
	 */
	private static String keyString = "AIzaSyDb2H4C8ztSgKJtlxx5jheMHjV_vIrIxAA";
	private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAIL_SEARCH = "https://maps.googleapis.com/maps/api/place/details/json?";
	public static final String LOG_KEY = "WeShould.search";
	public PlaceRequest() {
	}
	
	
	/**
	 * The searchByLocation only support up to 50000 meters away from the given location according to Google Place API.
	 * @param l - Location of the searchPlace
	 * @param searchname - the name to search, Google Place API will return any places' name that contains the searchName
	 * @return
	 * @throws Exception
	 */
	//Possible feature:
	//Note that Location can be get by getting the Device Location, on when user click on the map, we can get the location.
	public List<Place> searchByLocation(Location l, String searchname) throws Exception{
		if(l == null) {
			throw new IllegalArgumentException("Location is null");
		}
		Log.v(LOG_KEY, "Start SearchByLocation");
		String url = buildURLForGooglePlaces(l, searchname);
		try {		
			JSONObject obj = executeQuery(url);
			//make sure status is okay before we get the results
			if(obj.getString("status").equals("OK")) {			
				JSONArray jsonPlaces = obj.getJSONArray("results");
				List<Place> places = new ArrayList<Place>();
				//Loop throught the results and create Place
				for(int i = 0; i < jsonPlaces.length(); i++) {
					JSONObject place = (JSONObject) jsonPlaces.get(i);
					places.add(new Place(place));
				}
				return places;
			} else {
				Log.v(LOG_KEY, "query status fail");
			}
		} catch (Exception e) {
			Log.v(LOG_KEY, e.getMessage());
		}
		return null;
	}
	
	/**
	 * This method perform the PlaceDetailSearch to get more information about the 
	 * place you want to search.
	 * 
	 * To perform a PlaceDetailSearch, you must first obtain the reference of the place
	 * you want to search, reference can be find in Place Object which get it by performing searchByLocation query.
	 * 
	 * @param reference - the reference String return from querying searchByLocation.
	 * @return DetailPlace if success, null if fail
	 */
	public DetailPlace searchPlaceDetail(String reference) {
		String url = buildURLForDetailPlace(reference);
		try {		
			JSONObject obj = executeQuery(url);
			if(obj.getString("status").equals("OK")) {	
				//making a new DetailPlace
				JSONObject data = obj.getJSONObject("result");
				return new DetailPlace(data);
			} else {
				Log.v(LOG_KEY, "query status fail");
			}
		} catch (Exception e) {
			Log.v(LOG_KEY, e.getMessage());
		}
		return null;
	}
	
	
	/*
	 * @param url - the url to execute.
	 * @return a JSONObject from Google Place API
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private JSONObject executeQuery(String url) throws JSONException, ClientProtocolException, IOException, URISyntaxException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(new URI(url));
		ResponseHandler<String> handler = new BasicResponseHandler();
		String result = httpclient.execute(request, handler);
		return new JSONObject (result);
	}
	

	/** 
	 * Build the url for the searchByLocation query
	 * 
	 * @param myLocation - given the location to search
	 * @param searchName - the name to filter the results 
	 * searchName is filter such that only places contains the exactly searchName in its name will be return
	 * 
	 * @return url for the searchByLocation query
	 */
    private String buildURLForGooglePlaces(Location myLocation, String searchName){
        String baseUrl = PLACES_SEARCH_URL;
        String lat = String.valueOf(myLocation.getLatitude());
        String lon = String.valueOf(myLocation.getLongitude());
        String url = baseUrl + "location=" + lat + "," + lon + "&" +
                     "rankby=distance" + "&" + "sensor=false" +
                     "&" + "name=" + searchName +
                     "&" + "key=" + keyString;
        return url;
    }
    
    /*
     * @param referenceString - the reference obtain by Place Object which get it by performing a searchByLocation query
     * @return url for the placeDetail query
     */
    private String buildURLForDetailPlace(String referenceString) {
    	String baseUrl = PLACES_DETAIL_SEARCH;
    	String reference = referenceString;
    	String url = baseUrl + "reference=" + reference
    			+ "&" + "sensor=true" + "&" + "key=" + keyString;
    	return url;
    }
}
