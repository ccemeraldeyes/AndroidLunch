package we.should.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import we.should.Color;
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
	private Color color;
	
	/**
	 * Creates a new tag object with the given name and DB id
	 * @param id
	 * @param tag
	 * @param color
	 */
	public Tag(int id, String tag, Color color){
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
			this.tag = o.getString(tagKey);
			this.color = Color.get(o.getString(colorKey));
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
			Color color = Color.get(tags.getString(2));
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
		tagString.put(Tag.colorKey, this.getColor().toString());
		return tagString;
	}
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Returns the tag with the same name as name, or null if no such tag exists.
	 * @param ctx the Context to use
	 * @param name the name of the tag to get
	 * @return the tag called name
	 */
	public static Tag get(Context ctx, String name) {
		List<Tag> list = getTags(ctx);
		for (Tag tag : list) {
			if (tag.toString().equals(name)) {
				return tag;
			}
		}
		return null;
	}
}
