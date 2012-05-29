package we.should.search;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Place is immutable.
 * 
 * To construct a Place, you need to pass in a JSONObject 
 * come back from querying google place search by location and name.
 * It will parse the JSONObject and provide method to address information.
 * 
 * Before using the Place, be sure to check whether or not the Place is valid.
 * When there is error in parsing the JSONObject. call the isValid() method to 
 * determine if Place is valid.
 * 
 * @throws JSONException - if the JSONObject pass through the constructor fail.
 * @author Lawrence
 */
public class SearchResult {
	private double latitude, longitude, rating;
	protected String icon, id, name, vicinity, reference;
	public SearchResult(JSONObject obj) throws JSONException {
		//getLocation(obj);
		icon = obj.optString("icon", null);
		id = obj.optString("id", null);
		name = obj.optString("name", null);
		vicinity  = obj.optString("vicinity", null);
		reference = obj.optString("reference", null);
	}
	
//	/**
//	 * Getting the location off the JSONObject
//	 * @param obj
//	 * @throws JSONException
//	 */
//	private void getLocation(JSONObject obj) throws JSONException {
//		JSONObject geo = obj.getJSONObject("geometry");
//		JSONObject loc = geo.getJSONObject("location");
//		latitude = Double.parseDouble(loc.getString("lat"));
//		longitude = Double.parseDouble(loc.getString("lng"));
//	}
	
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
	/**
	 * Returns a qualifying detail string description of this.
	 * This will be displayed as a subtitle in the suggestion list.
	 * 
	 * Override this method in subclasses to change how what 
	 * details are displayed next to results.
	 * @return
	 */
	public String getDetail() {
		return getVicinity();
	}
	
	/**
	 * @return - String - the name of this place.
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * they are the same, if their reference is the same
	 */
	@Override
	public boolean equals(Object o) {
		if(o != null && getClass() == o.getClass()) {
			SearchResult p = (SearchResult) o;
			return p != null && p.reference.equals(this.reference);
		} else {
			return false;
		}
	}
	
	

}
