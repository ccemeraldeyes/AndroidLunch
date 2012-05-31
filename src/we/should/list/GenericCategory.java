package we.should.list;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import we.should.database.WSdb;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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


	
	public GenericCategory(String name, List<Field> fields, Context ctx) {
		super(name, fields, ctx);
		//checkRep();
	}
	protected GenericCategory(String name, JSONArray a, Context ctx) throws JSONException{
		super(name, a, ctx);
		//checkRep();
	}
	/**
	 * Checks the representation invariant.
	 * Throws an assertion error if it is violated.
	 */
	private void checkRep(){
		for(Item i : this.items){
			assert(i.getCategory() == this);
		}
	}

	@Override
	public List<Item> getItems() {
		if (!sync && ctx != null && id != 0) {
			Map<Integer, JSONObject> itemData = getItemData();
			for (int i : itemData.keySet()){
				Item nIt = this.newItem();
				try {
					nIt.DBtoData(itemData.get(i));
					if(!this.items.contains(nIt)) this.items.add(nIt);
				} catch (JSONException e) {
					Log.w("GenericCategory.getItems()", "Couldn't fill data from DB " + itemData.get(i).toString());
				}
				nIt.id = i;
				nIt.added = true;
			}
			sync = true;
		}
		checkRep();
		return this.items;
	}
	@Override
	public Item getItem(int index) {
		List<Item> items = getItems();
		for(Item i : items){
			if(i.getId() == index) return i;
		}
		return null;
	}
	/**
	 * Returns a list of JSONObjects formed from item rows
	 * stored in the DB for this category
	 * @return list of item JSONObjects
	 */
	protected Map<Integer, JSONObject> getItemData(){
		Map<Integer, JSONObject> out = new HashMap<Integer, JSONObject>();
		WSdb db = new WSdb(ctx);
		db.open();
		Cursor cur = db.getItemsOfCategory(this.id);
		while (cur.moveToNext()) {
			JSONObject data = null;
			try {
				data = new JSONObject(cur.getString(3));
				out.put(cur.getInt(0), data);
			} catch (JSONException e) {
				Log.e("GenericCategory.getItems()", "Database data string improperly formatted");
			}
		}
		cur.close();
		db.close();
		return out;
	}


	@Override
	public Item newItem() {
		Item i = new GenericItem(this, ctx);
		sync = false;
		return i;
	}
	
	@Override
	protected boolean addItem(Item i) {
		assert(i.getCategory()==this);
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
				try {
					this.id = (int) db.insertCategory(this.name, this.color.toString(),
							fieldsToDB().toString());
				} catch (SQLiteConstraintException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Invalid Category Parameters!");
				}
			} else {
				db.updateCategory(this.id, this.name, this.color.toString(), fieldsToDB().toString());
			}
			db.close();
		} else {
			Log.w("GenericCategory.save()", "Category not saved to database because context is null");
		}
	}
	@Override
	public void delete(){
		if(ctx != null && this.id != 0) {
			WSdb db = new WSdb(ctx);
			db.open();
			for(Item i : this.items){
				i.delete();
			}
			db.deleteCategory(this.id);
			db.close();
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
	
	@Override
	public int hashCode(){
		return this.name.hashCode();
	}
}
