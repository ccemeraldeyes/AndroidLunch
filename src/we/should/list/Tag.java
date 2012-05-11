package we.should.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import we.should.database.WSdb;

import android.content.Context;
import android.database.Cursor;

public class Tag {
	protected static String idKey = "id";
	protected static String tagKey = "tag";
	
	private int id;
	private String tag;
	
	public Tag(int id, String tag){
		this.id = id;
		this.tag = tag;
	}
	public String toString(){
		return tag;
	}
	public int getId(){
		return this.id;
	}
	public void setId(int i){
		this.id = i;
	}
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
