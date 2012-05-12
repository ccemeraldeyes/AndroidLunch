package we.should.search;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
/**
 * To construct a Place, you need to pass in a JSONObject 
 * come back from querying google place search by location and name.
 * It will parse the JSONObject and provide method to address information.
 * 
 * Before using the Place, be sure to check whether or not the Place is valid.
 * When there is error in parsing the JSONObject. call the isValid() method to determine if Place is valid.
 * 
 * @author Lawrence
 */
public class Place {
	private double latitude, longitude, rating;
	private String icon, id, name, vicinity, reference;
	private List<PlaceType> types;
	private PlaceType bestType;
	boolean isValid;
	//This is the filter list of the Place
	//We are looking for places, but because there are chance that a place contains multiple types
	//this filter list will have the place in order.  For example [University, Cafe, Bar], 
	//it will choose University as the bestType because it is first index in the filter
	private static PlaceType[] filterList = new PlaceType[]{PlaceType.UNIVERSITY, 
		PlaceType.RESTAURANT, PlaceType.MOVIE_RENTAL, PlaceType.MOVIE_THEATER, PlaceType.CAFE, PlaceType.BAR};

	public Place(JSONObject obj) {
		try {
			getLocation(obj);
			getTypes(obj);
			icon = obj.getString("icon");
			id = obj.getString("id");
			name = obj.getString("name");
			vicinity  = obj.getString("vicinity");
			reference = obj.getString("reference");
			if(types.contains(PlaceType.RESTAURANT)) {
				rating = obj.getDouble("rating");
			} else {
				rating = 0;
			}
			isValid = true;
		} catch (JSONException e) {
			Log.v(PlaceRequest.LOG_KEY, "fail to parse to Place");
			isValid = false;
		}
	}
	
	/**
	 * This should be called first whenever you create a Place
	 * When a place is invalid, you shouldn't use the Place cuz.
	 * some data are missing. Ex. id, lat, lng, etc....
	 * 
	 * @return true when it is valid, false otherwise.
	 */
	public boolean isValid() {
		return isValid;
	}
	
	//Getting the Type off the JSONobject
	private void getTypes(JSONObject obj) throws JSONException {
		types = new ArrayList<PlaceType>();
		JSONArray list = obj.getJSONArray("types");
		for(int i = 0; i < list.length(); i++) {
			for(int j = 0; j < PlaceRequest.searchTypes.length; j++) {
				//make sure the type is in our search request before we create the PlaceType Item
				if(list.getString(i).equals(PlaceRequest.searchTypes[j]))
					types.add(PlaceType.createPlaceType(list.getString(i)));
			}
		}
		bestType = filterType();
	}
	
	//Getting the location off the JSONObject
	private void getLocation(JSONObject obj) throws JSONException {
		JSONObject geo = obj.getJSONObject("geometry");
		JSONObject loc = geo.getJSONObject("location");
		latitude = Double.parseDouble(loc.getString("lat"));
		longitude = Double.parseDouble(loc.getString("lng"));
	}
	
	/**
	 * @return Google place reference, can be useful for other detail search
	 */
	public String getReference() {
		return reference;
	}
	
	/**
	 * @return - String-name represent the name of the place
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return - all the types that matches the place
	 */
	public List<PlaceType> getAllTypes() {
		return types;
	}
	
	/**
	 * @return -String the best type associate with the place
	 */
	public PlaceType getBestType() {
		return bestType;
	}
	
	/**
	 * @return double - represent the rating if it is a restaurant
	 *                - if place doesn't have rating, it will return 0
	 */
	public Double getRating() {
		return rating;
	}
	
	/**
	 * @return - String-id represent the id that is store as in Google Place.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return - Double- represent the latitude of the place
	 */
	public double getLat() {
		return latitude;
	}
	
	/**
	 * @return - Double - represent the longitude of the place
	 */
	public double getLng() {
		return longitude;
	}
	
	/**
	 * @return - String - the url if they have one.
	 */
	public String getIconUrl() {
		return icon;
	}
	
	/**
	 * @return - String - the vicinity around the place.
	 */
	public String getVicinity() {
		return vicinity;
	}
	
	//Filter the PlaceType and find the bestType it has
	private PlaceType filterType() {
		for(int i = 0; i < filterList.length; i++) {
			if(types.contains(filterList[i])) {
				return filterList[i];
			}
		}
		return null;
	}
}