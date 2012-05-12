package we.should.search;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * Detail place is an extension to place.  It has more information about a place.
 * including phoneNumber, website, url, address, international_phoneNumber.
 * 
 * Before using the DetailPlace, be sure to check whether or not the  DetailPlace is valid.
 * When there is error in parsing the JSONObject, call the isValid() method to determine that
 * 
 * To construct a Detailplace, you need a JSONObject from a DetailPlaceQuery on Google API.
 * @author Lawrence
 */
public class DetailPlace extends Place{
	private String phoneNumber, website, url, address, international_phoneNumber;
	public DetailPlace(JSONObject obj) {
		super(obj);
		try {
			phoneNumber = obj.getString("formatted_phone_number");
			website = obj.getString("website");
			url = obj.getString("url");
			address = obj.getString("formatted_address");
			international_phoneNumber = obj.getString("international_phone_number");
			isValid = true;
		} catch (JSONException e) {
			Log.v(PlaceRequest.LOG_KEY, "fail to parse to DetailPlace");
			isValid = false;
		}	
	}
	
	/**
	 * To get the Local Phone Number
	 * @return String - local phone number
	 */
	public String getLocalPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * To get the international Phone Number
	 * @return String - the internation phone number
	 */
	public String getInternational_phoneNumber() {
		return international_phoneNumber;
	}
	
	/**
	 * @return the url link in the Google Map
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return the website of the place.
	 */
	public String getWebSite() {
		return website;
	}
	
	/**
	 * @return the address of the place
	 */
	public String getAddress() {
		return address;
	}	
}
