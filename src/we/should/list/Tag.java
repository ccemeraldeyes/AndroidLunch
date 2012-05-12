package we.should.list;

import java.util.ArrayList;
import java.util.List;

import we.should.database.WSdb;
import android.content.Context;
import android.database.Cursor;

public class Tag {
	protected static String idKey = "id";
	protected static String tagKey = "tag";
	
	private int id;
	private String tag;
	
	/**
	 * Creates a new tag object with the given name and DB id
	 * @param id
	 * @param tag
	 */
	public Tag(int id, String tag){
		this.id = id;
		this.tag = tag;
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
			String tag = tags.getString(1);
			int id = tags.getInt(0);
			out.add(new Tag(id, tag));
		}
		return out;	
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

}
