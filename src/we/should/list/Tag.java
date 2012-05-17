package we.should.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import we.should.database.WSdb;
import android.content.Context;
import android.database.Cursor;

public class Tag implements Serializable {
	
	/** Needed for serialization. **/
	private static final long serialVersionUID = -2873443189292108185L;
	
	protected static String idKey = "id";
	protected static String tagKey = "tag";
	protected static String colorKey = "color";
	
	private int id;
	private String tag;
	private String color;
	
	/**
	 * Creates a new tag object with the given name and DB id
	 * @param id
	 * @param tag
	 */
	public Tag(int id, String tag, String color){
		this.id = id;
		this.tag = tag.substring(0,Math.min(tag.length(), 32)).trim();
		this.color = color;
	}
	/**
	 * 
	 * @param o
	 */
	protected Tag(JSONObject o){
		try {
			this.id = o.getInt(idKey);
			this.color = o.getString(colorKey);
			this.tag = o.getString(tagKey);
		} catch (JSONException e) {
			throw new IllegalArgumentException("JSON object parameter improperlly formed!");
		}
		
	}
	/**
	 * Returns the name of this tag
	 */
	public String toString(){
		return tag;
	}
	/**
	 * Returns the DB row entry of this tag. 0 if it is
	 * not saved to the DB.
	 * @return this.id
	 */
	public int getId(){
		return this.id;
	}
	/**
	 * Sets the db id of this tag
	 * @param i - should be the return value of a db insert call
	 */
	public void setId(int i){
		this.id = i;
	}
	/**
	 * Returns a list of all of the tags added to the DB
	 * @param ctx of the database
	 * @return a list of tag objects pulled from the DB
	 */
	public static List<Tag> getTags(Context ctx){
		if(ctx == null){
			throw new IllegalArgumentException("Context cannot be null!");
		}
		List<Tag> out = new ArrayList<Tag>();
		WSdb db = new WSdb(ctx);
		db.open();
		Cursor tags = db.getAllTags();
		while(tags.moveToNext()){
			String color = tags.getString(2);
			String tag = tags.getString(1);
			int id = tags.getInt(0);
			out.add(new Tag(id, tag, color));
		}
		tags.close(); //TS
		db.close(); //TS
		return out;	
	}
	
	/**
	 * Returns a formatted list of a set of tags.
	 */
	public static String getFormatted(Collection<? extends Tag> s) {
		StringBuilder builder = new StringBuilder();
	     Iterator<? extends Tag> iter = s.iterator();
	     while (iter.hasNext()) {
	         builder.append(iter.next());
	         if (!iter.hasNext()) {
	           break;                  
	         }
	         builder.append("; ");
	     }
	     return builder.toString();
	}
	
	/**
	 * Returns a map that maps each possible color's human readable name to its
	 * value.
	 * 
	 * @return see above
	 */
	public static Map<String, String> getAllTagColors() {
		Map<String, String> colorMap = new HashMap<String, String>();
		colorMap.put("Red", "#FF0000");
		colorMap.put("Green", "#00FF00");
		colorMap.put("Blue", "#0000FF");
		colorMap.put("Yellow", "#FFFF00");
		colorMap.put("Purple", "#FF00FF");
		colorMap.put("Cyan", "#00FFFF");
		return colorMap;
	}
	/**
	 * returns true if the names of this and o are the same.
	 */
	public boolean equals(Object o){
		if(o == this) return true;
		if(o == null || !(o instanceof Tag)) return false;
		Tag cp= Tag.class.cast(o);
		return this.tag.equals(cp.tag);
	}
	
	public int hashCode(){
		return this.tag.hashCode();
	}
	public JSONObject toJSON() throws JSONException {
		JSONObject tagString = new JSONObject();
		tagString.put(Tag.idKey, this.getId());
		tagString.put(Tag.tagKey, this.toString());
		tagString.put(Tag.colorKey, this.getColor());
		return tagString;
	}
	public String getColor() {
		return this.color;
	}
}
