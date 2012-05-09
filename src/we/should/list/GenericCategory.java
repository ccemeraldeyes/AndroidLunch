package we.should.list;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.database.WSdb;
import android.content.Context;
import android.database.Cursor;

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
	
	
	public GenericCategory(String name, List<Field> fields) {
		super(name, fields);
		items = new LinkedList<Item>();
		checkRep();
		
	}
	protected GenericCategory(String name, JSONArray a) throws JSONException{
		super(name, a);
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
		if (c == null){
			throw new IllegalStateException("Category Has not yet been saved!");
		}
		if (!sync) {
			WSdb db = new WSdb(c);
			db.open();
			Cursor cur = db.getItemsOfCategory(this.id);
			while (cur.moveToNext()) {
				String name = cur.getString(1);
				JSONObject data = null;
				try {
					data = new JSONObject(cur.getString(4));
					GenericItem nIt = new GenericItem(this);
					nIt.DBtoData(data);
					if(!this.items.contains(nIt)) this.items.add(nIt);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			db.close();
			sync = true;
		}
		return this.items;
	}

	@Override
	public Item newItem() {
		Item i = new GenericItem(this);
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
	public void save(Context c) {
		WSdb db = new WSdb(c);
		db.open();
		if(this.c == null){
			this.id = (int) db.insertCategory(this.name, this.color, fieldsToDB().toString());
			this.c = c;
		} else {
			db.updateCategoryColor(this.id, this.color);
			db.updateCategoryName(this.id, this.name);
		}
		db.close();
		
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
