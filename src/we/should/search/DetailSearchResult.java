package we.should.search;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import we.should.list.Field;


/**
 * DetailSearchResult is immutable.
 * 
 * DetailSearchResult is an extension to SearchResult.  It has more information 
 * about a result *potentially* including phoneNumber, website, url, address, 
 * international_phoneNumber. They contain and empty string otherwise
 * 
 * Before using the DetailPlace, be sure to check whether or not the
 * DetailPlace is valid. When there is error in parsing the JSONObject, 
 * call the isValid() method to determine that
 * 
 * @author Lawrence
 */
public class DetailSearchResult extends SearchResult{
	protected String phoneNumber, website, url, address, 
	               international_phoneNumber;
	
	/**
	 * To construct a Detailplace, you need a 
	 * 
	 * @param obj JSONObject from a DetailPlaceQuery on Google API.
	 * @thorws JSONException - if obj fails.
	 */
	public DetailSearchResult(JSONObject obj) throws JSONException {
		super(obj);
		phoneNumber = obj.optString("formatted_phone_number", "");
		website = obj.optString("website", "");
		url = obj.optString("url", "");
		address = obj.optString("formatted_address", "");
		international_phoneNumber = obj.optString("international_phone_number", "");
	}
	
	/**
	 * @return String - local phone number
	 */
	public String getLocalPhoneNumber() {
		return phoneNumber;
	}
	
	/**
	 * @return String - the international phone number
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
	 * @return the fields of this DetailPlace as a field map.
	 */
	public Map<Field, String> asFieldMap() {
		Map<Field, String> map = new HashMap<Field, String>();
		map.put(Field.ADDRESS, getAddress());
		map.put(Field.WEBSITE, getWebSite());
		map.put(Field.PHONENUMBER, getLocalPhoneNumber());
		return map;
	}
	
	/**
	 * they are the same, if their reference is the same
	 */
	@Override
	public boolean equals(Object o) {
		if(o != null && getClass() == o.getClass()) {
			DetailSearchResult p = (DetailSearchResult) o;
			return super.equals(p) && p.phoneNumber.equals(this.phoneNumber)
				&& p.website.equals(this.website)
				&& p.url.equals(this.url)
				&& p.address.equals(this.address)
				&& p.international_phoneNumber.equals(this.international_phoneNumber);
				
		} else {
			return false;
		}
	}
}
