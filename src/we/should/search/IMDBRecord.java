package we.should.search;

import org.json.JSONException;
import org.json.JSONObject;

public class IMDBRecord extends DetailPlace {
	private final String BASE_URL = "http://www.imdb.com/title/";
	private String year;
	
	public IMDBRecord(JSONObject obj) throws JSONException {
		super(obj);
		website = obj.optString("imdbID", null);
		if(website != null){
			website = BASE_URL + website;
		}
		name = obj.optString("Title", null);
		year = obj.optString("Year");
		
	}
	
	@Override
	public String getDetail(){
		return this.year;
	}


}
