
package we.should.list;

import java.io.IOException;
import java.util.*;

import org.json.*;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import we.should.WeShouldActivity;
import we.should.database.*;

/**
 * @author Davis Shepherd
 *
 *	The Category class represents an abstraction for a set of items that
 *  fall under this category.  It provides a factory method for adding items as well
 *  as returning items that have already been made. 
 *  
 * 
 *  
 *  
 *  
 */
public abstract class Category {
	public static final String DEFAULT_COLOR = "FFFFFF";
	protected final String name;	
	protected Context c = null;
	protected int id;
	protected String color; //test value
	protected List<Field> fields;

	
	/**
	 * Creates a new abstract Category with the given name.
	 * @param name - name of this category
	 */
	protected Category(String name){
		this.name = name;
		this.fields = new LinkedList<Field>();
		this.color = this.DEFAULT_COLOR;
	}
	protected Category(String name, List<Field> fields){
		this(name);
		this.fields = fields;
		this.color = this.DEFAULT_COLOR;
	}
	protected Category(String name, JSONArray a){
		this(name);
		try {
			this.fields = fieldsFromDB(a);
		} catch (JSONException e) {
			this.fields = new LinkedList<Field>();
			Log.w("Category constructor", "JSONArray passed to constructor has error");
		}
		this.color = this.DEFAULT_COLOR;
	}
	/**
	 * Sets the color representation of this category
	 * @param color is a hexadecimal representation of the color to set
	 */
	public void setColor(String color) {
		if(color.matches("^[0-9A-Fa-f]+$")) this.color = color;
		else throw new IllegalArgumentException("Color string " + color + " is not hex!");
	}
	/**
	 * Returns the hexidecimal representation of this color
	 * @return the color of this
	 */
	public String getColor(){
		return this.color;
	}
	/**
	 * Returns the items contained in this. That is the items that have both be created with
	 * newItem() and then saved with Item.save().
	 * @return the set of items in this
	 */
	public abstract List<Item> getItems();
	
	
	/**
	 * Returns a new item in this Category. This item is not added until Item.save() is called.
	 * @return a new Item(C)
	 */
	public abstract Item newItem();
	
	/**
	 * Adds the given item to this
	 * @param i - the item to be added
	 * @return true always
	 */
	protected abstract boolean addItem(Item i);
	
	/**
	 * Removes this given item i if it is contained in this and returns true. Otherwise returns false
	 * @param i
	 * @return true if item is removed, false otherwise
	 * @modifies removes i from this.Items
	 */
	protected abstract boolean removeItem(Item i);
	
	/**
	 * Returns the list of Fields associated with this category
	 * @return a list of Field objects that can be edited.
	 */
	public abstract List<Field> getFields();
	
	/**
	 * Saves this category object to the database associated with the given context
	 * @param ctx specifies the context of the database
	 * @throws IOException 
	 */
	public abstract void save(Context ctx);
	
	/**
	 * Returns the name of this Category
	 * @return name of this category
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Parses the database and returns all categories that have been created
	 * @return the list of created categories
	 * @throws JSONException 
	 */
	public static final Set<Category> getCategories(Context ctx) {
		WSdb db = new WSdb(ctx);
		db.open();
		Cursor c = db.getAllCategories();
		Category cat = null;
		Set<Category> out = new HashSet<Category>();
		while(c.moveToNext()){
			 int id = c.getInt(0);
			 String name = c.getString(1);
			 String color = c.getString(2);
			 String schema = c.getString(3);
			 if (name.equals("Movies")){
				 cat = new Movies();
			 } else {
				 JSONArray schemaList;
				try {
					schemaList = new JSONArray(schema);
					cat = new GenericCategory(name, schemaList);
				} catch (JSONException e) {
					Log.e("Category.getCategories", "Field Schema improperly formatted!");
					e.printStackTrace();
				}
			 }
			 cat.id = id;
			 cat.color = color;
			 cat.c = ctx;
			 out.add(cat);
		}
		return out;
	}
	JSONArray fieldsToDB(){
		JSONArray out = new JSONArray();
		for(Field f : this.fields){
			out.put(f.toDB());
		}
		return out;
	}
	List<Field> fieldsFromDB(JSONArray a) throws JSONException{
		List<Field> out = new LinkedList<Field>();
		for(int i = 0; i < a.length(); i++){
			out.add(new Field(a.getString(i)));
		}
		return out;
	}
	

}
