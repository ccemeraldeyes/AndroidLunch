package we.should.search;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import we.should.list.Field;

public class IMDBSearchResult extends DetailSearchResult {
	private final String BASE_URL = "http://m.imdb.com/title/";
	private String year, genre;
	
	public IMDBSearchResult(JSONObject obj) throws JSONException {
		super(obj);
		website = obj.optString("imdbID", "");
		if(website != null){
			website = BASE_URL + website;
		}
		name = obj.optString("Title", null);
		year = obj.optString("Year");
		genre = obj.optString("Genre");
		
	}
	
	@Override
	public String getDetail(){
		if (genre.length() > 0){
			return (this.year + " - " + this.genre);
		} else {
			return(this.year);
		}
		
	}
	@Override
	public Map<Field, String> asFieldMap() {
		Map<Field, String> map = new HashMap<Field, String>();
		map.put(Field.ADDRESS, getAddress());
		map.put(Field.WEBSITE, getWebSite());
		map.put(Field.COMMENT, getDetail());
		return map;
	}


}
