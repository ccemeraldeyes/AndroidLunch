package we.should.search;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.util.Log;

/**
 * PlaceRequest is the object use for querying on Google Place API.
 * It has two major types of querying, searchByLocation, and searchPlaceDetail
 * 
 * It is an object create by AsynTask in Activity to perform query on Google Place API.
 * @author Lawrence
 */
public class PlaceRequest extends Search{
	/**
	 * PlaceRequest.searchTypes defines what type of Place are you searching for.
	 * available types for the search can be find in https://developers.google.com/maps/documentation/places/supported_types
	 * 
	 * Currently it will search for any Place with type University or Restaurant, or Movie_rental, or movie_theater, or cafe, or bar.
	 */
	private static String keyString = "AIzaSyDb2H4C8ztSgKJtlxx5jheMHjV_vIrIxAA";
	private static final String SCHEME = "https";
	private static final String PLACES_SEARCH_URL =  "//maps.googleapis.com/maps/api/place/search/json?";
	private static final String PLACES_DETAIL_SEARCH = "//maps.googleapis.com/maps/api/place/details/json?";
	public static final String LOG_KEY = "WeShould.search";
	
	private GeoPoint l;
	
	public PlaceRequest(GeoPoint l) {
		this.l = l;
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
	public List<SearchResult> search(String searchname){
		if(l == null || searchname == null) {
			throw new IllegalArgumentException("input is null, or location is null");
		}
		
		Log.v(LOG_KEY, "Start SearchByLocation: " + searchname +".");
		try {
			searchname = searchname.trim();
			URI url = buildURLForGooglePlaces(l, searchname);
			JSONObject obj = Search.executeQuery(url);
			//make sure status is okay before we get the results
			if(obj.getString("status").equals("OK")) {			
				JSONArray jsonPlaces = obj.getJSONArray("results");
				List<SearchResult> places = new ArrayList<SearchResult>();
				//Loop throught the results and create Place
				for(int i = 0; i < jsonPlaces.length(); i++) {
					JSONObject place = (JSONObject) jsonPlaces.get(i);
					places.add(new SearchResult(place));
				}
				return places;
			} else if(obj.getString("status").equals("ZERO_RESULTS")){
				Log.v(LOG_KEY, "zero results");
				return null;
			} else {
				Log.v(LOG_KEY, obj.getString("status"));
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
	public DetailSearchResult searchDetail(String reference) {
		if(reference == null) {
			throw new IllegalArgumentException("reference string is null");
		}
		
		try {		
			URI url = buildURLForDetailPlace(reference);
			JSONObject obj = Search.executeQuery(url);
			if(obj.getString("status").equals("OK")) {	
				//making a new DetailPlace
				JSONObject data = obj.getJSONObject("result");
				return new DetailSearchResult(data);
			} else {
				Log.v(LOG_KEY, "query status fail");
			}
		} catch (Exception e) {
			Log.e(LOG_KEY, e.getMessage());
		}
		return null;
	}
	
	
	
	

	/** 
	 * Build the url for the searchByLocation query
	 * 
	 * @param myLocation - given the location to search
	 * @param searchName - the name to filter the results.  searchName 
	 *   is filter such that only places contains the exactly 
	 *   searchName in its name will be return
	 * @return url for the searchByLocation query
	 * @throws URISyntaxException 
	 */
    private URI buildURLForGooglePlaces(GeoPoint myLocation, String searchName) 
    		throws URISyntaxException{
        String baseUrl = PLACES_SEARCH_URL;
        String lat = String.valueOf(myLocation.getLatitudeE6()*1.0/1E6);
        String lon = String.valueOf(myLocation.getLongitudeE6()*1.0/1E6);
        String url = baseUrl + "location=" + lat + "," + lon + "&" +
                     "rankby=distance" + "&" + "sensor=true" +
                     "&" + "name=" + searchName +
                     "&" + "key=" + keyString;
    	Log.v(LOG_KEY, "build string url is: " + url);
        return new URI(SCHEME, url, null);
    }
    
    /**
     * @param referenceString - the reference obtain by Place Object 
     *   which get it by performing a searchByLocation query
     * @return url for the placeDetail query
     */
    private URI buildURLForDetailPlace(String referenceString) throws URISyntaxException {
    	String baseUrl = PLACES_DETAIL_SEARCH;
    	String reference = referenceString;
    	String url = baseUrl + "reference=" + reference
    			+ "&" + "sensor=true" + "&" + "key=" + keyString;
    	return new URI(SCHEME, url, null);
    }
}
