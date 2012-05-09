package we.should.list;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.database.WSdb;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * 
 * @author Davis
 * 
 * Representation Invariants:
 * 
 * For all Items i in items{
 * 		i.category == this
 * }
 */
public class GenericCategory extends Category {
	private List<Item> items; 
	private boolean sync = false;
	
	
	public GenericCategory(String name, List<Field> fields, Context ctx) {
		super(name, fields, ctx);
		items = new LinkedList<Item>();
		checkRep();
		
	}
	protected GenericCategory(String name, JSONArray a, Context ctx) throws JSONException{
		super(name, a, ctx);
		items = new LinkedList<Item>();
		checkRep();
	}
	/**
	 * Checks the representation invariant.
	 * Throws an assertion error if it is violated.
	 */
	private void checkRep(){
		for(Item i : items){
			assert(i.getCategory() == this);
		}
	}
	@Override
	public List<Item> getItems() {
		if (!sync && ctx != null && id != 0) {
			WSdb db = new WSdb(ctx);
			db.open();
			Cursor cur = db.getItemsOfCategory(this.id);
			while (cur.moveToNext()) {
				JSONObject data = null;
				try {
					data = new JSONObject(cur.getString(4));
					GenericItem nIt = new GenericItem(this, ctx);
					nIt.DBtoData(data);
					nIt.id = cur.getInt(0);
					if(!this.items.contains(nIt)) this.items.add(nIt);
				} catch (JSONException e) {
					Log.e("GenericCategory.getItems()", "Database data string improperly formatted");
				}
			}
			db.close();
			sync = true;
		}
		return this.items;
	}

	@Override
	public Item newItem() {
		Item i = new GenericItem(this, ctx);
		return i;
	}
	
	@Override
	protected boolean addItem(Item i) {
		Boolean output = this.items.add(i);
		checkRep();
		return output;
	}
	@Override
	public List<Field> getFields(){
		return Collections.unmodifiableList(fields);
	}
	@Override
	protected boolean removeItem(Item i) {
		return this.items.remove(i);
	}
	@Override
	public void save() {
		if (ctx != null) {
			WSdb db = new WSdb(ctx);
			db.open();
			if (this.id == 0) {
				this.id = (int) db.insertCategory(this.name, this.color.hashCode(),
						fieldsToDB().toString());
			} else {
				db.updateCategory(this.id, this.name, this.color.hashCode(), fieldsToDB().toString());
				//TODO:Manage color ids
			}
			db.close();
		} else {
			Log.w("GenericCategory.save()", "Category not saved to database because context is null");
		}
	}
	/**
	 * Returns true if this.fields == other.fields, and
	 * this.name = other.name
	 */
	@Override
	public boolean equals(Object other){
		if(other == this) return true;
		if(other == null || !(other instanceof GenericCategory)) return false;
		GenericCategory cp= GenericCategory.class.cast(other);
		return this.name.equals(cp.name) && this.fields.equals(cp.fields);
	}
	public int hashCode(){
		return this.name.hashCode();
	}
}
