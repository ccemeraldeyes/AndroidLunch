package we.should.search;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import we.should.list.Field;


/**
 * Detail place is an extension to place.  It has more information about a place.
 * including phoneNumber, website, url, address, international_phoneNumber.
 * 
 * Before using the DetailPlace, be sure to check whether or not the  DetailPlace is valid.
 * When there is error in parsing the JSONObject, call the isValid() method to determine that
 * 
 * To construct a Detailplace, you need a JSONObject from a DetailPlaceQuery on Google API.
 * 
 * @thorws JSONException - if the JSONObject pass through the constructor fail.
 * @author Lawrence
 */
public class DetailPlace extends Place{
	private String phoneNumber, website, url, address, international_phoneNumber;
	
	public DetailPlace(JSONObject obj) throws JSONException {
		super(obj);
		phoneNumber = obj.optString("formatted_phone_number", null);
		website = obj.optString("website", null);
		url = obj.optString("url", null);
		address = obj.optString("formatted_address", null);
		international_phoneNumber = obj.optString("international_phone_number", null);
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

	/**
	 * Returns the fields of this DetailPlace as a field map.
	 * @return see above you loony
	 */
	public Map<Field, String> asFieldMap() {
		Map<Field, String> map = new HashMap<Field, String>();
		map.put(Field.ADDRESS, getAddress());
		map.put(Field.WEBSITE, getWebSite());
		map.put(Field.PHONENUMBER, getLocalPhoneNumber());
		return map;
	}
}
